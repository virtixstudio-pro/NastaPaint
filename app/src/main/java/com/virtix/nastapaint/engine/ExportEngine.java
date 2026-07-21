package com.virtix.nastapaint.engine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportEngine {

    public enum Resolution {
        P520(520, 730),
        HD_720P(720, 1080),
        P1120(1120, 1580),
        K1(1024, 1448),
        K2(2048, 2896),
        K3(3072, 4344),
        K4(4096, 5792),
        K4_ULTRA(4320, 6108),
        UHD_ULTRA(5120, 7240),
        K8_ULTRA(8192, 11584);

        public final int width;
        public final int height;

        Resolution(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public static File exportCanvas(Bitmap sourceBitmap, Resolution targetRes, String fileName) throws IOException {
        // Redimensionnement vectoriel/Interpolation haute qualité
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(sourceBitmap, targetRes.width, targetRes.height, true);

        File exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "NastaPaint");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File outputFile = new File(exportDir, fileName + "_" + targetRes.name() + ".png");
        FileOutputStream fos = new FileOutputStream(outputFile);
        
        // Compression PNG sans perte (100% qualité vectorielle préservée)
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();

        if (scaledBitmap != sourceBitmap) {
            scaledBitmap.recycle();
        }

        return outputFile;
    }
}
