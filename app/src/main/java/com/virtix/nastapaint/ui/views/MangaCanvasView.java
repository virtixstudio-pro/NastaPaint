package com.virtix.nastapaint.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MangaCanvasView extends View {

    private Bitmap canvasBitmap;
    private Canvas drawCanvas;
    private Paint bitmapPaint;

    public MangaCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bitmapPaint = new Paint(Paint.DITHER_FLAG);
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
        if (canvasBitmap != null) {
            canvas.drawBitmap(canvasBitmap, 0, 0, bitmapPaint);
        }
    }

    /**
     * Importe un Bitmap externe sur le canevas principal
     */
    public void importBitmap(Bitmap importedBitmap) {
        if (importedBitmap == null) return;

        if (canvasBitmap == null && getWidth() > 0 && getHeight() > 0) {
            canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(canvasBitmap);
        }

        if (drawCanvas != null) {
            // Dessine l'image importée centrée/redimensionnée sur le canevas
            drawCanvas.drawBitmap(importedBitmap, 0, 0, null);
            invalidate(); // Rafraîchit l'affichage
        }
    }
}
