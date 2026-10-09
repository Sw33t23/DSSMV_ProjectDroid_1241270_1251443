package com.example.ecoguide;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvPoints = findViewById(R.id.tvPoints);
        TextView tvBadges = findViewById(R.id.tvBadges);

        var user = RecycleActivity.currentUser;

        tvUsername.setText("Utilizador: " + user.getUsername());
        tvPoints.setText("Pontos Ecológicos: " + user.getTotalPoints());

        if (user.getUnlockedBadges().isEmpty()) {
            tvBadges.setText("Ainda sem medalhas. Recicla mais para desbloquear!");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String badge : user.getUnlockedBadges()) {
                sb.append("• ").append(badge).append("\n");
            }
            tvBadges.setText(sb.toString());
        }
    }
}