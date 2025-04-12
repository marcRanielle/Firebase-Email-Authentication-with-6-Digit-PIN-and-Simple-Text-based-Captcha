package com.example.telepathix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash_Screen extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            SharedPreferences prefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE);
            boolean captchaVerified = prefs.getBoolean("captcha_verified", false);

            if (user != null && user.isEmailVerified() && captchaVerified) {
                startActivity(new Intent(Splash_Screen.this, Homepage.class));
            } else {
                startActivity(new Intent(Splash_Screen.this, MainActivity.class));
            }

            finish();

        }, SPLASH_TIME_OUT);
    }
}
