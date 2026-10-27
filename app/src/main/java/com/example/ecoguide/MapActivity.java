package com.example.ecoguide;

import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecoguide.data.SupabaseHelper;
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
    private SupabaseHelper supabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        supabaseHelper = new SupabaseHelper();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button btnFindClosest = findViewById(R.id.btnFindClosest);
        if (btnFindClosest != null) {
            btnFindClosest.setOnClickListener(v -> calcularEcopontoMaisProximo());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng defaultLocation = new LatLng(38.7369, -9.1426);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));

        carregarEcopontosNoMapa();
    }

    private void carregarEcopontosNoMapa() {
        supabaseHelper.getEcopontos(new SupabaseHelper.EcopontosCallback() {
            @Override
            public void onEcopontosLoaded(JSONArray ecopontos) {
                runOnUiThread(() -> {
                    try {
                        for (int i = 0; i < ecopontos.length(); i++) {
                            JSONObject eco = ecopontos.getJSONObject(i);
                            String name = eco.optString("name", "Ecoponto");
                            double lat = eco.getDouble("latitude");
                            double lng = eco.getDouble("longitude");

                            LatLng pos = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions().position(pos).title(name));
                        }
                    } catch (Exception e) {
                        Toast.makeText(MapActivity.this, "Erro ao processar ecopontos", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(MapActivity.this, "Erro: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void calcularEcopontoMaisProximo() {
        supabaseHelper.getEcopontos(new SupabaseHelper.EcopontosCallback() {
            @Override
            public void onEcopontosLoaded(JSONArray ecopontos) {
                try {
                    double userLat = 38.7369;
                    double userLng = -9.1426;

                    double minDistance = Double.MAX_VALUE;
                    JSONObject closest = null;
                    double closestLat = 0;
                    double closestLng = 0;

                    for (int i = 0; i < ecopontos.length(); i++) {
                        JSONObject eco = ecopontos.getJSONObject(i);
                        double ecoLat = eco.getDouble("latitude");
                        double ecoLng = eco.getDouble("longitude");

                        float[] results = new float[1];
                        Location.distanceBetween(userLat, userLng, ecoLat, ecoLng, results);
                        float distance = results[0];

                        if (distance < minDistance) {
                            minDistance = distance;
                            closest = eco;
                            closestLat = ecoLat;
                            closestLng = ecoLng;
                        }
                    }

                    if (closest != null) {
                        String name = closest.optString("name", "Ecoponto");
                        double finalClosestLat = closestLat;
                        double finalClosestLng = closestLng;
                        double finalDist = minDistance;

                        runOnUiThread(() -> {
                            LatLng pos = new LatLng(finalClosestLat, finalClosestLng);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
                            Toast.makeText(MapActivity.this, "Mais próximo: " + name + " (" + (int)finalDist + "m)", Toast.LENGTH_LONG).show();
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(MapActivity.this, "Erro ao calcular distância", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(MapActivity.this, "Erro ao carregar ecopontos", Toast.LENGTH_SHORT).show());
            }
        });
    }
}