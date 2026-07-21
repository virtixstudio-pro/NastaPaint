package com.virtix.nastapaint.core;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

public class LayerManager {
    private final List<Layer> layers = new ArrayList<>();
    private int activeLayerIndex = 0;
    private int canvasWidth, canvasHeight;

    public void setCanvasSize(int w, int h) {
        canvasWidth = w;
        canvasHeight = h;
        if (layers.isEmpty()) {
            addDefaultBackgroundLayer();
        }
    }

    private void addDefaultBackgroundLayer() {
        Bitmap bg = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bg);
        c.drawColor(0xFFFFFFFF); // Fond blanc
        Layer background = new Layer(bg, "Fond");
        background.locked = true;
        layers.add(background);
        activeLayerIndex = 0;
    }

    public Layer getActiveLayer() {
        return layers.get(activeLayerIndex);
    }

    public int getActiveLayerIndex() { return activeLayerIndex; }

    public void setActiveLayer(int index) {
        if (index >= 0 && index < layers.size()) {
            activeLayerIndex = index;
        }
    }

    public int getLayerCount() { return layers.size(); }

    public Layer getLayer(int index) { return layers.get(index); }

    public int indexOf(Layer layer) { return layers.indexOf(layer); }

    public void addLayer() {
        Bitmap bitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(android.graphics.Color.TRANSPARENT);
        Layer newLayer = new Layer(bitmap, "Calque " + layers.size());
        layers.add(newLayer);
        activeLayerIndex = layers.size() - 1;
    }

    public void removeLayer(int index) {
        if (layers.size() <= 1) return;
        Layer toRemove = layers.remove(index);
        if (toRemove.bitmap != null && !toRemove.bitmap.isRecycled()) {
            toRemove.bitmap.recycle();
        }
        if (activeLayerIndex >= layers.size()) {
            activeLayerIndex = layers.size() - 1;
        }
    }

    // Monter vers le haut de la pile (augmente l'index)
    public boolean moveUp(int index) {
        if (index < layers.size() - 1) {
            Layer temp = layers.set(index + 1, layers.get(index));
            layers.set(index, temp);
            if (activeLayerIndex == index) activeLayerIndex++;
            else if (activeLayerIndex == index + 1) activeLayerIndex--;
            return true;
        }
        return false;
    }

    // Descendre vers le fond (diminue l'index)
    public boolean moveDown(int index) {
        if (index > 0) {
            Layer temp = layers.set(index - 1, layers.get(index));
            layers.set(index, temp);
            if (activeLayerIndex == index) activeLayerIndex--;
            else if (activeLayerIndex == index - 1) activeLayerIndex++;
            return true;
        }
        return false;
    }

    public void setLayerOpacity(int index, float opacity) {
        layers.get(index).opacity = Math.max(0f, Math.min(1f, opacity));
    }

    public void toggleLayerVisibility(int index) {
        layers.get(index).isVisible = !layers.get(index).isVisible;
    }

    public void toggleLayerLock(int index) {
        layers.get(index).locked = !layers.get(index).locked;
    }

    public List<Layer> getLayers() {
        return layers;
    }
}
