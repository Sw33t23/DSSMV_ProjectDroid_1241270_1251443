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
    public static long currentUserId = -1L; // ID global do user logado
    private String currentUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationHelper = new LocationHelper(this);
        supabaseHelper = new SupabaseHelper();

        // Receber o username do Login/Registo
        currentUsername = getIntent().getStringExtra("USERNAME_KEY");
        if (currentUsername == null) currentUsername = "EcoUtilizador";

        // Obter o ID real do utilizador na BD com base no username
        supabaseHelper.getUserIdByUsername(currentUsername, new SupabaseHelper.UserIdCallback() {
            @Override
            public void onUserIdFound(long userId) {
                currentUserId = userId;
            }

            @Override
            public void onError(String error) {
                // Fallback de segurança caso ocorra erro
                currentUserId = 1L;
            }
        });

        Button btnMap = findViewById(R.id.btnMap);
        Button btnRecycle = findViewById(R.id.btnRecycle);
        Button btnProfile = findViewById(R.id.btnProfile);

        btnMap.setOnClickListener(v -> {
            locationHelper.getLastKnownLocation(new LocationHelper.LocationCallbackListener() {
                @Override
                public void onLocationReceived(double latitude, double longitude) {
                    Toast.makeText(MainActivity.this, "GPS Localizado: Lat " + latitude + ", Lon " + longitude, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onLocationError(String error) {
                    Toast.makeText(MainActivity.this, "Erro GPS: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnRecycle.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecycleActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}