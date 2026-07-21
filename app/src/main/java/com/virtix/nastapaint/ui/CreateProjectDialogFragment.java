package com.virtix.nastapaint.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.virtix.nastapaint.R;

public class CreateProjectDialogFragment extends DialogFragment {

    private EditText edtTitle, edtTomeCount, edtChaptersPerTome, edtPagesPerChapter;
    private RadioGroup radioGroupType;
    private LinearLayout layoutTomeDetails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_project, container, false);

        edtTitle = view.findViewById(R.id.edt_project_title);
        edtTomeCount = view.findViewById(R.id.edt_tome_count);
        edtChaptersPerTome = view.findViewById(R.id.edt_chapters_per_tome);
        edtPagesPerChapter = view.findViewById(R.id.edt_pages_per_chapter);
        radioGroupType = view.findViewById(R.id.radio_group_type);
        layoutTomeDetails = view.findViewById(R.id.layout_tome_details);
        Button btnConfirm = view.findViewById(R.id.btn_confirm_create);

        radioGroupType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_tome_series || checkedId == R.id.radio_chapter) {
                layoutTomeDetails.setVisibility(View.VISIBLE);
            } else {
                layoutTomeDetails.setVisibility(View.GONE);
            }
        });

        btnConfirm.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(getContext(), "Donnez un nom à votre projet !", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lancer l'atelier de dessin
            Intent intent = new Intent(getActivity(), CanvasActivity.class);
            intent.putExtra("PROJECT_TITLE", title);
            startActivity(intent);
            dismiss();
        });

        return view;
    }
}
