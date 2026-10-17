package com.example.ecoguide.data;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SupabaseHelper {

    private static final String SUPABASE_URL = "https://hhmfwgstwpjeegojygog.supabase.co/rest/v1/";
    private static final String API_KEY = "sb_publishable_1T4WcsZ2mz4cvgOsPKN9-A_RuAysq0O"; // Coloca aqui a tua Publishable Key real

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public interface SupabaseCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface UserIdCallback {
        void onUserIdFound(long userId);
        void onError(String error);
    }

    // Inserir Utilizador
    public void insertUser(String username, String password, SupabaseCallback callback) {
        executorService.execute(() -> {
            try {
                URL url = new URL(SUPABASE_URL + "users");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("apikey", API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
                conn.setRequestProperty("Prefer", "return=minimal");
                conn.setDoOutput(true);

                String jsonInputString = String.format("{\"username\":\"%s\", \"password\":\"%s\", \"total_points\":0}", username, password);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    callback.onSuccess();
                } else {
                    callback.onError("Erro HTTP (users): " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e("SupabaseHelper", "Erro", e);
                callback.onError(e.getMessage());
            }
        });
    }

    // Obter o ID real do utilizador através do username (GET /users?username=eq.X)
    public void getUserIdByUsername(String username, UserIdCallback callback) {
        executorService.execute(() -> {
            try {
                URL url = new URL(SUPABASE_URL + "users?username=eq." + username);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("apikey", API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();
                    conn.disconnect();

                    JSONArray jsonArray = new JSONArray(content.toString());
                    if (jsonArray.length() > 0) {
                        JSONObject userObj = jsonArray.getJSONObject(0);
                        long userId = userObj.getLong("id");
                        callback.onUserIdFound(userId);
                    } else {
                        callback.onError("Utilizador não encontrado.");
                    }
                } else {
                    callback.onError("Erro ao buscar ID: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("SupabaseHelper", "Erro", e);
                callback.onError(e.getMessage());
            }
        });
    }

    // Inserir Registo de Reciclagem com o ID real
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
                    callback.onError("Erro HTTP (records): " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e("SupabaseHelper", "Erro", e);
                callback.onError(e.getMessage());
            }
        });
    }
}