package com.virtix.nastapaint.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.virtix.nastapaint.R;
import com.virtix.nastapaint.ui.views.MangaCanvasView;

import java.io.File;
import java.io.FileOutputStream;

public class CanvasActivity extends AppCompatActivity {

    private MangaCanvasView mangaCanvasView;
    private View topMenuBar;
    private View bottomStatusBar;
    private TextView btnRestoreUi;

    // Nom officiel du dossier public pour l'application
    private static final String APP_FOLDER_NAME = "Vabeir"; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        mangaCanvasView = findViewById(R.id.mangaCanvasView);
        topMenuBar = findViewById(R.id.top_menu_bar);
        bottomStatusBar = findViewById(R.id.bottom_status_bar);
        btnRestoreUi = findViewById(R.id.btn_restore_ui);

        TextView menuFile = findViewById(R.id.menu_file);
        TextView btnFullscreen = findViewById(R.id.btn_fullscreen);

        if (menuFile != null) {
            menuFile.setOnClickListener(this::showFileMenu);
        }

        if (btnFullscreen != null) {
            btnFullscreen.setOnClickListener(v -> toggleFullScreen(true));
        }

        if (btnRestoreUi != null) {
            btnRestoreUi.setOnClickListener(v -> toggleFullScreen(false));
        }
    }

    private void showFileMenu(View anchorView) {
        PopupMenu popup = new PopupMenu(this, anchorView);
        popup.getMenu().add(0, 1, 0, "◄ Retour au menu principal");
        popup.getMenu().add(0, 2, 1, "💾 Sauvegarder le projet (.vpx)");
        popup.getMenu().add(0, 3, 2, "🖼 Enregistrer l'image (Export 4K/8K)...");
        popup.getMenu().add(0, 4, 3, "📖 Mode Lecteur Manga / Aperçu Planches");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1:
                    finish();
                    return true;
                case 2:
                    saveProjectLocally();
                    return true;
                case 3:
                    showExportOptionsDialog();
                    return true;
                case 4:
                    openMangaReaderPreview();
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    private void saveProjectLocally() {
        if (mangaCanvasView == null || mangaCanvasView.getCanvasBitmap() == null) {
            Toast.makeText(this, "Aucun canevas actif", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Dossier interne dédié aux projets modifiables (.vpx)
            File projectsDir = new File(getExternalFilesDir(null), "Projects");
            if (!projectsDir.exists()) projectsDir.mkdirs();

            String fileName = "project_" + System.currentTimeMillis() + ".vpx";
            File file = new File(projectsDir, fileName);

            FileOutputStream out = new FileOutputStream(file);
            mangaCanvasView.getCanvasBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            Toast.makeText(this, "Projet sauvegardé dans : " + file.getName(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur de sauvegarde", Toast.LENGTH_SHORT).show();
        }
    }

    private void showExportOptionsDialog() {
        String[] options = {"Full HD (1080p)", "Ultra HD (4K - 3840x2160)", "Master Manga (8K - 7680x4320)", "Format Brut Native"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enregistrer dans le dossier " + APP_FOLDER_NAME);
        builder.setItems(options, (dialog, which) -> {
            String selectedResolution = options[which];
            exportImageWithResolution(selectedResolution);
        });
        builder.show();
    }

    private void exportImageWithResolution(String resolution) {
        Bitmap src = mangaCanvasView.getCanvasBitmap();
        if (src == null) {
            Toast.makeText(this, "Erreur : rien à exporter", Toast.LENGTH_SHORT).show();
            return;
        }

        int targetW = src.getWidth();
        int targetH = src.getHeight();

        if (resolution.contains("4K")) {
            targetW = 3840;
            targetH = 2160;
        } else if (resolution.contains("8K")) {
            targetW = 7680;
            targetH = 4320;
        } else if (resolution.contains("Full HD")) {
            targetW = 1920;
            targetH = 1080;
        }

        try {
            Bitmap scaled = Bitmap.createScaledBitmap(src, targetW, targetH, true);
            
            // Création du dossier au nom de l'application dans le stockage public Pictures/
            File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File appExportDir = new File(picturesDir, APP_FOLDER_NAME);
            
            if (!appExportDir.exists()) {
                appExportDir.mkdirs();
            }

            String fileName = "Export_" + resolution.replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + ".png";
            File exportFile = new File(appExportDir, fileName);

            FileOutputStream out = new FileOutputStream(exportFile);
            scaled.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            Toast.makeText(this, "Exporté dans Pictures/" + APP_FOLDER_NAME + "/" + fileName, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Échec de l'exportation : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openMangaReaderPreview() {
        Toast.makeText(this, "Chargement du lecteur de planches...", Toast.LENGTH_SHORT).show();
        // Logique de lecture et d'enchaînement des pages du dossier de projet
    }

    private void toggleFullScreen(boolean fullScreen) {
        if (fullScreen) {
            topMenuBar.setVisibility(View.GONE);
            bottomStatusBar.setVisibility(View.GONE);
            btnRestoreUi.setVisibility(View.VISIBLE);

            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            topMenuBar.setVisibility(View.VISIBLE);
            bottomStatusBar.setVisibility(View.VISIBLE);
            btnRestoreUi.setVisibility(View.GONE);

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }
}
