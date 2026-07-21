package com.virtix.nastapaint.engine;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class ColorBlendEngine {

    public enum BlendType {
        NORMAL,
        ADD_PLUS,      // Mode 'Plus' (addition lumineuse des couleurs)
        MULTIPLY,      // Mode 'Produit' (idéal pour les ombres manga)
        OVERLAY        // Mode 'Incrustation'
    }

    /**
     * Applique le mode de fusion sur la peinture actuelle du pinceau/calque.
     */
    public static void applyBlendMode(Paint paint, BlendType blendType) {
        switch (blendType) {
            case ADD_PLUS:
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
                break;
            case MULTIPLY:
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
                break;
            case OVERLAY:
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
                break;
            case NORMAL:
            default:
                paint.setXfermode(null);
                break;
        }
    }

    /**
     * Mélange deux couleurs RVB selon un ratio donné (ex: 0.5 = 50% couleurA + 50% couleurB).
     */
    public static int blendTwoColors(int colorA, int colorB, float ratio) {
        float inverseRatio = 1f - ratio;
        float r = (Color.red(colorA) * ratio) + (Color.red(colorB) * inverseRatio);
        float g = (Color.green(colorA) * ratio) + (Color.green(colorB) * inverseRatio);
        float b = (Color.blue(colorA) * ratio) + (Color.blue(colorB) * inverseRatio);
        float a = (Color.alpha(colorA) * ratio) + (Color.alpha(colorB) * inverseRatio);

        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }
}
