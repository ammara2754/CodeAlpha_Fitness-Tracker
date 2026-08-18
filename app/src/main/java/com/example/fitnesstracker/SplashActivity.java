package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 1800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        TextView splashIcon = findViewById(R.id.tvSplashIcon);
        TextView splashTitle = findViewById(R.id.tvSplashTitle);

        // Fade-in animation
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(900);
        fadeIn.setFillAfter(true);

        splashIcon.startAnimation(fadeIn);
        splashTitle.startAnimation(fadeIn);

        // Open MainActivity after splash
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            Intent intent = new Intent(
                    SplashActivity.this,
                    MainActivity.class
            );

            startActivity(intent);

            finish();

        }, SPLASH_TIME);
    }
}