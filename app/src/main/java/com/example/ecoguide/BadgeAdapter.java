package com.example.ecoguide;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class BadgeAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> badges;

    public BadgeAdapter(Context context, ArrayList<String> badges) {
        super(context, android.R.layout.simple_list_item_1, badges);
        this.context = context;
        this.badges = badges;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        String badgeText = badges.get(position);
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(badgeText);
        textView.setTextSize(16); // Tamanho corrigido em pixels equivalentes
        textView.setPadding(28, 28, 28, 28);

        // Estética condicional baseada no estado da medalha
        if (badgeText.startsWith("✅")) {
            convertView.setBackgroundResource(R.drawable.card_background);
            textView.setTextColor(Color.parseColor("#27AE60")); // Verde elegante para desbloqueadas
        } else {
            convertView.setBackgroundResource(R.drawable.card_background);
            textView.setTextColor(Color.parseColor("#ADB5BD")); // Cinzento suave para bloqueadas
        }

        return convertView;
    }
}