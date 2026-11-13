package models;

import exceptions.NegativeAmountException;
import exceptions.NotEnoughCreditsException;
import exceptions.PlayerNotFoundException;

import java.util.List;

public class Player {
    private String name;
    private int credits;

    public Player(String name, int credits) {
        this.name = name;
        this.credits = credits;
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
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
        System.out.println("----------Player Details----------");
        System.out.println("Name: " + name);
        System.out.println("Credits: " + credits);
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
