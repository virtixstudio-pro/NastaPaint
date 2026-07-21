package com.virtix.nastapaint;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.virtix.nastapaint.core.MangaCanvasView;
import com.virtix.nastapaint.ui.views.LayerPanelView;
import com.virtix.nastapaint.ui.views.ToolbarView;
import com.virtix.nastapaint.utils.ExportManager;

public class CanvasActivity extends AppCompatActivity {

    private MangaCanvasView canvasView;
    private LayerPanelView layerPanelView;
    private ToolbarView toolbarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        canvasView = findViewById(R.id.mangaCanvasView);
        layerPanelView = findViewById(R.id.layerPanelView);
        toolbarView = findViewById(R.id.toolbarView);

        // Connexion entre la vue du Canvas et le gestionnaire de calques
        layerPanelView.setLayerManager(canvasView.getLayerManager());
        layerPanelView.setCanvasView(canvasView);
        layerPanelView.refresh();

        // Ajout d'un bouton d'exportation dynamique sur l'UI principale
        Button btnSave = new Button(this);
        btnSave.setText("💾 Export PNG");
        btnSave.setOnClickListener(v -> {
            ExportManager.saveToGallery(
                CanvasActivity.this, 
                canvasView.getLayerManager(), 
                canvasView.getWidth(), 
                canvasView.getHeight()
            );
        });

        toolbarView.addView(btnSave);
    }
}
