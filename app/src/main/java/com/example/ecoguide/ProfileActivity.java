package com.example.ecoguide;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoguide.data.SupabaseHelper;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileTitle;
    private ListView listViewBadges;
    private SupabaseHelper supabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvProfileTitle = findViewById(R.id.tvProfileTitle);
        listViewBadges = findViewById(R.id.listViewBadges);
        supabaseHelper = new SupabaseHelper();

        long userId = MainActivity.currentUserId;
        if (userId == -1L) {
            userId = 1L;
        }

        loadUserBadges(userId);
    }

    private void loadUserBadges(long userId) {
        supabaseHelper.getUserPoints(userId, new SupabaseHelper.PointsCallback() {
            @Override
            public void onPointsReceived(int points) {
                runOnUiThread(() -> {
                    if (tvProfileTitle != null) {
                        tvProfileTitle.setText("🏆 Conquistas (" + points + " pts)");
                    }
                    populateBadgesList(points);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    if (tvProfileTitle != null) {
                        tvProfileTitle.setText("🏆 Conquistas (0 pts)");
                    }
                    populateBadgesList(0);
                });
            }
        });
    }

    private void populateBadgesList(int userPoints) {
        // Lista completa com as 15 medalhas do sistema
        String[] allBadges = {
                "🌱 Nível 1: Eco-Iniciante (0+ pts)",
                "🌿 Nível 2: Reciclador Júnior (50+ pts)",
                "♻️ Nível 3: Amigo do Ambiente (100+ pts)",
                "📘 Nível 4: Guardião do Papel (150+ pts)",
                "🟡 Nível 5: Protetor do Plástico (200+ pts)",
                "🟢 Nível 6: Mestre do Vidro (250+ pts)",
                "🔋 Nível 7: Defensor Energético (300+ pts)",
                "🌍 Nível 8: Eco-Ativista (350+ pts)",
                "🌳 Nível 9: Guardião da Floresta (400+ pts)",
                "💧 Nível 10: Protetor dos Oceanos (450+ pts)",
                "⭐ Nível 11: Herói Sustentável (500+ pts)",
                "🔥 Nível 12: Lenda da Reciclagem (550+ pts)",
                "👑 Nível 13: Imperador Verde (600+ pts)",
                "💎 Nível 14: Guardião Supremo (650+ pts)",
                "🌟 Nível 15: Divindade Ecológica (700+ pts)"
        };

        ArrayList<String> displayList = new ArrayList<>();
        int tierSize = 50;

        for (int i = 0; i < allBadges.length; i++) {
            int requiredPoints = i * tierSize;
            if (userPoints >= requiredPoints) {
                displayList.add("✅ Desbloqueada: " + allBadges[i]);
            } else {
                displayList.add("🔒 Bloqueada: " + allBadges[i]);
            }
        }

        BadgeAdapter adapter = new BadgeAdapter(this, displayList);
        if (listViewBadges != null) {
            listViewBadges.setAdapter(adapter);
        }
    }
}