package com.example.ecoguide;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecoguide.data.SupabaseHelper;
import com.example.ecoguide.utils.LocationHelper;

public class MainActivity extends AppCompatActivity {

    private LocationHelper locationHelper;
    private SupabaseHelper supabaseHelper;
    public static long currentUserId = -1L; // ID global do utilizador logado
    private String currentUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationHelper = new LocationHelper(this);
        supabaseHelper = new SupabaseHelper();

        // Receber o username enviado pelo Ecrã de Login ou Registo
        currentUsername = getIntent().getStringExtra("USERNAME_KEY");
        if (currentUsername == null) {
            currentUsername = "EcoUtilizador";
        }

        // Obter o ID real do utilizador na base de dados com base no username
        supabaseHelper.getUserIdByUsername(currentUsername, new SupabaseHelper.UserIdCallback() {
            @Override
            public void onUserIdFound(long userId) {
                currentUserId = userId;
            }

            @Override
            public void onError(String error) {
                currentUserId = 1L; // Fallback de segurança caso ocorra falha de rede
            }
        });

        Button btnMap = findViewById(R.id.btnMap);
        Button btnRecycle = findViewById(R.id.btnRecycle);
        Button btnProfile = findViewById(R.id.btnProfile);

        // Botão Mapa / GPS -> Abre o ecrã do Google Maps
        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });

        // Botão Registo de Reciclagem -> Abre o ecrã de input de sacas
        btnRecycle.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecycleActivity.class);
            startActivity(intent);
        });

        // Botão Perfil -> Abre o ecrã de perfil e medalhas
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}