package com.virtix.nastapaint.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.virtix.nastapaint.R;

public class WelcomeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "NastaPaintPrefs";
    private static final String KEY_FIRST_LAUNCH = "isFirstLaunch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btnSkip = findViewById(R.id.btn_welcome_skip);
        Button btnStart = findViewById(R.id.btn_welcome_start);

        btnSkip.setOnClickListener(v -> completeWelcome());
        btnStart.setOnClickListener(v -> completeWelcome());
    }

    private void completeWelcome() {
        // Enregistrer que le premier lancement est effectué
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();

        // Rediriger vers l'écran d'accueil
        Intent intent = new Intent(WelcomeActivity.class, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
