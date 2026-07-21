package com.virtix.nastapaint.engine;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Path;

public class SmartOpacityTracer {

    private boolean isAssistActive = false;
    private float opacityThreshold = 0.5f; // Seuil d'opacité à détecter

    public SmartOpacityTracer() {}

    public void setAssistActive(boolean active) {
        this.isAssistActive = active;
    }

    public boolean isAssistActive() {
        return isAssistActive;
    }

    /**
     * Analyse le calque modèle (à opacité réduite) autour de la position du stylet/doigt
     * et magnétise/retrace les contours réels de l'image.
     */
    public Path snapToLowOpacityContours(Bitmap sourceBitmap, float currentX, float currentY, float searchRadius) {
        Path snappedPath = new Path();
        snappedPath.moveTo(currentX, currentY);

        if (!isAssistActive || sourceBitmap == null) {
            return snappedPath;
        }

        int width = sourceBitmap.getWidth();
        int height = sourceBitmap.getHeight();

        int startX = Math.max(0, (int) (currentX - searchRadius));
        int startY = Math.max(0, (int) (currentY - searchRadius));
        int endX = Math.min(width - 1, (int) (currentX + searchRadius));
        int endY = Math.min(height - 1, (int) (currentY + searchRadius));

        int bestX = (int) currentX;
        int bestY = (int) currentY;
        int maxEdgeValue = -1;

        // Détection de contour par différence de luminance sur les pixels du calque modèle
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                int pixel = sourceBitmap.getPixel(x, y);
                int alpha = Color.alpha(pixel);

                // Si le pixel fait partie du dessin à faible opacité
                if (alpha > 10 && alpha < 200) {
                    int gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;
                    int contrastEdge = 255 - gray; // Détection des lignes sombres

                    if (contrastEdge > maxEdgeValue) {
                        maxEdgeValue = contrastEdge;
                        bestX = x;
                        bestY = y;
                    }
                }
            }
        }

        snappedPath.lineTo(bestX, bestY);
        return snappedPath;
    }
}
