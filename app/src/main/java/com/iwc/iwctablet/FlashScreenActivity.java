package com.iwc.iwctablet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.android.material.textview.MaterialTextView;

public class FlashScreenActivity extends AppCompatActivity {

    MaterialTextView version;
    private static final int TIME_OUT = 4000;

    @SuppressLint("SetTextI18n")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_screen);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        version = findViewById(R.id.version_flash_screen_tv);
        version.setText("version " + getResources().getString(R.string.current_version));

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(FlashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, TIME_OUT);
    }
}