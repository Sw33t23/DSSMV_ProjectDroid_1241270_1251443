package com.example.ecoguide;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecoguide.data.SupabaseHelper;

public class RecycleActivity extends AppCompatActivity {

    private SupabaseHelper supabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);

        supabaseHelper = new SupabaseHelper();

        EditText etYellow = findViewById(R.id.etYellowSacks);
        EditText etGreen = findViewById(R.id.etGreenSacks);
        EditText etBlue = findViewById(R.id.etBlueSacks);
        Button btnSubmit = findViewById(R.id.btnSubmitRecycle);

        btnSubmit.setOnClickListener(v -> {
            int yellow = parseSacks(etYellow.getText().toString());
            int green = parseSacks(etGreen.getText().toString());
            int blue = parseSacks(etBlue.getText().toString());

            if (yellow == 0 && green == 0 && blue == 0) {
                Toast.makeText(this, "Insere pelo menos uma saca num ecoponto!", Toast.LENGTH_SHORT).show();
                return;
            }

            int earnedPoints = (yellow * 15) + (green * 10) + (blue * 5);

            long userId = MainActivity.currentUserId;
            if (userId == -1L) {
                userId = 1L; // Fallback de segurança
            }

            // Criamos uma variável final para ser aceite dentro da classe anónima do callback
            final long finalUserId = userId;

            btnSubmit.setEnabled(false);
            Toast.makeText(this, "A registar ecoponto e reciclagem...", Toast.LENGTH_SHORT).show();

            String nomeEcopontoDinamico = "Ecoponto Próximo (" + System.currentTimeMillis() + ")";
            double latitudeAtual = 41.1579;
            double longitudeAtual = -8.6291;

            // 1. Criar/Inserir automaticamente o ecoponto no Supabase e obter o seu ID
            supabaseHelper.insertEcopontoAndGetId(nomeEcopontoDinamico, latitudeAtual, longitudeAtual, new SupabaseHelper.EcopontoIdCallback() {
                @Override
                public void onEcopontoIdCreated(long ecopontoId) {

                    // 2. Com o ID obtido, inserir o registo de reciclagem associado usando a variável final
                    supabaseHelper.insertRecyclingRecord(finalUserId, yellow, green, blue, earnedPoints, ecopontoId, new SupabaseHelper.SupabaseCallback() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(() -> {
                                Toast.makeText(RecycleActivity.this, "Sucesso! Ganhaste " + earnedPoints + " pontos.", Toast.LENGTH_LONG).show();
                                finish();
                            });
                        }

                        @Override
                        public void onError(String error) {
                            runOnUiThread(() -> {
                                Toast.makeText(RecycleActivity.this, "Erro no registo: " + error, Toast.LENGTH_LONG).show();
                                btnSubmit.setEnabled(true);
                            });
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(RecycleActivity.this, "Erro ao criar ecoponto: " + error, Toast.LENGTH_LONG).show();
                        btnSubmit.setEnabled(true);
                    });
                }
            });
        });
    }

    private int parseSacks(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}