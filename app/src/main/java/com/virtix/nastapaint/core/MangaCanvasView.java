package com.virtix.nastapaint.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.List;

public class MangaCanvasView extends View {

    private LayerManager layerManager;
    private Paint layerPaint;
    private Paint drawPaint;
    private boolean eraserMode = false;

    private Path currentPath;
    private final PointF lastPoint = new PointF();

    private ScaleGestureDetector scaleGestureDetector;
    private boolean isZoomingOrPanning = false;
    private float scaleFactor = 1.0f;
    private float translateX = 0f, translateY = 0f;
    private float focusX, focusY;
    private int activePointerId = -1;

    private final UndoRedoManager undoRedoManager = new UndoRedoManager();

    public MangaCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MangaCanvasView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setLayerType(LAYER_TYPE_HARDWARE, null);

        drawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStrokeWidth(6f);
        drawPaint.setColor(0xFF000000);
        drawPaint.setDither(false);

        currentPath = new Path();

        layerPaint = new Paint();
        layerPaint.setFilterBitmap(false);

        layerManager = new LayerManager();

        setupGestureDetector(context);
    }

    private void setupGestureDetector(Context context) {
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                isZoomingOrPanning = true;
                focusX = detector.getFocusX();
                focusY = detector.getFocusY();
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float newScale = scaleFactor * detector.getScaleFactor();
                if (newScale >= 0.1f && newScale <= 5.0f) {
                    scaleFactor = newScale;
                    translateX += (detector.getFocusX() - focusX) / scaleFactor;
                    translateY += (detector.getFocusY() - focusY) / scaleFactor;
                }
                focusX = detector.getFocusX();
                focusY = detector.getFocusY();
                invalidate();
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                isZoomingOrPanning = false;
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0 && (w != oldw || h != oldh)) {
            layerManager.setCanvasSize(w, h);
            undoRedoManager.saveState(layerManager.getActiveLayer().bitmap);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(translateX, translateY);
        canvas.scale(scaleFactor, scaleFactor);

        canvas.drawColor(0xFFFFFFFF);

        List<Layer> layers = layerManager.getLayers();
        for (Layer layer : layers) {
            if (layer.isVisible) {
                layerPaint.setAlpha((int)(255 * layer.opacity));
                canvas.drawBitmap(layer.bitmap, 0, 0, layerPaint);
            }
        }

        // Dessiner le tracé en cours uniquement si ce n'est pas la gomme (la gomme s'exécute directement sur le bitmap)
        if (!currentPath.isEmpty() && !eraserMode) {
            canvas.drawPath(currentPath, drawPaint);
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);

        if (isZoomingOrPanning) {
            if (event.getPointerCount() == 2) {
                if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                    float dx = event.getX() - lastPoint.x;
                    float dy = event.getY() - lastPoint.y;
                    translateX += dx;
                    translateY += dy;
                    lastPoint.set(event.getX(), event.getY());
                    invalidate();
                } else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                    lastPoint.set(event.getX(), event.getY());
                }
                return true;
            }
            if (event.getActionMasked() == MotionEvent.ACTION_UP ||
                event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
                isZoomingOrPanning = false;
                return true;
            }
            return true;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (layerManager.getActiveLayer().locked) return true;
                currentPath.reset();
                float x = event.getX();
                float y = event.getY();
                currentPath.moveTo(toCanvasX(x), toCanvasY(y));
                lastPoint.set(x, y);
                activePointerId = event.getPointerId(0);
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                if (activePointerId == -1 || layerManager.getActiveLayer().locked) break;
                int pointerIndex = event.findPointerIndex(activePointerId);
                if (pointerIndex == -1) break;
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);
                float midX = (lastPoint.x + newX) / 2f;
                float midY = (lastPoint.y + newY) / 2f;
                currentPath.quadTo(
                    toCanvasX(lastPoint.x), toCanvasY(lastPoint.y),
                    toCanvasX(midX), toCanvasY(midY)
                );
                
                // Si gomme active, effacer directement le bitmap au fil du mouvement
                if (eraserMode) {
                    Canvas layerCanvas = new Canvas(layerManager.getActiveLayer().bitmap);
                    layerCanvas.drawPath(currentPath, drawPaint);
                }
                
                lastPoint.set(newX, newY);
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (activePointerId != -1 && !currentPath.isEmpty() && !layerManager.getActiveLayer().locked) {
                    currentPath.lineTo(toCanvasX(lastPoint.x), toCanvasY(lastPoint.y));
                    Canvas layerCanvas = new Canvas(layerManager.getActiveLayer().bitmap);
                    layerCanvas.drawPath(currentPath, drawPaint);
                    currentPath.reset();
                    undoRedoManager.saveState(layerManager.getActiveLayer().bitmap);
                }
                activePointerId = -1;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private float toCanvasX(float viewX) {
        return (viewX - translateX) / scaleFactor;
    }

    private float toCanvasY(float viewY) {
        return (viewY - translateY) / scaleFactor;
    }

    public void setStrokeColor(int color) {
        drawPaint.setColor(color);
    }

    public void setStrokeWidth(float width) {
        drawPaint.setStrokeWidth(width);
    }

    public void setEraserMode(boolean eraser) {
        eraserMode = eraser;
        if (eraser) {
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            drawPaint.setColor(0x00000000);
        } else {
            drawPaint.setXfermode(null);
            drawPaint.setColor(0xFF000000);
        }
    }

    public void clearCanvas() {
        if (layerManager.getActiveLayer().locked) return;
        Canvas c = new Canvas(layerManager.getActiveLayer().bitmap);
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        undoRedoManager.saveState(layerManager.getActiveLayer().bitmap);
        invalidate();
    }

    public void undo() {
        if (undoRedoManager.canUndo()) {
            undoRedoManager.undo(layerManager.getActiveLayer().bitmap, null);
            invalidate();
        }
    }

    public void redo() {
        if (undoRedoManager.canRedo()) {
            undoRedoManager.redo(layerManager.getActiveLayer().bitmap, null);
            invalidate();
        }
    }

    public LayerManager getLayerManager() {
        return layerManager;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        undoRedoManager.release();
        for (Layer layer : layerManager.getLayers()) {
            if (layer.bitmap != null && !layer.bitmap.isRecycled()) {
                layer.bitmap.recycle();
            }
        }
    }
}
