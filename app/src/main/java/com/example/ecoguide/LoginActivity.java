package com.example.ecoguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecoguide.data.SupabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private SupabaseHelper supabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Verificar se já existe uma sessão guardada no telemóvel
        SharedPreferences prefs = getSharedPreferences("EcoGuidePrefs", MODE_PRIVATE);
        String savedUser = prefs.getString("LOGGED_USER", null);

        if (savedUser != null) {
            // Se já estiver logado, salta direto para a MainActivity sem mostrar o ecrã de login
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("USERNAME_KEY", savedUser);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        supabaseHelper = new SupabaseHelper();

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnGoToRegister = findViewById(R.id.btnGoToRegister);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnLogin.setEnabled(false);
                Toast.makeText(this, "A validar credenciais...", Toast.LENGTH_SHORT).show();

                supabaseHelper.getUserIdByUsername(username, new SupabaseHelper.UserIdCallback() {
                    @Override
                    public void onUserIdFound(long userId) {
                        runOnUiThread(() -> {
                            // 2. Guardar a sessão com SharedPreferences em caso de sucesso
                            SharedPreferences.Editor editor = getSharedPreferences("EcoGuidePrefs", MODE_PRIVATE).edit();
                            editor.putString("LOGGED_USER", username);
                            editor.apply();

                            Toast.makeText(LoginActivity.this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("USERNAME_KEY", username);
                            startActivity(intent);
                            finish();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Erro: Utilizador não encontrado ou password incorreta.", Toast.LENGTH_LONG).show();
                            btnLogin.setEnabled(true);
                        });
                    }
                });
            });
        }

        if (btnGoToRegister != null) {
            btnGoToRegister.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            });
        }
    }
}