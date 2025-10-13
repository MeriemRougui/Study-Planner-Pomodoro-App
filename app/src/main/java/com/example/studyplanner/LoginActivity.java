package com.example.studyplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button loginButton, signupButton;

    SharedPreferences sharedPreferences;
    public static final String PREFS_NAME = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            String savedUsername = sharedPreferences.getString("username", null);
            String savedPassword = sharedPreferences.getString("password", null);

            if (username.equals(savedUsername) && password.equals(savedPassword)) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
            }
        });

        signupButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!username.isEmpty() && !password.isEmpty()) {
                sharedPreferences.edit()
                        .putString("username", username)
                        .putString("password", password)
                        .apply();
                Toast.makeText(this, "Signup successful! You can now log in.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Username and Password required!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
