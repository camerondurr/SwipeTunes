package com.example.camer.swipetunes.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import com.example.camer.swipetunes.model.Point;
import com.example.camer.swipetunes.model.Gesture;
import com.example.camer.swipetunes.model.PointCloudRecognizer;

public class DrawingView extends View
{
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFFFF0000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;

    // PDollar
    ArrayList<Point> points = new ArrayList<>();
    ArrayList<Gesture> gestures = new ArrayList<>();
    private int strokeIndex = -1;
    private boolean isMouseDown = false;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing()
    {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // view given size
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);

                // PDollar
                if (strokeIndex == -1) {
                    points = new ArrayList<>();
                }
                isMouseDown = true;
                strokeIndex++;

                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);

                // PDollar
                if (!isMouseDown) {
                    return true;
                }
                points.add(new Point(touchX, touchY, strokeIndex));

                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();

                // PDollar
                isMouseDown = false;

                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setColor(String newColor) {
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public void clear() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public String recognizeGesture() {
        Point[] pointsArray = points.toArray(new Point[points.size()]);
        String gestureName = "unknown";
        Gesture gesture = new Gesture(pointsArray, gestureName);
        String recognizedGestureName = PointCloudRecognizer.Classify(gesture, gestures);
        return recognizedGestureName;
    }

    public void resetStrokeIndex() {
        this.strokeIndex = -1;
    }

    public void addGesture(String gestureName) {
        if (points.size() == 0) {
            return;
        }
        else {
            Point[] pointsArray = points.toArray(new Point[points.size()]);
            Gesture gesture = new Gesture(pointsArray, gestureName);
            gestures.add(gesture);
        }
    }
}
