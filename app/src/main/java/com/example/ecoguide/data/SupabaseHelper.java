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
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SupabaseHelper {

    private static final String SUPABASE_URL = "https://hhmfwgstwpjeegojygog.supabase.co/rest/v1/";
    private static final String API_KEY = "sb_publishable_1T4WcsZ2mz4cvgOsPKN9-A_RuAysq0O";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public interface SupabaseCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface UserIdCallback {
        void onUserIdFound(long userId);
        void onError(String error);
    }

    public interface EcopontosCallback {
        void onEcopontosLoaded(JSONArray ecopontos);
        void onError(String error);
    }

    public interface PointsCallback {
        void onPointsReceived(int points);
        void onError(String error);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao encriptar password", e);
        }
    }

    public void insertUser(String username, String password, SupabaseCallback callback) {
        String hashedPassword = hashPassword(password);

        executorService.execute(() -> {
            try {
                URL url = new URL(SUPABASE_URL + "users");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("apikey", API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
                conn.setRequestProperty("Prefer", "return=representation");
                conn.setDoOutput(true);

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("username", username);
                jsonBody.put("password", hashedPassword);
                jsonBody.put("total_points", 0);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    callback.onSuccess();
                } else {
                    String errorDetails = "";
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        errorDetails = response.toString();
                    } catch (Exception e) {
                        errorDetails = "Não foi possível ler o detalhe do erro";
                    }

                    if (errorDetails.contains("23505")) {
                        callback.onError("Este nome de utilizador já está a ser utilizado.");
                    } else {
                        callback.onError("Erro: " + errorDetails);
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e("SupabaseHelper", "Erro", e);
                callback.onError(e.getMessage());
            }
        });
    }

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
                    StringBuilder content = new StringBuilder();
                    String inputLine;
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

    public void insertRecyclingRecord(long userId, int yellow, int green, int blue, int points, Long ecopontoId, SupabaseCallback callback) {
        executorService.execute(() -> {
            try {
                URL url = new URL(SUPABASE_URL + "recycle_records");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("apikey", API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
                conn.setRequestProperty("Prefer", "return=representation");
                conn.setDoOutput(true);

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("user_id", userId);
                jsonBody.put("sacks_yellow", yellow);
                jsonBody.put("sacks_green", green);
                jsonBody.put("sacks_blue", blue);
                jsonBody.put("points_earned", points);

                if (ecopontoId != null) {
                    jsonBody.put("ecoponto_id", ecopontoId);
                } else {
                    jsonBody.put("ecoponto_id", JSONObject.NULL);
                }

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
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

    public void getEcopontos(EcopontosCallback callback) {
        executorService.execute(() -> {
            try {
                URL url = new URL(SUPABASE_URL + "ecopontos");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("apikey", API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder content = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();
                    conn.disconnect();

                    JSONArray ecopontosArray = new JSONArray(content.toString());
                    callback.onEcopontosLoaded(ecopontosArray);
                } else {
                    callback.onError("Erro ao buscar ecopontos: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("SupabaseHelper", "Erro", e);
                callback.onError(e.getMessage());
            }
        });
    }

    public void getUserPoints(long userId, PointsCallback callback) {
        executorService.execute(() -> {
            try {
                URL url = new URL(SUPABASE_URL + "users?id=eq." + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("apikey", API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        content.append(line);
                    }
                    in.close();
                    conn.disconnect();

                    JSONArray arr = new JSONArray(content.toString());
                    if (arr.length() > 0) {
                        JSONObject obj = arr.getJSONObject(0);
                        int points = obj.getInt("total_points");
                        callback.onPointsReceived(points);
                    } else {
                        callback.onError("Utilizador não encontrado");
                    }
                } else {
                    callback.onError("Erro ao buscar pontos: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("SupabaseHelper", "Erro", e);
                callback.onError(e.getMessage());
            }
        });
    }
}