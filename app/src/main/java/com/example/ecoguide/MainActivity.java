package com.example.ecoguide;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

    private TextView tvWelcome, tvUserPoints, tvUserBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationHelper = new LocationHelper(this);
        supabaseHelper = new SupabaseHelper();

        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserPoints = findViewById(R.id.tvUserPoints);
        tvUserBadge = findViewById(R.id.tvUserBadge);

        Button btnLogout = findViewById(R.id.btnLogout);

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                currentUserId = -1L;
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        currentUsername = getIntent().getStringExtra("USERNAME_KEY");
        if (currentUsername == null) {
            currentUsername = "EcoUtilizador";
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
                    if (tvUserBadge != null) tvUserBadge.setText(calculateBadge(points));
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

    private String calculateBadge(int points) {
        if (points < 50) {
            return "🌱 Eco-Iniciante";
        } else if (points < 150) {
            return "🌿 Eco-Amigo";
        } else {
            return "🌳 Mestre da Reciclagem";
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