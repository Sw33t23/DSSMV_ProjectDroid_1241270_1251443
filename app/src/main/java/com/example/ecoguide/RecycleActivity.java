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

            // ID de utilizador temporário para teste
            String userId = "00000000-0000-0000-0000-000000000000";

            btnSubmit.setEnabled(false); // Evitar duplo clique
            Toast.makeText(this, "A enviar dados para a nuvem...", Toast.LENGTH_SHORT).show();

            supabaseHelper.insertRecyclingRecord(userId, yellow, green, blue, earnedPoints, new SupabaseHelper.SupabaseCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        Toast.makeText(RecycleActivity.this, "Sucesso! Ganhaste " + earnedPoints + " pontos guardados na BD.", Toast.LENGTH_LONG).show();
                        finish();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(RecycleActivity.this, "Erro ao enviar: " + error, Toast.LENGTH_LONG).show();
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