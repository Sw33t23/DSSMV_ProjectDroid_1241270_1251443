package com.example.ecoguide;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecoguide.utils.LocationHelper;

public class MainActivity extends AppCompatActivity {

    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationHelper = new LocationHelper(this);

        Button btnMap = findViewById(R.id.btnMap);
        Button btnRecycle = findViewById(R.id.btnRecycle);
        Button btnProfile = findViewById(R.id.btnProfile);

        // Botão GPS / Ecopontos Próximos
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

        // Abrir Ecrã de Registo (User Input)
        btnRecycle.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecycleActivity.class);
            startActivity(intent);
        });

        // Abrir Ecrã de Perfil e Medalhas
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}