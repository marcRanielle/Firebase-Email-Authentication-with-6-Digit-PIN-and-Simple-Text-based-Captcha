package com.example.telepathix;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class Captcha extends AppCompatActivity {

    private ImageView captchaImageView, backButton;
    private EditText captchaInput;
    private String captchaText;
    private TextView SubmitButton, errorMessage;
    private ProgressBar progressBar, timerBar;
    private int failedAttempts = 0;
    private boolean isLockedOut = false;
    private long lockoutTimeMillis = 60000;
    private long lockoutEndTime;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha);

        captchaImageView = findViewById(R.id.captcha_image);
        captchaInput = findViewById(R.id.captcha_input);

        generateCaptcha();

        progressBar = findViewById(R.id.progressBar);
        timerBar = findViewById(R.id.timerBar);
        errorMessage = findViewById(R.id.errorMessage);
        SubmitButton = findViewById(R.id.SubmitButton);
        backButton = findViewById(R.id.backButton);

        SubmitButton.setOnClickListener(v -> {
            if (isLockedOut) {
                Toast.makeText(Captcha.this, "Too many failed attempts. Please wait.", Toast.LENGTH_SHORT).show();
                return;
            }

            String userInput = captchaInput.getText().toString();
            progressBar.setVisibility(View.VISIBLE);

            if (userInput.equals(captchaText)) {
                Toast.makeText(Captcha.this, "CAPTCHA Verified!", Toast.LENGTH_SHORT).show();
                navigateToHomepage();
            } else {
                failedAttempts++;
                errorMessage.setText("Incorrect CAPTCHA, Try Again.");
                captchaInput.setText("");
                progressBar.setVisibility(View.GONE);
                generateCaptcha();

                if (failedAttempts >= 3) {
                    isLockedOut = true;
                    SubmitButton.setEnabled(false);
                    captchaInput.setEnabled(false);
                    timerBar.setVisibility(View.VISIBLE);
                    timerBar.setProgress(60);

                    new android.os.CountDownTimer(60000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            int secondsLeft = (int) (millisUntilFinished / 1000);
                            errorMessage.setText("Too many failed attempts. Please wait " + secondsLeft + " seconds.");
                            timerBar.setProgress(secondsLeft);
                        }

                        public void onFinish() {
                            isLockedOut = false;
                            failedAttempts = 0;
                            SubmitButton.setEnabled(true);
                            captchaInput.setEnabled(true);
                            errorMessage.setText("");
                            timerBar.setVisibility(View.GONE);
                            generateCaptcha();
                        }
                    }.start();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });

    }

    private String generateRandomString() {
        int length = 6;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder captcha = new StringBuilder();

        for (int i = 0; i < length; i++) {
            captcha.append(characters.charAt(random.nextInt(characters.length())));
        }

        return captcha.toString();
    }

    private Bitmap generateCaptchaImage(String captchaText) {
        int width = 200, height = 80;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint();
        paint.setTextSize(40);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);

        canvas.drawText(captchaText, 50, 50, paint);

        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            paint.setColor(Color.GRAY);
            int startX = rand.nextInt(width);
            int startY = rand.nextInt(height);
            int endX = rand.nextInt(width);
            int endY = rand.nextInt(height);
            canvas.drawLine(startX, startY, endX, endY, paint);
        }

        return bitmap;
    }

    private void generateCaptcha() {
        captchaText = generateRandomString();
        Bitmap captchaImage = generateCaptchaImage(captchaText);
        captchaImageView.setImageBitmap(captchaImage);
    }

    private void navigateToHomepage(){
        getSharedPreferences("AuthPrefs", MODE_PRIVATE)
                .edit()
                .putBoolean("captcha_verified", true)
                .apply();

        Intent intent = new Intent(Captcha.this, Homepage.class);
        startActivity(intent);
        finish();
    }

    private void startLockoutTimer() {
        isLockedOut = true;
        lockoutEndTime = System.currentTimeMillis() + lockoutTimeMillis;

        SubmitButton.setEnabled(false);
        captchaInput.setEnabled(false);
        errorMessage.setText("Too many failed attempts. Please wait 30 seconds.");

        handler.postDelayed(() -> {
            isLockedOut = false;
            failedAttempts = 0;
            SubmitButton.setEnabled(true);
            captchaInput.setEnabled(true);
            errorMessage.setText("");
            generateCaptcha();
        }, lockoutTimeMillis);
    }

    private void navigateToLogin(){
        Intent intent = new Intent(Captcha.this, Login.class);
        startActivity(intent);
        finish();
    }

}
