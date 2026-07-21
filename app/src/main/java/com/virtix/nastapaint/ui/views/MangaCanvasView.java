package com.virtix.nastapaint.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class MangaCanvasView extends View {

    private Bitmap canvasBitmap;
    private Canvas drawCanvas;
    private Path drawPath;
    private Paint drawPaint;
    private Paint canvasPaint;

    private Matrix transformMatrix = new Matrix();
    private Matrix inverseMatrix = new Matrix();
    private ScaleGestureDetector scaleDetector;
    private float scaleFactor = 1.0f;
    private float focusX = 0f;
    private float focusY = 0f;

    private float lastTouchX, lastTouchY;
    private boolean isMultiTouch = false;

    public MangaCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        drawPath = new Path();
        drawPaint = new Paint();
        
        drawPaint.setColor(Color.BLACK);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(8f);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0 && canvasBitmap == null) {
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(canvasBitmap);
            drawCanvas.drawColor(Color.WHITE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.concat(transformMatrix);
        
        if (canvasBitmap != null) {
            canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        }
        canvas.drawPath(drawPath, drawPaint);
        
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);

        int pointerCount = event.getPointerCount();
        if (pointerCount > 1) {
            isMultiTouch = true;
            drawPath.reset();
            invalidate();
            return true;
        }

        transformMatrix.invert(inverseMatrix);
        float[] pts = new float[]{event.getX(), event.getY()};
        inverseMatrix.mapPoints(pts);
        float touchX = pts[0];
        float touchY = pts[1];

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMultiTouch = false;
                drawPath.reset();
                drawPath.moveTo(touchX, touchY);
                lastTouchX = touchX;
                lastTouchY = touchY;
                break;

            case MotionEvent.ACTION_MOVE:
                if (!isMultiTouch) {
                    drawPath.quadTo(lastTouchX, lastTouchY, (touchX + lastTouchX) / 2, (touchY + lastTouchY) / 2);
                    lastTouchX = touchX;
                    lastTouchY = touchY;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!isMultiTouch) {
                    drawPath.lineTo(touchX, touchY);
                    if (drawCanvas != null) {
                        drawCanvas.drawPath(drawPath, drawPaint);
                    }
                    drawPath.reset();
                }
                isMultiTouch = false;
                break;
        }

        invalidate();
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 5.0f));

            focusX = detector.getFocusX();
            focusY = detector.getFocusY();

            transformMatrix.setScale(scaleFactor, scaleFactor, focusX, focusY);
            invalidate();
            return true;
        }
    }

    public void setBrushSize(float size) {
        drawPaint.setStrokeWidth(size);
    }

    public void setEraser(boolean isEraser) {
        if (isEraser) {
            drawPaint.setColor(Color.WHITE);
        } else {
            drawPaint.setColor(Color.BLACK);
        }
    }

    public void clearCanvas() {
        if (drawCanvas != null) {
            drawCanvas.drawColor(Color.WHITE);
            invalidate();
        }
    }

    public Bitmap getCanvasBitmap() {
        return canvasBitmap;
    }
}
