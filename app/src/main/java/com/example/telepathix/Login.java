package com.example.telepathix;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText emailInput, pin1, pin2, pin3, pin4, pin5, pin6;
    private TextView loginButton, errorMessage, SignupButton;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.email);
        pin1 = findViewById(R.id.pin1);
        pin2 = findViewById(R.id.pin2);
        pin3 = findViewById(R.id.pin3);
        pin4 = findViewById(R.id.pin4);
        pin5 = findViewById(R.id.pin5);
        pin6 = findViewById(R.id.pin6);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        errorMessage = findViewById(R.id.errorMessage);
        SignupButton = findViewById(R.id.Signup);

        auth = FirebaseAuth.getInstance();

        pin1.addTextChangedListener(new SimpleTextWatcher(pin1, pin2));
        pin2.addTextChangedListener(new SimpleTextWatcher(pin2, pin3));
        pin3.addTextChangedListener(new SimpleTextWatcher(pin3, pin4));
        pin4.addTextChangedListener(new SimpleTextWatcher(pin4, pin5));
        pin5.addTextChangedListener(new SimpleTextWatcher(pin5, pin6));
        pin6.addTextChangedListener(new SimpleTextWatcher(pin6, null));


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateSignup();
            }
        });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String pin = pin1.getText().toString().trim() + pin2.getText().toString().trim() + pin3.getText().toString().trim()
                + pin4.getText().toString().trim() + pin5.getText().toString().trim() + pin6.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pin)) {
            errorMessage.setText("All fields are required");
            return;
        }

        // ✅ Reset CAPTCHA flag in case it was previously verified
        getSharedPreferences("AuthPrefs", MODE_PRIVATE)
                .edit()
                .remove("captcha_verified")
                .apply();

        progressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email, pin)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();

                        if (user != null) {
                            if (user.isEmailVerified()) {
                                startActivity(new Intent(Login.this, Captcha.class));
                                finish();
                            } else {
                                Toast.makeText(Login.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, Verification.class));
                            }
                        }
                    } else {
                        errorMessage.setText("Login failed, Try Again");
                    }
                });
    }

    public class SimpleTextWatcher implements TextWatcher {
        private EditText nextEditText;
        private EditText currentEditText;

        public SimpleTextWatcher(EditText currentEditText, EditText nextEditText) {
            this.currentEditText = currentEditText;
            this.nextEditText = nextEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() == 1) {
                currentEditText.setBackgroundColor(android.graphics.Color.parseColor("#0070ff"));

                if (nextEditText != null) {
                    nextEditText.requestFocus();
                }
            } else if (editable.length() == 0) {
                currentEditText.setBackgroundColor(android.graphics.Color.WHITE);
            }
        }
    }

    private void navigateSignup(){
        Intent intent = new Intent(Login.this, Signup.class);
        startActivity(intent);
        finish();
    }
}
