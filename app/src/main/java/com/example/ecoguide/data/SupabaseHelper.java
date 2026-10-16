package com.example.ecoguide.data;

import android.util.Log;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SupabaseHelper {

    // URL base correto do PostgREST do Supabase
    private static final String SUPABASE_URL = "https://hhmfwgstwpjeegojygog.supabase.co/rest/v1/";
    private static final String API_KEY = "sb_publishable_1T4WcsZ2mz4cvgOsPKN9-A_RuAysq0O"; // Coloca aqui a tua Publishable Key real

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public interface SupabaseCallback {
        void onSuccess();
        void onError(String error);
    }

    public void insertUser(String username, SupabaseCallback callback) {
        executorService.execute(() -> {
            try {
                // Endpoint direto para a tabela 'users'
                URL url = new URL(SUPABASE_URL + "users");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("apikey", API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
                conn.setRequestProperty("Prefer", "return=minimal");
                conn.setDoOutput(true);

                String jsonInputString = String.format("{\"username\":\"%s\", \"total_points\":0}", username);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    callback.onSuccess();
                } else {
                    callback.onError("Erro 404/Servidor (users): " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e("SupabaseHelper", "Erro de rede", e);
                callback.onError(e.getMessage());
            }
        });
    }

    public void insertRecyclingRecord(long userId, int yellow, int green, int blue, int points, SupabaseCallback callback) {
        executorService.execute(() -> {
            try {
                URL url = new URL(SUPABASE_URL + "recycle_records");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("apikey", API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
                conn.setRequestProperty("Prefer", "return=minimal");
                conn.setDoOutput(true);

                String jsonInputString = String.format(
                        "{\"user_id\":%d, \"sacks_yellow\":%d, \"sacks_green\":%d, \"sacks_blue\":%d, \"points_earned\":%d}",
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
                    callback.onError("Erro 404/Servidor (records): " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e("SupabaseHelper", "Erro de rede", e);
                callback.onError(e.getMessage());
            }
        });
    }
}