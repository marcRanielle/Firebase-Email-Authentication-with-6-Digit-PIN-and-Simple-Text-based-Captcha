package com.example.telepathix;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.view.inputmethod.InputMethodManager;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText emailInput, pin1, pin2, pin3, pin4, pin5, pin6;
    private TextView loginButton, errorMessage, SignupButton, textView, errorMessage2;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private ImageView backButton;
    private LinearLayout pinBox;

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
        backButton = findViewById(R.id.backButton);
        pinBox = findViewById(R.id.linearLayout);
        textView = findViewById(R.id.textView7);
        errorMessage2 = findViewById(R.id.errorMessage2);

        auth = FirebaseAuth.getInstance();

        pin1.addTextChangedListener(new SimpleTextWatcher(pin1, pin2, null));
        pin2.addTextChangedListener(new SimpleTextWatcher(pin2, pin3, pin1));
        pin3.addTextChangedListener(new SimpleTextWatcher(pin3, pin4, pin2));
        pin4.addTextChangedListener(new SimpleTextWatcher(pin4, pin5, pin3));
        pin5.addTextChangedListener(new SimpleTextWatcher(pin5, pin6, pin4));
        pin6.addTextChangedListener(new SimpleTextWatcher(pin6, null, pin5));

        EditText[] pins = {pin1, pin2, pin3, pin4, pin5, pin6};
        for (EditText pin : pins) {
            pin.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButton();

                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginUser();
                    }
                });

            }
        });

        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateSignup();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMain();
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
                                Intent intent = new Intent(Login.this, Captcha.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Login.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, Verification.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    } else {
                        pin1.setText("");
                        pin2.setText("");
                        pin3.setText("");
                        pin4.setText("");
                        pin5.setText("");
                        pin6.setText("");

                        pin1.requestFocus();

                        errorMessage2.setText("Login failed, Try Again");
                    }
                });
    }

    public class SimpleTextWatcher implements TextWatcher {
        private EditText nextEditText;
        private EditText currentEditText;
        private EditText previousEditText;

        public SimpleTextWatcher(EditText currentEditText, EditText nextEditText, EditText previousEditText) {
            this.currentEditText = currentEditText;
            this.nextEditText = nextEditText;
            this.previousEditText = previousEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0 && before == 1 && previousEditText != null) {
                previousEditText.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1) {
                currentEditText.setBackgroundResource(R.drawable.pinborder2);
                if (nextEditText != null) nextEditText.requestFocus();
            } else if (s.length() == 0) {
                currentEditText.setBackgroundResource(R.drawable.pinborder);
            }
        }
    }

    private void navigateSignup(){
        Intent intent = new Intent(Login.this, Signup.class);
        startActivity(intent);
        finish();
    }

    private void navigateToMain(){
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void nextButton(){
        progressBar.setVisibility(View.VISIBLE);
        String userInput = emailInput.getText().toString().trim();

        if (!userInput.isEmpty()) {
            textView.setText("Enter your PIN");
            loginButton.setText("Sign in");
            emailInput.setVisibility(View.GONE);
            errorMessage.setVisibility(View.VISIBLE);
            pinBox.setVisibility(View.VISIBLE);
            pin1.requestFocus();
            errorMessage2.setVisibility(View.VISIBLE);
            errorMessage2.setText("");

            pin1.setText("");
            pin2.setText("");
            pin3.setText("");
            pin4.setText("");
            pin5.setText("");
            pin6.setText("");
            pin1.requestFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(pin1, InputMethodManager.SHOW_IMPLICIT);

            progressBar.setVisibility(View.GONE);
        } else {
            errorMessage.setText("This field is required!");
            progressBar.setVisibility(View.GONE);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    private void reset() {
        emailInput.setText("");
        emailInput.clearFocus();
        emailInput.setError(null);

        progressBar.setVisibility(View.VISIBLE);
        textView.setText("Enter your Email Address");
        loginButton.setText("Next");
        errorMessage.setText("");
        errorMessage.setVisibility(View.INVISIBLE);
        emailInput.setVisibility(View.VISIBLE);
        pinBox.setVisibility(View.GONE);
        errorMessage2.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.GONE);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(emailInput, InputMethodManager.SHOW_IMPLICIT);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButton();

                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginUser();
                    }
                });

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMain();
            }
        });
    }

}