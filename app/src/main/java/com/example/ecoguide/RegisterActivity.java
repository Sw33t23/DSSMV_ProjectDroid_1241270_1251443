package com.example.ecoguide;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecoguide.data.SupabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private SupabaseHelper supabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        supabaseHelper = new SupabaseHelper();

        EditText etUsername = findViewById(R.id.etRegUsername);
        EditText etPassword = findViewById(R.id.etRegPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnGoToLogin = findViewById(R.id.btnGoToLogin);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            btnRegister.setEnabled(false);
            Toast.makeText(this, "A registar conta na nuvem...", Toast.LENGTH_SHORT).show();

            supabaseHelper.insertUser(username, password, new SupabaseHelper.SupabaseCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.putExtra("USERNAME_KEY", username);
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Erro: " + error, Toast.LENGTH_LONG).show();
                        btnRegister.setEnabled(true);
                    });
                }
            });
        });

        btnGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
}