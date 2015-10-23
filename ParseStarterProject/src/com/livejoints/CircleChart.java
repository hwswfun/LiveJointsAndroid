package com.livejoints;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by nathantofte on 10/20/15.
 */
public class CircleChart extends View {
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

    Paint paint;

    @Override
    protected void onDraw(Canvas canvas) {
       // canvas.drawArc(getLeft(),getTop(),getRight(),getBottom(),40.0f,10.0f,true,paint);


        drawArcSegment(canvas,getWidth()/2, getHeight()/2, 50.0f, 130.0f, 10.0f , 10.0f, paint, null);
        super.onDraw(canvas);
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
