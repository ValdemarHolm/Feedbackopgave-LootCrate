package models;

import exceptions.ItemNotFoundException;
import exceptions.NegativeAmountException;
import exceptions.NotEnoughCreditsException;
import exceptions.PlayerNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private int credits;
    private List<String> inventory;

    public Player(String name, int credits) {
        this.name = name;
        this.credits = credits;
        this.inventory = new ArrayList<>();
    }

    public Player(String name, int credits, List<String> inventory) {
        this.name = name;
        this.credits = credits;
        this.inventory = (inventory != null) ? new ArrayList<>(inventory) : new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    public List<String> getInventory() {
        return inventory;
    }

    public void addItem(String item) {
        inventory.add(item);
    }

    // Bestemmer salgsværdien for en given genstand
    public static int getItemValue(String item) {
        switch (item.toLowerCase()) {
            case "rusty dagger": return 40;
            case "wooden shield": return 50;
            case "health potion": return 30;
            case "steel sword": return 200;
            case "iron armor": return 250;
            case "mana elixir": return 150;
            case "mythic dragon blade": return 4000;
            case "crown of kings": return 5000;
            case "phoenix feather": return 3000;
            default: return 50; // standardpris for andre genstande
        }
    }

    // Sælger en genstand til dens standardværdi
    public int sellItem(String item) throws ItemNotFoundException {
        int price = getItemValue(item);
        sellItem(item, price);
        return price;
    }

    // Sælger en genstand til en specificeret pris
    public void sellItem(String item, int price) throws ItemNotFoundException, NegativeAmountException {
        if (!inventory.contains(item)) {
            throw new ItemNotFoundException("Genstanden '" + item + "' blev ikke fundet i " + name + "'s inventory.");
        }
        if (price < 0) {
            throw new NegativeAmountException("Salgsprisen kan ikke være negativ.");
        }
        inventory.remove(item);
        this.credits += price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void addCredits(int amount) throws NegativeAmountException {
        if (amount < 0) {
            throw new NegativeAmountException("Amount must be positive.");
        }
        this.credits += amount;
    }

    public void subtractCredits(int amount) throws NotEnoughCreditsException {
        if (amount > credits) {
            throw new NotEnoughCreditsException("Not enough credits.");
        }
        this.credits -= amount;
    }

    public void displayPlayerDetails() {
        System.out.println("---------- Player Details ----------");
        System.out.println("Name: " + name);
        System.out.println("Credits: " + credits);
        if (inventory.isEmpty()) {
            System.out.println("Inventory: (Tomt)");
        } else {
            System.out.println("Inventory: " + String.join(", ", inventory));
        }
        System.out.println("------------------------------------");
    }

    // Søger efter en spiller i listen ud fra navn
    public static Player findPlayer(List<Player> players, String name) throws PlayerNotFoundException {
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        throw new PlayerNotFoundException("Player '" + name + "' cannot be found in the system.");
    }
}
