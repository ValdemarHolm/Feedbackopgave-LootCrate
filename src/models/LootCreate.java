package models;

import exceptions.NotEnoughCreditsException;

import java.util.Random;

public class LootCreate {
    private String LootCreateName;
    private String rarity;
    private int price;

    public LootCreate(String LootCreateName, String rarity, int price) {
        this.LootCreateName = LootCreateName;
        this.rarity = rarity;
        this.price = price;
    }

    public String getLootCreateName() {
        return LootCreateName;
    }

    public String getRarity() {
        return rarity;
    }

    public int getPrice() {
        return price;
    }

    public void setLootCreateName(String lootCreateName) {
        LootCreateName = lootCreateName;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void openLootCreate(Player player) throws NotEnoughCreditsException {
        player.subtractCredits(price);
        System.out.println("Loot create successfully opened!");
    }
}
