package com.virtix.nastapaint.tools;

import com.virtix.nastapaint.core.MangaCanvasView;

public class ToolManager {

    public enum Tool {
        PEN,
        ERASER,
        CLEAR
    }

    private Tool activeTool = Tool.PEN;
    private float strokeWidth = 6f;
    private int strokeColor = 0xFF000000;

    public void setActiveTool(Tool tool) {
        this.activeTool = tool;
    }

    public Tool getActiveTool() {
        return activeTool;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float width) {
        this.strokeWidth = Math.max(1f, Math.min(100f, width));
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int color) {
        this.strokeColor = color;
    }

    public void applyToCanvas(MangaCanvasView canvasView) {
        if (canvasView == null) return;

        switch (activeTool) {
            case PEN:
                canvasView.setEraserMode(false);
                canvasView.setStrokeColor(strokeColor);
                canvasView.setStrokeWidth(strokeWidth);
                break;
            case ERASER:
                canvasView.setEraserMode(true);
                canvasView.setStrokeWidth(strokeWidth * 2);
                break;
            case CLEAR:
                canvasView.clearCanvas();
                activeTool = Tool.PEN;
                applyToCanvas(canvasView);
                break;
        }
    }
}
