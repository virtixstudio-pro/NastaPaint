package com.virtix.nastapaint.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.virtix.nastapaint.R;

public class ToolbarView extends LinearLayout {

    private View decreaseSizeButton;
    private View increaseSizeButton;
    private TextView sizeText;

    public ToolbarView(Context context) {
        super(context);
        init(context);
    }

    public ToolbarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ToolbarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        int layoutResId = context.getResources().getIdentifier("view_toolbar", "layout", context.getPackageName());
        if (layoutResId != 0) {
            LayoutInflater.from(context).inflate(layoutResId, this, true);
            
            int btnMinusId = context.getResources().getIdentifier("btn_size_minus", "id", context.getPackageName());
            int btnPlusId = context.getResources().getIdentifier("btn_size_plus", "id", context.getPackageName());
            int txtSizeId = context.getResources().getIdentifier("txt_size", "id", context.getPackageName());

            if (btnMinusId != 0) decreaseSizeButton = findViewById(btnMinusId);
            if (btnPlusId != 0) increaseSizeButton = findViewById(btnPlusId);
            if (txtSizeId != 0) sizeText = findViewById(txtSizeId);
        }
    }
}
