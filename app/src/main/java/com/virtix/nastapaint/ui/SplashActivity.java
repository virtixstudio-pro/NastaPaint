package com.virtix.nastapaint.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.virtix.nastapaint.R;

public class SplashActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "NastaPaintPrefs";
    private static final String KEY_FIRST_LAUNCH = "isFirstLaunch";
    private static final int SPLASH_DELAY = 2500; // 2.5 secondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView txtTitle = findViewById(R.id.txt_splash_title);
        TextView txtSubtitle = findViewById(R.id.txt_splash_subtitle);

        // Animation d'apparition progressive (Fade-In futuriste)
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1500);
        txtTitle.startAnimation(fadeIn);
        txtSubtitle.startAnimation(fadeIn);

        new Handler(Looper.getMainLooper()).postDelayed(this::checkFirstLaunchAndNavigate, SPLASH_DELAY);
    }

    private void checkFirstLaunchAndNavigate() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean(KEY_FIRST_LAUNCH, true);

        Intent intent;
        if (isFirstLaunch) {
            // Premier lancement -> Écran de bienvenue
            intent = new Intent(SplashActivity.this, WelcomeActivity.class);
        } else {
            // Déjà lancé -> Écran d'accueil direct
            intent = new Intent(SplashActivity.this, HomeActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
