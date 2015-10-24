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

        int startAngle = 0;
        int prevAngle = startAngle;


        Path segmentPath = new Path();
        double start = Math.toRadians(startAngle);

        segmentPath.moveTo((float) (centerX + radiusInside * Math.cos(start)), (float) (centerY + radiusInside * Math.sin(start)));

        for (int i = 0; i < NUMBER_OF_CATEGORIES-20; i++) {
            Log.d(TAG, "adjusted " + i + ": " + adjustedValuesByTens[i]);
            prevAngle = prevAngle + sweepAngle;
            radiusOutside = 50 + adjustedValuesByTens[i];
            sweepAngle = 10;

            outerRect = new RectF(centerX - radiusOutside, centerY - radiusOutside, centerX + radiusOutside, centerY + radiusOutside);
            segmentPath.arcTo(outerRect, prevAngle, sweepAngle);
        }

        int currentAngle = startAngle + prevAngle + sweepAngle;

        //double end = Math.toRadians(prevAngle + sweepAngle);
        //segmentPath.lineTo((float)(centerX + radiusInside * Math.cos(end)), (float)(centerY + radiusInside * Math.sin(end)));
        segmentPath.arcTo(innerRect, currentAngle, -currentAngle);

        canvas.drawPath(segmentPath, paint);
    }


    private static final float CIRCLE_LIMIT = 359.9999f;
    /** FROM http://stackoverflow.com/questions/3874424/android-looking-for-a-drawarc-method-with-inner-outer-radius
     * Draws a thick arc between the defined angles, see {@link Canvas#drawArc} for more.
     * This method is equivalent to
     * <pre><code>
     * float rMid = (rInn + rOut) / 2;
     * paint.setStyle(Style.STROKE); // there's nothing to fill
     * paint.setStrokeWidth(rOut - rInn); // thickness
     * canvas.drawArc(new RectF(cx - rMid, cy - rMid, cx + rMid, cy + rMid), startAngle, sweepAngle, false, paint);
     * </code></pre>
     * but supports different fill and stroke paints.
     *
     * @param canvas
     * @param cx horizontal middle point of the oval
     * @param cy vertical middle point of the oval
     * @param rInn inner radius of the arc segment
     * @param rOut outer radius of the arc segment
     * @param startAngle see {@link Canvas#drawArc}
     * @param sweepAngle see {@link Canvas#drawArc}, capped at &plusmn;360
     * @param fill filling paint, can be <code>null</code>
     * @param stroke stroke paint, can be <code>null</code>
     * @see Canvas#drawArc
     */
    public static void drawArcSegment(Canvas canvas, float cx, float cy, float rInn, float rOut, float startAngle,
                                      float sweepAngle, Paint fill, Paint stroke) {
        if (sweepAngle > CIRCLE_LIMIT) {
            sweepAngle = CIRCLE_LIMIT;
        }
        if (sweepAngle < -CIRCLE_LIMIT) {
            sweepAngle = -CIRCLE_LIMIT;
        }

        RectF outerRect = new RectF(cx - rOut, cy - rOut, cx + rOut, cy + rOut);
        RectF innerRect = new RectF(cx - rInn, cy - rInn, cx + rInn, cy + rInn);

        Path segmentPath = new Path();
        double start = Math.toRadians(startAngle);
        segmentPath.moveTo((float)(cx + rInn * Math.cos(start)), (float)(cy + rInn * Math.sin(start)));
        segmentPath.lineTo((float)(cx + rOut * Math.cos(start)), (float)(cy + rOut * Math.sin(start)));
        segmentPath.arcTo(outerRect, startAngle, sweepAngle);
        double end = Math.toRadians(startAngle + sweepAngle);
        segmentPath.lineTo((float)(cx + rInn * Math.cos(end)), (float)(cy + rInn * Math.sin(end)));
        segmentPath.arcTo(innerRect, startAngle + sweepAngle, -sweepAngle);
        if (fill != null) {
            canvas.drawPath(segmentPath, fill);
        }
        if (stroke != null) {
            canvas.drawPath(segmentPath, stroke);
        }
    }


}
