package com.example.ecoguide;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecoguide.model.UserProfile;

public class RecycleActivity extends AppCompatActivity {

    // Instância global simples para demonstrar os dados na app
    public static UserProfile currentUser = new UserProfile("EcoUtilizador");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);

        EditText etMaterial = findViewById(R.id.etMaterialType);
        EditText etQuantity = findViewById(R.id.etQuantity);
        Button btnSubmit = findViewById(R.id.btnSubmitRecycle);

        btnSubmit.setOnClickListener(v -> {
            String material = etMaterial.getText().toString().trim();
            String qtyStr = etQuantity.getText().toString().trim();

            if (material.isEmpty() || qtyStr.isEmpty()) {
                Toast.makeText(this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(qtyStr);
            currentUser.addRecycledItems(material, quantity);

            Toast.makeText(this, "Registo efetuado! Pontos totais: " + currentUser.getTotalPoints(), Toast.LENGTH_LONG).show();
            finish(); // Fecha o ecrã e volta ao menu principal
        });
    }
}