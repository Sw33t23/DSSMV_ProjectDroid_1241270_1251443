package com.example.ecoguide;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoguide.data.SupabaseHelper;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileTitle;
    private ListView listViewBadges;
    private Button btnEditUsername, btnDeleteAccount;
    private SupabaseHelper supabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvProfileTitle = findViewById(R.id.tvProfileTitle);
        listViewBadges = findViewById(R.id.listViewBadges);
        btnEditUsername = findViewById(R.id.btnEditUsername);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        supabaseHelper = new SupabaseHelper();

        // Declarar final para garantir compatibilidade com expressões lambda
        final long userId = (MainActivity.currentUserId != -1L) ? MainActivity.currentUserId : 1L;

        loadUserBadges(userId);

        // Ação para Alterar Nome (Método PATCH)
        if (btnEditUsername != null) {
            btnEditUsername.setOnClickListener(v -> showEditUsernameDialog(userId));
        }

        // Ação para Apagar Conta (Método DELETE)
        if (btnDeleteAccount != null) {
            btnDeleteAccount.setOnClickListener(v -> showDeleteConfirmationDialog(userId));
        }
    }

    private void showEditUsernameDialog(final long userId) {
        final EditText input = new EditText(this);
        input.setHint("Novo nome de utilizador");
        input.setPadding(40, 30, 40, 30);

        new AlertDialog.Builder(this)
                .setTitle("Alterar Nome de Utilizador")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        supabaseHelper.updateUsername(userId, newName, new SupabaseHelper.SupabaseCallback() {
                            @Override
                            public void onSuccess() {
                                runOnUiThread(() -> {
                                    Toast.makeText(ProfileActivity.this, "Nome atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                                    SharedPreferences.Editor editor = getSharedPreferences("EcoGuidePrefs", MODE_PRIVATE).edit();
                                    editor.putString("LOGGED_USER", newName);
                                    editor.apply();
                                    recreate(); // Reinicia a activity para refletir as mudanças
                                });
                            }

                            @Override
                            public void onError(String error) {
                                runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Erro ao atualizar: " + error, Toast.LENGTH_LONG).show());
                            }
                        });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showDeleteConfirmationDialog(final long userId) {
        new AlertDialog.Builder(this)
                .setTitle("Apagar Conta")
                .setMessage("Tens a certeza que pretendes apagar a tua conta permanentemente? Esta ação não pode ser desfeita.")
                .setPositiveButton("Sim, Apagar", (dialog, which) -> {
                    supabaseHelper.deleteUserAccount(userId, new SupabaseHelper.SupabaseCallback() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(() -> {
                                SharedPreferences prefs = getSharedPreferences("EcoGuidePrefs", MODE_PRIVATE);
                                prefs.edit().clear().apply();

                                Toast.makeText(ProfileActivity.this, "Conta apagada com sucesso.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            });
                        }

                        @Override
                        public void onError(String error) {
                            runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Erro ao apagar conta: " + error, Toast.LENGTH_LONG).show());
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
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