package com.virtix.nastapaint.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MangaCanvasView extends View {

    private Bitmap canvasBitmap;
    private Canvas drawCanvas;
    private Path drawPath;
    private Paint drawPaint;
    private Paint canvasPaint;

    private float lastX, lastY;
    private static final float TOUCH_TOLERANCE = 4f;

    public MangaCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDrawing();
    }

    private void initDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();
        
        // Configuration du pinceau de base (Rendu type Plume G)
        drawPaint.setColor(Color.BLACK);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(8f);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            // Création de la surface de dessin en mémoire HD
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(canvasBitmap);
            drawCanvas.drawColor(Color.WHITE); // Fond blanc de la planche
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvasBitmap != null) {
            canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        }
        // Dessine le trait en cours de tracé en temps réel
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.reset();
                drawPath.moveTo(touchX, touchY);
                lastX = touchX;
                lastY = touchY;
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(touchX - lastX);
                float dy = Math.abs(touchY - lastY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    // Lissage de courbe Bézier pour un trait fluide
                    drawPath.quadTo(lastX, lastY, (touchX + lastX) / 2, (touchY + lastY) / 2);
                    lastX = touchX;
                    lastY = touchY;
                }
                break;

            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX, touchY);
                // Validation du trait sur le bitmap permanent
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;

            default:
                return false;
        }

        invalidate(); // Force la redessin de la vue
        return true;
    }

    public void importBitmap(Bitmap importedBitmap) {
        if (importedBitmap == null) return;
        if (drawCanvas != null) {
            drawCanvas.drawBitmap(importedBitmap, 0, 0, null);
            invalidate();
        }
    }

    public void clearCanvas() {
        if (drawCanvas != null) {
            drawCanvas.drawColor(Color.WHITE);
            invalidate();
        }
    }
}
