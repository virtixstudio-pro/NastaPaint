package com.virtix.nastapaint.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.virtix.nastapaint.R;
import com.virtix.nastapaint.core.MangaCanvasView;
import com.virtix.nastapaint.tools.ToolManager;

public class ToolbarView extends LinearLayout {

    private ToolManager toolManager;
    private MangaCanvasView canvasView;

    private Button penButton;
    private Button eraserButton;
    private Button clearButton;
    private Button decreaseSizeButton;
    private Button increaseSizeButton;
    private TextView sizeText;
    private Button undoButton;
    private Button redoButton;

    public ToolbarView(Context context) {
        super(context);
        init(context);
    }

    public ToolbarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_toolbar, this, true);

        penButton = findViewById(R.id.btn_pen);
        eraserButton = findViewById(R.id.btn_eraser);
        clearButton = findViewById(R.id.btn_clear);
        decreaseSizeButton = findViewById(R.id.btn_size_minus);
        increaseSizeButton = findViewById(R.id.btn_size_plus);
        sizeText = findViewById(R.id.txt_size);
        undoButton = findViewById(R.id.btn_undo);
        redoButton = findViewById(R.id.btn_redo);

        penButton.setOnClickListener(v -> setTool(ToolManager.Tool.PEN));
        eraserButton.setOnClickListener(v -> setTool(ToolManager.Tool.ERASER));
        clearButton.setOnClickListener(v -> setTool(ToolManager.Tool.CLEAR));

        decreaseSizeButton.setOnClickListener(v -> adjustStrokeWidth(-1f));
        increaseSizeButton.setOnClickListener(v -> adjustStrokeWidth(1f));

        undoButton.setOnClickListener(v -> {
            if (canvasView != null) {
                canvasView.undo();
            }
        });

        redoButton.setOnClickListener(v -> {
            if (canvasView != null) {
                canvasView.redo();
            }
        });

        updateSizeDisplay(6f);
    }

    public void setToolManager(ToolManager manager) {
        this.toolManager = manager;
    }

    public void setCanvasView(MangaCanvasView canvasView) {
        this.canvasView = canvasView;
    }

    private void setTool(ToolManager.Tool tool) {
        if (toolManager == null || canvasView == null) return;
        toolManager.setActiveTool(tool);
        toolManager.applyToCanvas(canvasView);
        updateToolButtons(tool);
    }

    private void adjustStrokeWidth(float delta) {
        if (toolManager == null || canvasView == null) return;
        float newWidth = toolManager.getStrokeWidth() + delta;
        toolManager.setStrokeWidth(newWidth);
        toolManager.applyToCanvas(canvasView);
        updateSizeDisplay(toolManager.getStrokeWidth());
    }

    private void updateSizeDisplay(float width) {
        sizeText.setText(String.format("%.0f", width));
    }

    private void updateToolButtons(ToolManager.Tool active) {
        penButton.setSelected(active == ToolManager.Tool.PEN);
        eraserButton.setSelected(active == ToolManager.Tool.ERASER);
    }
}
