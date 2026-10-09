package com.example.ecoguide.model;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    private String username;
    private int totalPoints;
    private int plasticCount;
    private int glassCount;
    private int paperCount;
    private List<String> unlockedBadges;

    public UserProfile(String username) {
        this.username = username;
        this.totalPoints = 0;
        this.plasticCount = 0;
        this.glassCount = 0;
        this.paperCount = 0;
        this.unlockedBadges = new ArrayList<>();
        checkAndUpdateBadges();
    }

    public void addRecycledItems(String materialType, int quantity) {
        if (quantity <= 0) return;

        switch (materialType.toLowerCase()) {
            case "plastico":
                this.plasticCount += quantity;
                this.totalPoints += (quantity * 10);
                break;
            case "vidro":
                this.glassCount += quantity;
                this.totalPoints += (quantity * 15);
                break;
            case "papel":
                this.paperCount += quantity;
                this.totalPoints += (quantity * 5);
                break;
            default:
                break;
        }
        checkAndUpdateBadges();
    }

    private void checkAndUpdateBadges() {
        unlockedBadges.clear();
        if (totalPoints >= 10) {
            unlockedBadges.add("🌱 Reciclador Iniciado");
        }
        if (totalPoints >= 50) {
            unlockedBadges.add("🥉 Iniciante Verde");
        }
        if (totalPoints >= 100) {
            unlockedBadges.add("🥈 Eco-Amigo Prata");
        }
        if (totalPoints >= 200) {
            unlockedBadges.add("🏅 Mestre da Reciclagem");
        }
    }

    public String getUsername() { return username; }
    public int getTotalPoints() { return totalPoints; }
    public int getPlasticCount() { return plasticCount; }
    public int getGlassCount() { return glassCount; }
    public int getPaperCount() { return paperCount; }
    public List<String> getUnlockedBadges() { return unlockedBadges; }
}