package com.virtix.nastapaint.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.virtix.nastapaint.R;
import com.virtix.nastapaint.ui.views.MangaCanvasView;

import java.io.File;
import java.io.FileOutputStream;

public class CanvasActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;
    private MangaCanvasView mangaCanvasView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        mangaCanvasView = findViewById(R.id.mangaCanvasView);

        ImageButton btnBack = findViewById(R.id.btn_back);
        ImageButton btnSave = findViewById(R.id.btn_save);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish()); // Retour à l'écran d'accueil
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveProjectLocally());
        }
    }

    private void saveProjectLocally() {
        if (mangaCanvasView == null || mangaCanvasView.getCanvasBitmap() == null) {
            Toast.makeText(this, "Rien à sauvegarder", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File projectsDir = new File(getFilesDir(), "projects");
            if (!projectsDir.exists()) {
                projectsDir.mkdirs();
            }

            String fileName = "manga_page_" + System.currentTimeMillis() + ".png";
            File file = new File(projectsDir, fileName);

            FileOutputStream out = new FileOutputStream(file);
            mangaCanvasView.getCanvasBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            Toast.makeText(this, "Projet sauvegardé !", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap importedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                if (mangaCanvasView != null) {
                    mangaCanvasView.importBitmap(importedBitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
