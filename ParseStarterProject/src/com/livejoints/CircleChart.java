package com.livejoints;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by nathantofte on 10/20/15.
 */
public class CircleChart extends ImageView {
    private final static String TAG = CircleChart.class.getSimpleName();

    int valuesByTens[] = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    int adjustedValuesByTens[] = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    long valuesTotal = 0;

    private final int NUMBER_OF_CATEGORIES = 35;

    Paint paint;
    Paint overlayPaint;
    Paint maskoffPaint;

    // chartCanvas is the normal draw from this ImageView.  Instead of drawing directly into the
    // canvas, it is drawn into chartCanvas then masked off using transparencyMaskCanvas
    Bitmap chartBitmap;
    Canvas chartCanvas;

    Bitmap transparencyMaskBitmap;
    Canvas transparencyMaskCanvas;


    public CircleChart(Context context) {
        super(context);
        init();
    }

    public CircleChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    void init() {
        setBackgroundColor(Color.TRANSPARENT);

        paint = new Paint();
        paint.setAntiAlias(true);

        maskoffPaint = new Paint();
        maskoffPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        maskoffPaint.setAntiAlias(true);

        overlayPaint = new Paint();
        overlayPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        overlayPaint.setAntiAlias(true);

        segmentPath = new Path();
    }

int maxRadius=0;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            transparencyMaskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            transparencyMaskCanvas = new Canvas(transparencyMaskBitmap);
            chartBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            chartCanvas = new Canvas(chartBitmap);

            centerX = w / 2;
            centerY = h / 2;

            if (centerX < centerY) {
                maxRadius = centerY;
            } else {
                maxRadius = centerX;
            }
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        // draws the src image for this into the chartCanvas
        super.onDraw(chartCanvas);

        transparencyMaskBitmap.eraseColor(Color.TRANSPARENT);

        // draw the chart as an opaque color with transparent non-chart part.
        drawInsideCircleGraph(transparencyMaskCanvas);

        // non transparent colors in transparencyMaskCanvas allow the original src "chartCanvas" to
        // show through.  any points transparent in transparencyMaskCanvas become transparent.
        chartCanvas.drawBitmap(transparencyMaskBitmap, 0, 0, maskoffPaint);

        canvas.drawBitmap(chartBitmap, 0, 0, overlayPaint);
    }

    RectF outerRect; // outside of arc for chart
    RectF innerRect; // inside of arc for chart
    int centerX = 0;
    int centerY = 0;
    Path segmentPath = null;

    void drawOutsideCircleGraph(Canvas canvas) {

        int radiusOutside;
        int radiusOutsideStart = 70;
        int radiusInside = 40;
        int startAngle = 270;  // because 0 degrees is to the right.  We add 270 to get "0" as top.
        int sweepAngle = 10;

        innerRect = new RectF(centerX - radiusInside, centerY - radiusInside, centerX + radiusInside, centerY + radiusInside);


        int prevAngle = startAngle;

        double start = Math.toRadians(startAngle);

        segmentPath.reset();
        segmentPath.moveTo((float) (centerX + radiusInside * Math.cos(start)), (float) (centerY + radiusInside * Math.sin(start)));

        for (int i = 0; i < NUMBER_OF_CATEGORIES-20; i++) {
            //Log.d(TAG, "adjusted " + i + ": " + adjustedValuesByTens[i]);

            radiusOutside = radiusOutsideStart + adjustedValuesByTens[i];

            outerRect = new RectF(centerX - radiusOutside, centerY - radiusOutside, centerX + radiusOutside, centerY + radiusOutside);
            segmentPath.arcTo(outerRect, prevAngle, sweepAngle);
            prevAngle = prevAngle + sweepAngle;
        }

        segmentPath.arcTo(innerRect, prevAngle, -prevAngle);

        canvas.drawPath(segmentPath, paint);
    }


    void drawInsideCircleGraph(Canvas canvas) {


        int radiusOutside = maxRadius;
        int radiusInsideStart = 70;
        int radiusInside = 40;
        int startAngle = 270;  // because 0 degrees is to the right.  We add 270 to get "0" as top.
        int sweepAngle = 10;

        outerRect = new RectF(centerX - radiusOutside, centerY - radiusOutside, centerX + radiusOutside, centerY + radiusOutside);


        int prevAngle = startAngle;

        double start = Math.toRadians(startAngle);

        segmentPath.reset();
        segmentPath.moveTo((float) (centerX + radiusOutside * Math.cos(start)), (float) (centerY + radiusOutside * Math.sin(start)));

        for (int i = 0; i < NUMBER_OF_CATEGORIES-20; i++) {
            //Log.d(TAG, "adjusted " + i + ": " + adjustedValuesByTens[i]);

            radiusInside = radiusInsideStart + adjustedValuesByTens[i];

            innerRect = new RectF(centerX - radiusInside, centerY - radiusInside, centerX + radiusInside, centerY + radiusInside);
            segmentPath.arcTo(innerRect, prevAngle, sweepAngle);
            prevAngle = prevAngle + sweepAngle;
        }

        segmentPath.arcTo(outerRect, prevAngle, startAngle-prevAngle);

        canvas.drawPath(segmentPath, paint);
    }





    public void addValue(int angle) {
        int index = angle / 10;
        if (index > NUMBER_OF_CATEGORIES) index = NUMBER_OF_CATEGORIES;

        valuesByTens[index]++;
        valuesTotal++;
        alignValues();
        adjustedValues();
        //printAdjustedValues();
        invalidate();

    }


    private void printValues() {
        for (int i = 0; i < NUMBER_OF_CATEGORIES; i++) {
            Log.d(TAG, "" + i + ": " + valuesByTens[i]);
        }
    }

    final long MAX_VALUES_TOTAL = 50000;

    private void alignValues() {
        if (valuesTotal > MAX_VALUES_TOTAL) {
            valuesTotal = valuesTotal / 100;

            for (int i = 0; i < NUMBER_OF_CATEGORIES; i++) {
                valuesByTens[i] = valuesByTens[i] / 100;
            }
        }
    }


    final static int CLIP_VALUE = 100;
    // ok with 100% in each of the 36 categories.  So lets set bar at valuesTotal / 36.  Then get percent form that

    private void adjustedValues() {
        double saturationValue = (double)valuesTotal / (double)NUMBER_OF_CATEGORIES;
        if (saturationValue < 100) saturationValue = 100;

        for (int i = 0; i < NUMBER_OF_CATEGORIES; i++) {
            double currentValue = (double) valuesByTens[i];


            double percent = (currentValue / saturationValue) * 100.0;
            if (percent > CLIP_VALUE) percent = CLIP_VALUE;

            adjustedValuesByTens[i] = (int) percent;
        }

    }

    private void printAdjustedValues() {
        for (int i = 0; i < NUMBER_OF_CATEGORIES; i++) {
            Log.d(TAG, "adjusted " + i + ": " + adjustedValuesByTens[i]);
        }
    }






}


