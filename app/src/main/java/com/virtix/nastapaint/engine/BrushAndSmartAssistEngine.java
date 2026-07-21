package com.virtix.nastapaint.engine;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class BrushAndSmartAssistEngine {

    public enum BrushType {
        GPEN_MANGA,
        MARU_PEN,
        SKETCH_PENCIL,
        ERASER_HARD,
        ERASER_SOFT_OPACITY,
        VECTOR_INTERSECTION_ERASER
    }

    // Configuration des cases à cocher pour "Tricher Intelligemment"
    private boolean optionCloseLines = true;      // Fermeture auto des contours
    private boolean optionAutoColorFill = false;  // Remplissage automatique
    private boolean optionContourTracer = false;  // Suivi continu des lignes

    private Paint currentPaint;
    private BrushType currentType = BrushType.GPEN_MANGA;

    public BrushAndSmartAssistEngine() {
        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);
        setBrushType(BrushType.GPEN_MANGA);
    }

    public void setBrushType(BrushType type) {
        this.currentType = type;
        currentPaint.setXfermode(null); // Reset mode effacement

        switch (type) {
            case GPEN_MANGA:
                currentPaint.setColor(Color.BLACK);
                currentPaint.setStrokeCap(Paint.Cap.ROUND);
                currentPaint.setStyle(Paint.Style.STROKE);
                currentPaint.setStrokeWidth(5f);
                break;

            case MARU_PEN:
                currentPaint.setColor(Color.BLACK);
                currentPaint.setStrokeCap(Paint.Cap.SQUARE);
                currentPaint.setStyle(Paint.Style.STROKE);
                currentPaint.setStrokeWidth(2f);
                break;

            case SKETCH_PENCIL:
                currentPaint.setColor(Color.GRAY);
                currentPaint.setAlpha(150);
                currentPaint.setStyle(Paint.Style.STROKE);
                currentPaint.setStrokeWidth(3f);
                break;

            case ERASER_HARD:
                currentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                currentPaint.setStrokeWidth(20f);
                break;

            case ERASER_SOFT_OPACITY:
                currentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                currentPaint.setAlpha(100); // Effacement progressif selon pression
                currentPaint.setStrokeWidth(30f);
                break;
        }
    }

    // Getters / Setters pour les options à cocher de l'algorithme
    public void setOptionCloseLines(boolean enable) { this.optionCloseLines = enable; }
    public boolean isOptionCloseLines() { return optionCloseLines; }

    public void setOptionAutoColorFill(boolean enable) { this.optionAutoColorFill = enable; }
    public boolean isOptionAutoColorFill() { return optionAutoColorFill; }

    public void setOptionContourTracer(boolean enable) { this.optionContourTracer = enable; }
    public boolean isOptionContourTracer() { return optionContourTracer; }

    public Paint getCurrentPaint() {
        return currentPaint;
    }
}
