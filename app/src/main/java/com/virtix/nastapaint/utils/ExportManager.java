package com.virtix.nastapaint.utils;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.virtix.nastapaint.core.Layer;
import com.virtix.nastapaint.core.LayerManager;

import java.io.OutputStream;
import java.util.List;

public class ExportManager {

    public static Bitmap mergeLayers(LayerManager layerManager, int width, int height) {
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();

        // Fond blanc par défaut
        canvas.drawColor(0xFFFFFFFF);

        List<Layer> layers = layerManager.getLayers();
        for (Layer layer : layers) {
            if (layer.isVisible && layer.bitmap != null && !layer.bitmap.isRecycled()) {
                paint.setAlpha((int) (255 * layer.opacity));
                canvas.drawBitmap(layer.bitmap, 0, 0, paint);
            }
        }
        return result;
    }

    public static void saveToGallery(Context context, LayerManager layerManager, int width, int height) {
        Bitmap finalImage = mergeLayers(layerManager, width, height);
        String filename = "NastaPaint_" + System.currentTimeMillis() + ".png";

        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/NastaPaint");

                Uri imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                fos = context.getContentResolver().openOutputStream(imageUri);
            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                java.io.File image = new java.io.File(imagesDir, filename);
                fos = new java.io.FileOutputStream(image);
            }

            finalImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            if (fos != null) {
                fos.close();
            }

            Toast.makeText(context, "Image enregistrée dans la Galerie !", Toast.LENGTH_SHORT).show();
            finalImage.recycle();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Erreur lors de la sauvegarde : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
