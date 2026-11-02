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

    // Variável global que guarda o ID do ecoponto selecionado para usar na reciclagem
    public static Long selectedEcopontoId = 1L; // ID padrão de segurança

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button btnFindClosest = findViewById(R.id.btnFindClosest);
        if (btnFindClosest != null) {
            btnFindClosest.setOnClickListener(v -> encontrarEcopontoMaisProximoNoGPS());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Posição padrão inicial em Portugal (caso o GPS demore)
        LatLng defaultLocation = new LatLng(39.5572, -7.8394);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 7));
    }

    private void encontrarEcopontoMaisProximoNoGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permissão de localização necessária.", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double userLat = location.getLatitude();
                double userLng = location.getLongitude();
                LatLng userPos = new LatLng(userLat, userLng);

                // Criar um ecoponto de teste perto da tua posição real atual
                double ecoLat = userLat + 0.003;
                double ecoLng = userLng + 0.003;
                LatLng ecoPos = new LatLng(ecoLat, ecoLng);

                // Definimos o ID do ecoponto (ex: ID 1 ou outro que tenhas na base de dados)
                selectedEcopontoId = 1L;

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(ecoPos).title("Ecoponto Mais Próximo"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userPos, 14));

                float[] results = new float[1];
                Location.distanceBetween(userLat, userLng, ecoLat, ecoLng, results);
                float distance = results[0];

                Toast.makeText(MapActivity.this, "Ecoponto encontrado a " + (int)distance + " metros de ti!", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(MapActivity.this, "Não foi possível obter a tua posição GPS atual.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}