package com.example.ecoguide;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Inicializar o cliente de localização do Google Play Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button btnFindClosest = findViewById(R.id.btnFindClosest);
        if (btnFindClosest != null) {
            btnFindClosest.setOnClickListener(v -> obterLocalizacaoRealDoDispositivo());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Ativar o botão azul de localização no mapa se houver permissões
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Posição padrão inicial de segurança (Lisboa, caso o GPS demore)
        LatLng defaultLocation = new LatLng(38.7369, -9.1426);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
    }

    private void obterLocalizacaoRealDoDispositivo() {
        // Verificar se as permissões de localização foram concedidas
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Pedir permissões ao utilizador se não as tiver
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Toast.makeText(this, "Por favor, concede permissão de localização.", Toast.LENGTH_LONG).show();
            return;
        }

        // Obter a última localização conhecida real do dispositivo
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double userLat = location.getLatitude();
                double userLng = location.getLongitude();

                LatLng userPos = new LatLng(userLat, userLng);

                // Criamos um ecoponto de teste virtual ligeiramente à frente da tua posição atual (ex: a ~500 metros)
                // para poderes ver o cálculo a funcionar exatamente onde tu estás (seja em Coimbra, Porto, etc.)
                double ecopontoLat = userLat + 0.004;
                double ecopontoLng = userLng + 0.004;
                LatLng ecopontoPos = new LatLng(ecopontoLat, ecopontoLng);

                // Limpar marcadores anteriores e adicionar o novo ecoponto perto de ti
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(ecopontoPos).title("Ecoponto Próximo de Ti"));

                // Centrar o mapa na tua posição atual com zoom
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userPos, 14));

                // Calcular a distância exata em metros entre ti e o ecoponto
                float[] results = new float[1];
                Location.distanceBetween(userLat, userLng, ecopontoLat, ecopontoLng, results);
                float distance = results[0];

                Toast.makeText(MapActivity.this, "Estás aqui! Ecoponto mais próximo a " + (int)distance + " metros.", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(MapActivity.this, "Não foi possível obter a localização atual. Verifica se o GPS está ligado.", Toast.LENGTH_LONG).show();
            }
        });
    }
}