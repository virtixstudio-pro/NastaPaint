package com.virtix.nastapaint.core;

import android.graphics.Bitmap;

public class Layer {
    public Bitmap bitmap;
    public float opacity = 1f;
    public boolean isVisible = true;
    public boolean locked = false;
    public String name;

    public Layer(Bitmap bitmap, String name) {
        this.bitmap = bitmap;
        this.name = name;
    }
}
