package com.example.telepathix;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView SignupButton, LoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        SignupButton = findViewById(R.id.SignupButton);
        LoginButton = findViewById(R.id.LoginButton);

        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupNavigate();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginNavigate();
            }
        });

    }

    private void SignupNavigate(){
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(MainActivity.this, Signup.class);
        startActivity(intent);
        finish();
    }

    private void LoginNavigate(){
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }
}