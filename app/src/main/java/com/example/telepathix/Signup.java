package com.example.telepathix;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Signup extends AppCompatActivity {
    private EditText Email, Pin, ConfirmPin;
    private TextView SignupButton, errorMessage, loginButton;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Email = findViewById(R.id.email);
        Pin = findViewById(R.id.Pin);
        ConfirmPin = findViewById(R.id.ConfirmPin);
        SignupButton = findViewById(R.id.SignupButton);
        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        errorMessage = findViewById(R.id.errorMessage);
        loginButton = findViewById(R.id.loginButton);
        backButton = findViewById(R.id.backButton);

        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateLogin();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMain();
            }
        });
    }

    private void registerUser() {
        String email = Email.getText().toString().trim();
        String pin = Pin.getText().toString().trim();
        String confirmPin = ConfirmPin.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pin) || TextUtils.isEmpty(confirmPin)) {
            errorMessage.setText("All fields are required");
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (pin.length() != 6) {
            errorMessage.setText("PIN must be 6 digits");
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (!pin.equals(confirmPin)) {
            errorMessage.setText("PINs do not match");
            progressBar.setVisibility(View.GONE);
            return;
        }

        auth.createUserWithEmailAndPassword(email, pin)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        sendVerificationEmail();
                    } else {
                        Toast.makeText(this, "Signup Failed, Try Again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = auth.getCurrentUser();
        progressBar.setVisibility(View.GONE);
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Verification email sent!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Signup.this, Verification.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Failed to send verification email, Try Again", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void navigateLogin(){
        Intent intent = new Intent(Signup.this, Login.class);
        startActivity(intent);
        finish();
    }

    private void navigateToMain(){
        Intent intent = new Intent(Signup.this, MainActivity.class);
        startActivity(intent);
    }
}
