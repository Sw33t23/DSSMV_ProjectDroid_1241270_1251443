package com.example.ecoguide;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoguide.data.SupabaseHelper;
import com.example.ecoguide.utils.LocationHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationHelper locationHelper;
    private SupabaseHelper supabaseHelper;

    private double userLat = 0.0;
    private double userLon = 0.0;
    private double targetLat = 0.0;
    private double targetLon = 0.0;
    private String targetName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        locationHelper = new LocationHelper(this);
        supabaseHelper = new SupabaseHelper();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button btnNavigate = findViewById(R.id.btnNavigate);
        btnNavigate.setOnClickListener(v -> {
            if (targetLat != 0.0 && targetLon != 0.0) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + targetLat + "," + targetLon);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + targetLat + "," + targetLon));
                    startActivity(webIntent);
                }
            } else {
                Toast.makeText(this, "A calcular o ecoponto mais próximo...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationHelper.getLastKnownLocation(new LocationHelper.LocationCallbackListener() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                userLat = latitude;
                userLon = longitude;

                LatLng userLocation = new LatLng(userLat, userLon);
                mMap.addMarker(new MarkerOptions().position(userLocation).title("A minha localização"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14f));

                loadEcopontosAndFindClosest();
            }

            @Override
            public void onLocationError(String error) {
                Toast.makeText(MapActivity.this, "Erro GPS: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadEcopontosAndFindClosest() {
        supabaseHelper.getEcopontos(new SupabaseHelper.EcopontosCallback() {
            @Override
            public void onEcopontosLoaded(JSONArray ecopontos) {
                try {
                    float minDistance = Float.MAX_VALUE;
                    targetName = "";

                    for (int i = 0; i < ecopontos.length(); i++) {
                        JSONObject obj = ecopontos.getJSONObject(i);
                        String name = obj.getString("name");
                        double lat = obj.getDouble("latitude");
                        double lon = obj.getDouble("longitude");

                        LatLng ecoLatLng = new LatLng(lat, lon);
                        mMap.addMarker(new MarkerOptions().position(ecoLatLng).title("Ecoponto: " + name));

                        float[] results = new float[1];
                        Location.distanceBetween(userLat, userLon, lat, lon, results);
                        float distance = results[0];

                        if (distance < minDistance) {
                            minDistance = distance;
                            targetName = name;
                            targetLat = lat;
                            targetLon = lon;
                        }
                    }

                    final float finalMinDist = minDistance;
                    final String finalName = targetName;

                    runOnUiThread(() -> Toast.makeText(MapActivity.this,
                            "Mais próximo: " + finalName + " (" + String.format("%.0f", finalMinDist) + "m)",
                            Toast.LENGTH_LONG).show());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(MapActivity.this, "Erro a carregar ecopontos: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}