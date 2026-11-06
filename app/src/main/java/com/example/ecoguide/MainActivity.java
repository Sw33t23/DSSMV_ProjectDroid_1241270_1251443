package com.example.ecoguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoguide.data.SupabaseHelper;
import com.example.ecoguide.utils.LocationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private LocationHelper locationHelper;
    private SupabaseHelper supabaseHelper;
    public static long currentUserId = -1L;
    private String currentUsername = "";

    private TextView tvWelcome, tvUserPoints, tvUserBadge, tvProgressLabel;
    private ProgressBar progressBarBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationHelper = new LocationHelper(this);
        supabaseHelper = new SupabaseHelper();

        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserPoints = findViewById(R.id.tvUserPoints);
        tvUserBadge = findViewById(R.id.tvUserBadge);
        tvProgressLabel = findViewById(R.id.tvProgressLabel);
        progressBarBadge = findViewById(R.id.progressBarBadge);

        Button btnLeaderboard = findViewById(R.id.btnLeaderboard);

        if (btnLeaderboard != null) {
            btnLeaderboard.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, LeaderboardActivity.class));
            });
        }

        currentUsername = getIntent().getStringExtra("USERNAME_KEY");
        if (currentUsername == null) {
            // Fallback caso venha direto da sessão guardada sem Intent extra
            SharedPreferences prefs = getSharedPreferences("EcoGuidePrefs", MODE_PRIVATE);
            currentUsername = prefs.getString("LOGGED_USER", "EcoUtilizador");
        }

        tvWelcome.setText("🌱 Olá, " + currentUsername + "!");

        supabaseHelper.getUserIdByUsername(currentUsername, new SupabaseHelper.UserIdCallback() {
            @Override
            public void onUserIdFound(long userId) {
                currentUserId = userId;
                loadUserData(userId);
            }

            @Override
            public void onError(String error) {
                currentUserId = 1L;
                loadUserData(currentUserId);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_map) {
                    startActivity(new Intent(MainActivity.this, MapActivity.class));
                    return true;
                } else if (id == R.id.nav_recycle) {
                    startActivity(new Intent(MainActivity.this, RecycleActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return true;
                } else if (id == R.id.nav_logout) {
                    // Limpar a sessão guardada nas SharedPreferences ao fazer Logout
                    SharedPreferences prefs = getSharedPreferences("EcoGuidePrefs", MODE_PRIVATE);
                    prefs.edit().clear().apply();

                    currentUserId = -1L;
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            });
        }
    }

    private void loadUserData(long userId) {
        supabaseHelper.getUserPoints(userId, new SupabaseHelper.PointsCallback() {
            @Override
            public void onPointsReceived(int points) {
                runOnUiThread(() -> {
                    if (tvUserPoints != null) tvUserPoints.setText(points + " pts");
                    updateBadgeAndProgress(points);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    if (tvUserPoints != null) tvUserPoints.setText("0 pts");
                });
            }
        });
    }

    private void updateBadgeAndProgress(int points) {
        String[] badges = {
                "🌱 Nível 1: Eco-Iniciante",
                "🌿 Nível 2: Reciclador Júnior",
                "♻️ Nível 3: Amigo do Ambiente",
                "📘 Nível 4: Guardião do Papel",
                "🟡 Nível 5: Protetor do Plástico",
                "🟢 Nível 6: Mestre do Vidro",
                "🔋 Nível 7: Defensor Energético",
                "🌍 Nível 8: Eco-Ativista",
                "🌳 Nível 9: Guardião da Floresta",
                "💧 Nível 10: Protetor dos Oceanos",
                "⭐ Nível 11: Herói Sustentável",
                "🔥 Nível 12: Lenda da Reciclagem",
                "👑 Nível 13: Imperador Verde",
                "💎 Nível 14: Guardião Supremo",
                "🌟 Nível 15: Divindade Ecológica"
        };

        int tierSize = 50;
        int currentLevel = Math.min(points / tierSize, badges.length - 1);

        tvUserBadge.setText(badges[currentLevel]);

        if (currentLevel < badges.length - 1) {
            int pointsInCurrentTier = points % tierSize;
            int percentage = (pointsInCurrentTier * 100) / tierSize;

            progressBarBadge.setProgress(percentage);
            tvProgressLabel.setText("Progresso para o Nível " + (currentLevel + 2) + ": " + pointsInCurrentTier + "/" + tierSize + " pts");
        } else {
            progressBarBadge.setProgress(100);
            tvProgressLabel.setText("Nível Máximo Atingido! 🏆");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUserId != -1L) {
            loadUserData(currentUserId);
        }
    }
}