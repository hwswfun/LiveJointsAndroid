package com.livejoints;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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


    public CircleChart(Context context) {
        super(context);
        paint = new Paint();
    }

    public CircleChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.CYAN);
        setBackgroundColor(Color.TRANSPARENT);
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






    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        Log.d(TAG,"onDraw()");
        //drawArcSegment(canvas, getWidth() / 2, getHeight() / 2, 50.0f, 130.0f, 10.0f, 10.0f, paint, null);


        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radiusOutside = 150;
        int radiusInside = 1;
        int sweepAngle = 10;

        RectF outerRect; // = new RectF(centerX - radiusOutside, centerY - radiusOutside, centerX + radiusOutside, centerY + radiusOutside);
        RectF innerRect = new RectF(centerX - radiusInside, centerY - radiusInside, centerX + radiusInside, centerY + radiusInside);

        int startAngle = 270;
        int prevAngle = startAngle;


        Path segmentPath = new Path();
        double start = Math.toRadians(startAngle);

        segmentPath.moveTo((float) (centerX + radiusInside * Math.cos(start)), (float) (centerY + radiusInside * Math.sin(start)));

        for (int i = 0; i < NUMBER_OF_CATEGORIES-20; i++) {
            Log.d(TAG, "adjusted " + i + ": " + adjustedValuesByTens[i]);

            radiusOutside = 60 + adjustedValuesByTens[i];
            sweepAngle = 10;

            outerRect = new RectF(centerX - radiusOutside, centerY - radiusOutside, centerX + radiusOutside, centerY + radiusOutside);
            segmentPath.arcTo(outerRect, prevAngle, sweepAngle);
            prevAngle = prevAngle + sweepAngle;
        }

        int currentAngle = startAngle + prevAngle + sweepAngle;

        //double end = Math.toRadians(prevAngle + sweepAngle);
        //segmentPath.lineTo((float)(centerX + radiusInside * Math.cos(end)), (float)(centerY + radiusInside * Math.sin(end)));
        segmentPath.arcTo(innerRect, currentAngle, -currentAngle);

        canvas.drawPath(segmentPath, paint);
    }



}
