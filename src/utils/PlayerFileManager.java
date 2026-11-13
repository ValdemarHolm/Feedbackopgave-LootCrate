package utils;

import models.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerFileManager {

    // Gemmer listen af spillere og deres inventory i en .txt-fil
    public static void savePlayers(List<Player> players, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Player player : players) {
                String items = String.join(",", player.getInventory());
                // Format: Navn;Credits;Item1,Item2,Item3
                writer.println(player.getName() + ";" + player.getCredits() + ";" + items);
            }
            System.out.println("Spillerdata blev gemt i " + filePath);
        } catch (IOException e) {
            System.out.println("Fejl ved gemning af spillere: " + e.getMessage());
        }
    }

    // Indlæser spillere fra en .txt-fil
    public static List<Player> loadPlayers(String filePath) {
        List<Player> players = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return players; // Returner tom liste hvis filen ikke findes endnu
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(";", -1);
                if (parts.length >= 2) {
                    String name = parts[0];
                    int credits = Integer.parseInt(parts[1]);
                    List<String> inventory = new ArrayList<>();

                    if (parts.length >= 3 && !parts[2].trim().isEmpty()) {
                        String[] items = parts[2].split(",");
                        inventory.addAll(Arrays.asList(items));
                    }

                    players.add(new Player(name, credits, inventory));
                }
            }
            System.out.println("Spillere indlæst fra " + filePath + " (" + players.size() + " fundet).");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Fejl ved indlæsning fra " + filePath + ": " + e.getMessage());
        }

        return players;
    }
}
