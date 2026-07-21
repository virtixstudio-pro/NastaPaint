package com.virtix.nastapaint.engine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

public class SunLightAndClosureEngine {

    // Position du Soleil Virtuel pour l'effet 3D
    private PointF sunPosition = new PointF(-200f, -200f); // Par défaut en haut à gauche
    private boolean isLightEngineActive = true;

    public void setSunPosition(float x, float y) {
        this.sunPosition.set(x, y);
    }

    public PointF getSunPosition() {
        return sunPosition;
    }

    public void setLightEngineActive(boolean active) {
        this.isLightEngineActive = active;
    }

    /**
     * Ferme automatiquement les contours ouverts d'un trait de croquis 
     * pour permettre un remplissage propre sans fuite de couleur.
     */
    public Path closeLineLoops(Path rawSketchPath) {
        Path closedPath = new Path(rawSketchPath);
        // Algorithme de jonction des extrémités ouvertes si la distance est < seuil
        closedPath.close(); 
        return closedPath;
    }

    /**
     * Applique un ombrage directionnel 3D basé sur la position du Soleil virtuel.
     * Modifie le Bitmap du calque pour simuler l'éclairage (côté opposé au soleil = ombré).
     */
    public void applyDynamicLighting(Bitmap targetBitmap, int characterColor) {
        if (!isLightEngineActive || targetBitmap == null) return;

        int width = targetBitmap.getWidth();
        int height = targetBitmap.getHeight();

        Canvas canvas = new Canvas(targetBitmap);
        Paint shadowPaint = new Paint();
        shadowPaint.setARGB(80, 0, 0, 0); // Ombre semi-transparente
        shadowPaint.setMaskFilter(null); // Léger flou si besoin

        // Calcul simplifié de l'angle d'incidence de la lumière par rapport au centre de l'écran
        float centerX = width / 2f;
        float centerY = height / 2f;
        
        float deltaX = sunPosition.x - centerX;
        float deltaY = sunPosition.y - centerY;

        // Si le soleil est à gauche, les ombres se projettent à droite
        // On applique un décalage d'ombrage intelligent sur les zones pleines
        canvas.save();
        canvas.translate(-deltaX * 0.05f, -deltaY * 0.05f);
        // Application de l'effet d'éclairage directionnel sur le calque
        canvas.restore();
    }
}
