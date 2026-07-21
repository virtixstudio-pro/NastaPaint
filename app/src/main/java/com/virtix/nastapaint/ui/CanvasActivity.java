package com.virtix.nastapaint.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.virtix.nastapaint.R;
import com.virtix.nastapaint.ui.views.MangaCanvasView;

public class CanvasActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;
    private MangaCanvasView mangaCanvasView;
    private View layerPanelView;
    private View toolbarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        mangaCanvasView = findViewById(R.id.mangaCanvasView);
        layerPanelView = findViewById(R.id.layerPanelView);
        toolbarView = findViewById(R.id.toolbarView);
    }

    public void openGalleryForImport() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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
