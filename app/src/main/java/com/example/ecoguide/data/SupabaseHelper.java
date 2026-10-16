package com.example.ecoguide.data;

import android.util.Log;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SupabaseHelper {

    // Substitui pelos teus dados reais do Supabase (Project Settings > API)
    private static final String SUPABASE_URL = "https://hhmfwgstwpjeegojygog.supabase.co/rest/v1/recycling_records";
    private static final String SUPABASE_ANON_KEY = "sb_publishable_1T4WcsZ2mz4cvgOsPKN9-A_RuAysq0O";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public interface SupabaseCallback {
        void onSuccess();
        void onError(String error);
    }

    public void insertRecyclingRecord(String userId, int yellow, int green, int blue, int points, SupabaseCallback callback) {
        executorService.execute(() -> {
            try {
                URL url = new URL(SUPABASE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("apikey", SUPABASE_ANON_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_ANON_KEY);
                conn.setRequestProperty("Prefer", "return=minimal");
                conn.setDoOutput(true);

                // Criar o JSON manualmente sem dependências externas pesadas
                String jsonInputString = String.format(
                        "{\"user_id\":\"%s\", \"sacks_yellow\":%d, \"sacks_green\":%d, \"sacks_blue\":%d, \"points_earned\":%d}",
                        userId, yellow, green, blue, points
                );

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    callback.onSuccess();
                } else {
                    callback.onError("Erro no servidor: " + responseCode);
                }

                conn.disconnect();
            } catch (Exception e) {
                Log.e("SupabaseHelper", "Erro na rede", e);
                callback.onError(e.getMessage());
            }
        });
    }
}