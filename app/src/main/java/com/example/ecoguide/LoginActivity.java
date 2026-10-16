package com.example.ecoguide;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etUsername = findViewById(R.id.etUsername);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnGoToRegister = findViewById(R.id.btnGoToRegister);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(this, "Por favor, insere o teu nome de utilizador!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("USERNAME_KEY", username);
            startActivity(intent);
            finish();
        });

        btnGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }
}