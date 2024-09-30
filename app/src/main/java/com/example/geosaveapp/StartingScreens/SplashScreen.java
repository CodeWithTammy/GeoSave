package com.example.geosaveapp.StartingScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.geosaveapp.R;

public class SplashScreen extends AppCompatActivity {
    private static final int SPLASH_DURATION = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed((Runnable)(new Runnable() {
            public final void run() {
                startActivity(new Intent(SplashScreen.this, BoardingScreen.class));
                finish();
            }
        }),5000);
    }
}