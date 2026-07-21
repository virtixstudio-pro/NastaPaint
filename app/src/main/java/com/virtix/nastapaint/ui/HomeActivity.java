package com.virtix.nastapaint.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.virtix.nastapaint.R;

public class HomeActivity extends AppCompatActivity {

    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        searchBar = findViewById(R.id.search_bar);
        ImageButton btnCreateProject = findViewById(R.id.btn_create_project);

        // Recherche dynamique dans les projets/tomes/chapitres
        if (searchBar != null) {
            searchBar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterProjects(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // Clic sur le grand bouton central -> Ouverture du tableau de création
        if (btnCreateProject != null) {
            btnCreateProject.setOnClickListener(v -> showCreateProjectDialog());
        }
    }

    private void filterProjects(String query) {
        // Logique de filtrage des projets
    }

    private void showCreateProjectDialog() {
        CreateProjectDialogFragment dialog = new CreateProjectDialogFragment();
        dialog.show(getSupportFragmentManager(), "CreateProjectDialog");
    }
}
