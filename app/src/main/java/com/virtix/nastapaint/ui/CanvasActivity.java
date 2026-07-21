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
    private View dockTools;
    private TextView btnRestoreUi;
    private TextView txtBrushInfo;

    private static final String APP_FOLDER_NAME = "Vabeir";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        mangaCanvasView = findViewById(R.id.mangaCanvasView);
        topMenuBar = findViewById(R.id.top_menu_bar);
        bottomStatusBar = findViewById(R.id.bottom_status_bar);
        dockTools = findViewById(R.id.dock_tools);
        btnRestoreUi = findViewById(R.id.btn_restore_ui);
        txtBrushInfo = findViewById(R.id.txt_brush_info);

        TextView menuFile = findViewById(R.id.menu_file);
        TextView btnFullscreen = findViewById(R.id.btn_fullscreen);

        // Outils
        TextView btnPen = findViewById(R.id.btn_tool_pen);
        TextView btnEraser = findViewById(R.id.btn_tool_eraser);
        TextView btnClear = findViewById(R.id.btn_tool_clear);

        if (menuFile != null) menuFile.setOnClickListener(this::showFileMenu);
        if (btnFullscreen != null) btnFullscreen.setOnClickListener(v -> toggleFullScreen(true));
        if (btnRestoreUi != null) btnRestoreUi.setOnClickListener(v -> toggleFullScreen(false));

        if (btnPen != null) {
            btnPen.setOnClickListener(v -> {
                if (mangaCanvasView != null) mangaCanvasView.setEraser(false);
                if (txtBrushInfo != null) txtBrushInfo.setText("Plume G • Mode Encrage");
                Toast.makeText(this, "Mode Plume", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnEraser != null) {
            btnEraser.setOnClickListener(v -> {
                if (mangaCanvasView != null) mangaCanvasView.setEraser(true);
                if (txtBrushInfo != null) txtBrushInfo.setText("Gomme Active");
                Toast.makeText(this, "Mode Gomme", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnClear != null) {
            btnClear.setOnClickListener(v -> {
                if (mangaCanvasView != null) mangaCanvasView.clearCanvas();
                Toast.makeText(this, "Planche effacée", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void showFileMenu(View anchorView) {
        PopupMenu popup = new PopupMenu(this, anchorView);
        popup.getMenu().add(0, 1, 0, "◄ Quitter vers Accueil");
        popup.getMenu().add(0, 2, 1, "💾 Sauvegarder Projet (.vpx)");
        popup.getMenu().add(0, 3, 2, "🖼 Exporter Image (4K/8K)...");

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
                default:
                    return false;
            }
        });
        popup.show();
    }

    private void saveProjectLocally() {
        if (mangaCanvasView == null || mangaCanvasView.getCanvasBitmap() == null) {
            Toast.makeText(this, "Canevas vide", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File projectsDir = new File(getExternalFilesDir(null), "Projects");
            if (!projectsDir.exists()) projectsDir.mkdirs();

            File file = new File(projectsDir, "project_" + System.currentTimeMillis() + ".vpx");
            FileOutputStream out = new FileOutputStream(file);
            mangaCanvasView.getCanvasBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            Toast.makeText(this, "Sauvegardé : " + file.getName(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur de sauvegarde", Toast.LENGTH_SHORT).show();
        }
    }

    private void showExportOptionsDialog() {
        String[] options = {"Full HD (1080p)", "Ultra HD 4K", "Master Manga 8K"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exportation dans /Pictures/" + APP_FOLDER_NAME);
        builder.setItems(options, (dialog, which) -> exportImageWithResolution(options[which]));
        builder.show();
    }

    private void exportImageWithResolution(String resolution) {
        Bitmap src = mangaCanvasView.getCanvasBitmap();
        if (src == null) return;

        int targetW = src.getWidth();
        int targetH = src.getHeight();

        if (resolution.contains("4K")) { targetW = 3840; targetH = 2160; }
        else if (resolution.contains("8K")) { targetW = 7680; targetH = 4320; }
        else if (resolution.contains("Full HD")) { targetW = 1920; targetH = 1080; }

        try {
            Bitmap scaled = Bitmap.createScaledBitmap(src, targetW, targetH, true);
            File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File appExportDir = new File(picturesDir, APP_FOLDER_NAME);
            if (!appExportDir.exists()) appExportDir.mkdirs();

            File exportFile = new File(appExportDir, "Export_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(exportFile);
            scaled.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            Toast.makeText(this, "Exporté dans " + exportFile.getName(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur export", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleFullScreen(boolean fullScreen) {
        if (fullScreen) {
            topMenuBar.setVisibility(View.GONE);
            bottomStatusBar.setVisibility(View.GONE);
            dockTools.setVisibility(View.GONE);
            btnRestoreUi.setVisibility(View.VISIBLE);

            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            topMenuBar.setVisibility(View.VISIBLE);
            bottomStatusBar.setVisibility(View.VISIBLE);
            dockTools.setVisibility(View.VISIBLE);
            btnRestoreUi.setVisibility(View.GONE);

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }
}
