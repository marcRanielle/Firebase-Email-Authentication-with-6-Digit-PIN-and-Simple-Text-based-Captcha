package com.example.telepathix;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Verification extends AppCompatActivity {
    private TextView VerifyButton, errorMessage, resend;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        auth = FirebaseAuth.getInstance();
        VerifyButton = findViewById(R.id.VerifyButton);
        progressBar = findViewById(R.id.progressBar);
        errorMessage = findViewById(R.id.errorMessage);
        resend = findViewById(R.id.resend);
        backButton = findViewById(R.id.backButton);

        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailVerification();
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResend();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackButton();
            }
        });
    }

    private void checkEmailVerification() {
        FirebaseUser user = auth.getCurrentUser();
        progressBar.setVisibility(View.VISIBLE);
        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    if (user.isEmailVerified()) {
                        Toast.makeText(this, "Email Verified!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Verification.this, Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        errorMessage.setTextColor(Color.parseColor("#8b0000"));
                        errorMessage.setText("Email not verified. Check your inbox.");
                    }
                } else {
                    Toast.makeText(this, "Failed to reload user info", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void setBackButton() {
        Intent intent = new Intent(Verification.this, Signup.class);
        startActivity(intent);
        finish();
    }

    private void setResend() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !user.isEmailVerified()) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Verification.this, "Verification email sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Verification.this, "Failed to resend verification email", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            if (user == null) {
                Toast.makeText(Verification.this, "No user logged in.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Verification.this, "User is already verified.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
