package com.virtix.nastapaint.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.net.Uri;
import java.io.InputStream;

public class MangaToolkitEngine {

    private final Context context;

    public MangaToolkitEngine(Context context) {
        this.context = context;
    }

    /**
     * Importe une image depuis la galerie sans AUCUN risque de crash mémoire (OOM).
     */
    public Bitmap safelyImportImage(Uri imageUri, int maxWidth, int maxHeight) {
        try {
            InputStream input = context.getContentResolver().openInputStream(imageUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, options);
            if (input != null) input.close();

            // Calcul du facteur de réduction pour préserver la RAM
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            InputStream secondInput = context.getContentResolver().openInputStream(imageUri);
            Bitmap importedBitmap = BitmapFactory.decodeStream(secondInput, null, options);
            if (secondInput != null) secondInput.close();

            return importedBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Évite tout crash brutal de l'application
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * Génère des lignes de vitesse manga convergentes vers un point d'impact.
     */
    public void drawRadialSpeedlines(Canvas canvas, PointF centerPoint, int lineCount, float innerRadius, float outerRadius) {
        Paint linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(3f);
        linePaint.setAntiAlias(true);

        double angleStep = (2 * Math.PI) / lineCount;

        for (int i = 0; i < lineCount; i++) {
            double angle = i * angleStep;
            
            // Point de départ (hors de la zone d'exclusion centrale)
            float startX = (float) (centerPoint.x + Math.cos(angle) * innerRadius);
            float startY = (float) (centerPoint.y + Math.sin(angle) * innerRadius);

            // Point d'arrivée (bord de l'action)
            float endX = (float) (centerPoint.x + Math.cos(angle) * outerRadius);
            float endY = (float) (centerPoint.y + Math.sin(angle) * outerRadius);

            canvas.drawLine(startX, startY, endX, endY, linePaint);
        }
    }

    /**
     * Applique une trame de pointillisme manga (Screentone) sur une zone donnée.
     */
    public void drawHalftoneTone(Canvas canvas, Path targetArea, int dotSpacing, int dotRadius) {
        Paint dotPaint = new Paint();
        dotPaint.setColor(Color.BLACK);
        dotPaint.setAntiAlias(true);
        dotPaint.setStyle(Paint.Style.FILL);

        canvas.save();
        canvas.clipPath(targetArea); // Découpe la trame uniquement dans la zone ciblée

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        for (int x = 0; x < width; x += dotSpacing) {
            for (int y = 0; y < height; y += dotSpacing) {
                canvas.drawCircle(x, y, dotRadius, dotPaint);
            }
        }

        canvas.restore();
    }
}
