package com.virtix.nastapaint.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.virtix.nastapaint.R;
import com.virtix.nastapaint.core.MangaCanvasView;
import com.virtix.nastapaint.tools.ToolManager;
import com.virtix.nastapaint.ui.views.LayerPanelView;
import com.virtix.nastapaint.ui.views.ToolbarView;

import java.io.InputStream;

public class CanvasActivity extends AppCompatActivity {

    private MangaCanvasView mangaCanvasView;
    private ToolbarView toolbarView;
    private LayerPanelView layerPanelView;
    private ToolManager toolManager;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try (InputStream inputStream = getContentResolver().openInputStream(selectedImageUri)) {
                            Bitmap importedBitmap = BitmapFactory.decodeStream(inputStream);
                            if (importedBitmap != null && mangaCanvasView != null) {
                                mangaCanvasView.importBitmap(importedBitmap);
                                if (layerPanelView != null) {
                                    layerPanelView.refresh();
                                }
                                Toast.makeText(this, "Image importée avec succès", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Erreur lors de l'importation", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        mangaCanvasView = findViewById(R.id.mangaCanvasView);
        toolbarView = findViewById(R.id.toolbarView);
        layerPanelView = findViewById(R.id.layerPanelView);

        toolManager = new ToolManager();
        
        // Configuration de la Toolbar
        toolbarView.setToolManager(toolManager);
        toolbarView.setCanvasView(mangaCanvasView);

        // Connexion du panneau de calques
        if (layerPanelView != null && mangaCanvasView != null) {
            layerPanelView.setLayerManager(mangaCanvasView.getLayerManager());
            layerPanelView.setCanvasView(mangaCanvasView);
            layerPanelView.refresh();
        }

        // Ecouteurs d'actions personnalisés de la Toolbar
        toolbarView.setOnToolbarActionListener(new ToolbarView.OnToolbarActionListener() {
            @Override
            public void onImportImageRequested() {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            }

            @Override
            public void onToggleLayersRequested() {
                if (layerPanelView != null) {
                    if (layerPanelView.getVisibility() == View.VISIBLE) {
                        layerPanelView.setVisibility(View.GONE);
                    } else {
                        layerPanelView.setVisibility(View.VISIBLE);
                        layerPanelView.refresh();
                    }
                }
            }
        });

        toolManager.applyToCanvas(mangaCanvasView);
        enableFullScreenImmersive();
    }

    private void enableFullScreenImmersive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE);
            }
        } else {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_FULLSCREEN
              | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_FULLSCREEN
              | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }
}
