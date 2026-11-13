package models;

import exceptions.NotEnoughCreditsException;
import exceptions.PlayerNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootCreate {
    private String LootCreateName;
    private String rarity;
    private int price;
    private List<String> possibleRewards;
    private Random random = new Random();

    public LootCreate(String LootCreateName, String rarity, int price) {
        this.LootCreateName = LootCreateName;
        this.rarity = rarity;
        this.price = price;
        this.possibleRewards = generateDefaultRewards(rarity);
    }

    public LootCreate(String LootCreateName, String rarity, int price, List<String> possibleRewards) {
        this.LootCreateName = LootCreateName;
        this.rarity = rarity;
        this.price = price;
        this.possibleRewards = (possibleRewards != null) ? new ArrayList<>(possibleRewards) : generateDefaultRewards(rarity);
    }

    private List<String> generateDefaultRewards(String rarity) {
        List<String> list = new ArrayList<>();
        if (rarity.equalsIgnoreCase("Common") || rarity.equalsIgnoreCase("F") || rarity.equalsIgnoreCase("D")) {
            list.add("Rusty Dagger");
            list.add("Wooden Shield");
            list.add("Health Potion");
        } else if (rarity.equalsIgnoreCase("Rare") || rarity.equalsIgnoreCase("B")) {
            list.add("Steel Sword");
            list.add("Iron Armor");
            list.add("Mana Elixir");
        } else {
            list.add("Mythic Dragon Blade");
            list.add("Crown of Kings");
            list.add("Phoenix Feather");
        }
        return list;
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

    public List<String> getPossibleRewards() {
        return possibleRewards;
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

    public String openLootCreate(Player player) throws NotEnoughCreditsException, PlayerNotFoundException {
        if (player == null) {
            throw new PlayerNotFoundException("Player cannot be found.");
        }
        player.subtractCredits(price);

        String reward = "Mystery Token";
        if (possibleRewards != null && !possibleRewards.isEmpty()) {
            reward = possibleRewards.get(random.nextInt(possibleRewards.size()));
        }
        player.addItem(reward);

        System.out.println("Loot create successfully opened!");
        System.out.println("-> Belønning: " + reward + " (tilføjet til " + player.getName() + "'s inventory)");
        return reward;
    }
}
