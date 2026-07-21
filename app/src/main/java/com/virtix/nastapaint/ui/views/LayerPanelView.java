package com.virtix.nastapaint.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.virtix.nastapaint.core.Layer;
import com.virtix.nastapaint.core.LayerManager;
import com.virtix.nastapaint.core.MangaCanvasView;
import com.virtix.nastapaint.R;

public class LayerPanelView extends LinearLayout {

    private LayerManager layerManager;
    private MangaCanvasView canvasView;
    private LinearLayout layersContainer;

    public LayerPanelView(Context context) {
        super(context);
        init(context);
    }

    public LayerPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_layer_panel, this, true);
        layersContainer = findViewById(R.id.layers_container);

        Button addButton = findViewById(R.id.btn_add_layer);
        addButton.setOnClickListener(v -> {
            if (layerManager != null && canvasView != null) {
                layerManager.addLayer();
                canvasView.invalidate();
                refresh();
            }
        });
    }

    public void setLayerManager(LayerManager manager) {
        this.layerManager = manager;
    }

    public void setCanvasView(MangaCanvasView canvasView) {
        this.canvasView = canvasView;
    }

    public void refresh() {
        if (layerManager == null) return;
        layersContainer.removeAllViews();

        for (int i = 0; i < layerManager.getLayerCount(); i++) {
            final int index = i;
            Layer layer = layerManager.getLayer(i);

            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_layer, layersContainer, false);

            ImageView thumb = itemView.findViewById(R.id.layer_thumb);
            TextView nameText = itemView.findViewById(R.id.layer_name);
            Button btnVis = itemView.findViewById(R.id.btn_visibility);
            Button btnLock = itemView.findViewById(R.id.btn_lock);
            Button btnUp = itemView.findViewById(R.id.btn_up);
            Button btnDown = itemView.findViewById(R.id.btn_down);
            Button btnDel = itemView.findViewById(R.id.btn_delete);

            // Utilisation directe du bitmap sans allocation d'une nouvelle instance scaled (Fix OOM)
            if (layer.bitmap != null && !layer.bitmap.isRecycled()) {
                thumb.setImageBitmap(layer.bitmap);
            }
            
            nameText.setText(layer.name);
            itemView.setSelected(layerManager.getActiveLayerIndex() == index);

            btnVis.setText(layer.isVisible ? "👁" : "🚫");
            btnVis.setOnClickListener(v -> {
                layerManager.toggleLayerVisibility(index);
                canvasView.invalidate();
                refresh();
            });

            btnLock.setText(layer.locked ? "🔒" : "🔓");
            btnLock.setOnClickListener(v -> {
                layerManager.toggleLayerLock(index);
                refresh();
            });

            btnUp.setOnClickListener(v -> {
                if (layerManager.moveUp(index)) {
                    canvasView.invalidate();
                    refresh();
                }
            });

            btnDown.setOnClickListener(v -> {
                if (layerManager.moveDown(index)) {
                    canvasView.invalidate();
                    refresh();
                }
            });

            btnDel.setOnClickListener(v -> {
                if (layerManager.getLayerCount() > 1) {
                    layerManager.removeLayer(index);
                    canvasView.invalidate();
                    refresh();
                }
            });

            itemView.setOnClickListener(v -> {
                layerManager.setActiveLayer(index);
                refresh();
            });

            layersContainer.addView(itemView);
        }
    }
}
