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

public class LeaderboardAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> leaderboardItems;

    public LeaderboardAdapter(Context context, ArrayList<String> leaderboardItems) {
        super(context, android.R.layout.simple_list_item_1, leaderboardItems);
        this.context = context;
        this.leaderboardItems = leaderboardItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        String itemText = leaderboardItems.get(position);
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(itemText);
        textView.setTextSize(16);
        textView.setPadding(28, 28, 28, 28);

        // Aplicar o fundo moderno em cartão
        convertView.setBackgroundResource(R.drawable.card_background);

        // Destacar o Top 3 visualmente
        if (position == 0) {
            textView.setTextColor(Color.parseColor("#D4AC0D")); // Ouro para 1º lugar
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
        } else if (position == 1) {
            textView.setTextColor(Color.parseColor("#7F8C8D")); // Prata para 2º lugar
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
        } else if (position == 2) {
            textView.setTextColor(Color.parseColor("#D35400")); // Bronze para 3º lugar
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            textView.setTextColor(Color.parseColor("#212529")); // Cor normal para os restantes
            textView.setTypeface(null, android.graphics.Typeface.NORMAL);
        }

        return convertView;
    }
}