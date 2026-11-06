package com.example.ecoguide;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LeaderboardActivity extends AppCompatActivity {

    private ListView listViewLeaderboard;
    private ArrayList<String> leaderboardList;
    private LeaderboardAdapter adapter;

    private static final String SUPABASE_URL = "https://hhmfwgstwpjeegojygog.supabase.co/rest/v1/";
    private static final String API_KEY = "sb_publishable_1T4WcsZ2mz4cvgOsPKN9-A_RuAysq0O";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        listViewLeaderboard = findViewById(R.id.listViewLeaderboard);
        leaderboardList = new ArrayList<>();

        adapter = new LeaderboardAdapter(this, leaderboardList);
        listViewLeaderboard.setAdapter(adapter);

        fetchLeaderboardData();
    }

    private void fetchLeaderboardData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                URL url = new URL(SUPABASE_URL + "users?select=username,total_points&order=total_points.desc&limit=10");
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

                    JSONArray jsonArray = new JSONArray(content.toString());
                    leaderboardList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject user = jsonArray.getJSONObject(i);
                        String username = user.getString("username");
                        int points = user.getInt("total_points");

                        String rankEmoji = (i == 0) ? "🥇 " : (i == 1) ? "🥈 " : (i == 2) ? "🥉 " : (i + 1) + "º ";
                        leaderboardList.add(rankEmoji + " " + username + "  —  " + points + " pts");
                    }

                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                } else {
                    runOnUiThread(() -> Toast.makeText(LeaderboardActivity.this, "Erro ao carregar leaderboard", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(LeaderboardActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}