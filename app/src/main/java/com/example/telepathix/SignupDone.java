package com.example.telepathix;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupDone extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_done);

        new Handler().postDelayed(() -> {

            Intent intent = new Intent(SignupDone.this, Login.class);
            startActivity(intent);
            finish();

        }, SPLASH_TIME_OUT);

    }
}