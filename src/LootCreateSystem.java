import exceptions.ItemNotFoundException;
import exceptions.NegativeAmountException;
import exceptions.NotEnoughCreditsException;
import exceptions.PlayerNotFoundException;
import models.LootCreate;
import models.Player;
import utils.PlayerFileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LootCreateSystem {
    private static final String FILE_PATH = "players.txt";

    public static void main(String[] args) {
        // 1. Hent spillere fra fil (eller opret standard-spillere hvis filen er tom/ikke findes)
        List<Player> players = PlayerFileManager.loadPlayers(FILE_PATH);
        if (players.isEmpty()) {
            players.add(new Player("Valdemar", 1000));
            players.add(new Player("Oliver", 500));
            players.add(new Player("Kasper", 1000));
            players.add(new Player("Jonas", 1000));
            PlayerFileManager.savePlayers(players, FILE_PATH);
        }

        // 2. Opret Loot Crates
        List<LootCreate> lootCreates = new ArrayList<>(List.of(
                new LootCreate("Bronze Crate", "Common", 100),
                new LootCreate("Silver Crate", "Rare", 500),
                new LootCreate("Gold Crate", "Rare", 1000),
                new LootCreate("Diamond Crate", "Legendary", 10000)
        ));

        // 3. CLI Menu med Scanner
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Velkommen til Loot Crate Systemet!");

        while (running) {
            System.out.println("\n========== HOVEDMENU ==========");
            System.out.println("1. Vis alle spillere og inventory");
            System.out.println("2. Opret en ny spiller");
            System.out.println("3. Tilføj credits til en spiller");
            System.out.println("4. Vis tilgængelige Loot Crates");
            System.out.println("5. Åbn en Loot Crate for en spiller");
            System.out.println("6. Sælg en genstand fra inventory");
            System.out.println("7. Kør automatiske tests af Exceptions");
            System.out.println("8. Gem spillere til .txt fil");
            System.out.println("9. Gem og afslut");
            System.out.println("===============================");
            System.out.print("Vælg et menupunkt (1-9): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\n--- Spillere i systemet ---");
                    for (Player p : players) {
                        p.displayPlayerDetails();
                    }
                    break;

                case "2":
                    System.out.print("Indtast navn på ny spiller: ");
                    String newName = scanner.nextLine().trim();
                    if (newName.isEmpty()) {
                        System.out.println("Navnet må ikke være tomt.");
                        break;
                    }
                    System.out.print("Indtast start-credits: ");
                    try {
                        int startCredits = Integer.parseInt(scanner.nextLine().trim());
                        if (startCredits < 0) {
                            throw new NegativeAmountException("Start-credits må ikke være negative.");
                        }
                        players.add(new Player(newName, startCredits));
                        System.out.println("Spilleren " + newName + " blev oprettet med " + startCredits + " credits!");
                    } catch (NumberFormatException e) {
                        System.out.println("Fejl: Indtast venligst et gyldigt heltal for credits.");
                    } catch (NegativeAmountException e) {
                        System.out.println("Fejl: " + e.getMessage());
                    }
                    break;

                case "3":
                    System.out.print("Indtast spillernavn: ");
                    String targetName = scanner.nextLine().trim();
                    try {
                        Player player = Player.findPlayer(players, targetName);
                        System.out.print("Indtast beløb der skal tilføjes (f.eks. 200 eller -50 for at teste fejl): ");
                        int amount = Integer.parseInt(scanner.nextLine().trim());
                        player.addCredits(amount);
                        System.out.println(amount + " credits tilføjet! Nuværende saldo: " + player.getCredits());
                    } catch (PlayerNotFoundException e) {
                        System.out.println("Fejl: " + e.getMessage());
                    } catch (NegativeAmountException e) {
                        System.out.println("Fejl: " + e.getMessage());
                    } catch (NumberFormatException e) {
                        System.out.println("Fejl: Ugyldigt talformat.");
                    }
                    break;

                case "4":
                    System.out.println("\n--- Tilgængelige Loot Crates ---");
                    for (int i = 0; i < lootCreates.size(); i++) {
                        LootCreate c = lootCreates.get(i);
                        System.out.println((i + 1) + ". " + c.getLootCreateName() + " (" + c.getRarity() + ") - Pris: " + c.getPrice() + " credits");
                    }
                    break;

                case "5":
                    System.out.print("Indtast spillernavn: ");
                    String openingPlayerName = scanner.nextLine().trim();
                    try {
                        Player p = Player.findPlayer(players, openingPlayerName);
                        System.out.println("Vælg Loot Crate:");
                        for (int i = 0; i < lootCreates.size(); i++) {
                            LootCreate c = lootCreates.get(i);
                            System.out.println((i + 1) + ". " + c.getLootCreateName() + " (" + c.getPrice() + " credits)");
                        }
                        System.out.print("Vælg nummer: ");
                        int crateIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;

                        if (crateIndex < 0 || crateIndex >= lootCreates.size()) {
                            System.out.println("Ugyldigt valg af Loot Crate.");
                            break;
                        }

                        LootCreate selectedCrate = lootCreates.get(crateIndex);
                        selectedCrate.openLootCreate(p);
                    } catch (PlayerNotFoundException e) {
                        System.out.println("Fejl: " + e.getMessage());
                    } catch (NotEnoughCreditsException e) {
                        System.out.println("Fejl: " + e.getMessage());
                    } catch (NumberFormatException e) {
                        System.out.println("Fejl: Indtast venligst et tal.");
                    }
                    break;

                case "6":
                    System.out.print("Indtast spillernavn: ");
                    String sellerName = scanner.nextLine().trim();
                    try {
                        Player seller = Player.findPlayer(players, sellerName);
                        List<String> inv = seller.getInventory();
                        if (inv.isEmpty()) {
                            System.out.println(seller.getName() + " har ingen genstande i sit inventory at sælge.");
                            break;
                        }

                        System.out.println("\n--- " + seller.getName() + "'s Inventory ---");
                        for (int i = 0; i < inv.size(); i++) {
                            String item = inv.get(i);
                            int val = Player.getItemValue(item);
                            System.out.println((i + 1) + ". " + item + " (Værdi: " + val + " credits)");
                        }
                        System.out.print("Vælg nummer på genstand der skal sælges: ");
                        int itemIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;

                        if (itemIndex < 0 || itemIndex >= inv.size()) {
                            System.out.println("Ugyldigt nummer.");
                            break;
                        }

                        String chosenItem = inv.get(itemIndex);
                        int earned = seller.sellItem(chosenItem);
                        System.out.println("Succes! Du solgte " + chosenItem + " for " + earned + " credits.");
                        System.out.println("Ny saldo for " + seller.getName() + ": " + seller.getCredits() + " credits.");
                    } catch (PlayerNotFoundException e) {
                        System.out.println("Fejl: " + e.getMessage());
                    } catch (ItemNotFoundException e) {
                        System.out.println("Fejl: " + e.getMessage());
                    } catch (NumberFormatException e) {
                        System.out.println("Fejl: Indtast venligst et tal.");
                    }
                    break;

                case "7":
                    runAutomaticTests(players, lootCreates);
                    break;

                case "8":
                    PlayerFileManager.savePlayers(players, FILE_PATH);
                    break;

                case "9":
                    PlayerFileManager.savePlayers(players, FILE_PATH);
                    System.out.println("Tak fordi du spillede!");
                    running = false;
                    break;

                default:
                    System.out.println("Ugyldigt valg. Vælg mellem 1 og 9.");
            }
        }
        scanner.close();
    }

    // Kører de automatiske test-scenarier
    public static void runAutomaticTests(List<Player> players, List<LootCreate> lootCreates) {
        System.out.println("\n========== KØRER AUTOMATISKE TESTS ==========");
        LootCreate cheapCrate = lootCreates.get(0); // 100 credits
        LootCreate expensiveCrate = lootCreates.get(3); // 10000 credits

        // Test 1: Spiller åbner en crate (skal virke)
        System.out.println("\nTest 1: Spiller åbner en crate (skal virke)");
        try {
            Player p = Player.findPlayer(players, "Valdemar");
            cheapCrate.openLootCreate(p);
        } catch (NotEnoughCreditsException | PlayerNotFoundException e) {
            System.out.println("Fejl: " + e.getMessage());
        }

        // Test 2: Spiller prøver at åbne en crate uden nok credits (skal give fejl)
        System.out.println("\nTest 2: Spiller prøver at åbne crate uden nok credits (skal give fejl)");
        try {
            Player p = Player.findPlayer(players, "Oliver");
            expensiveCrate.openLootCreate(p);
        } catch (NotEnoughCreditsException | PlayerNotFoundException e) {
            System.out.println("Fejl fanget: " + e.getMessage());
        }

        // Test 3: Spiller prøver at tilføje negative credits (skal give fejl)
        System.out.println("\nTest 3: Spiller prøver at tilføje negative credits (skal give fejl)");
        try {
            Player p = Player.findPlayer(players, "Valdemar");
            p.addCredits(-100);
        } catch (NegativeAmountException e) {
            System.out.println("Fejl fanget: " + e.getMessage());
        } catch (PlayerNotFoundException e) {
            System.out.println("Fejl fanget: " + e.getMessage());
        }

        // Test 4: En spiller der IKKE findes søges op (skal give fejl)
        System.out.println("\nTest 4: Søger efter spiller der ikke findes (skal give fejl)");
        try {
            Player ghost = Player.findPlayer(players, "IkkeEksisterendeSpiller123");
            cheapCrate.openLootCreate(ghost);
        } catch (PlayerNotFoundException e) {
            System.out.println("Fejl fanget: " + e.getMessage());
        } catch (NotEnoughCreditsException e) {
            System.out.println("Fejl fanget: " + e.getMessage());
        }

        // Test 5: Sælg en genstand fra inventory (både succes og ItemNotFoundException)
        System.out.println("\nTest 5: Sælger en genstand fra inventory (skal virke)");
        try {
            Player p = Player.findPlayer(players, "Valdemar");
            if (p.getInventory().isEmpty()) {
                p.addItem("Wooden Shield");
            }
            String itemToSell = p.getInventory().get(0);
            int creditsBefore = p.getCredits();
            int earned = p.sellItem(itemToSell);
            System.out.println("Solgte " + itemToSell + " for " + earned + " credits. Ny saldo: " + p.getCredits() + " (før: " + creditsBefore + ")");
        } catch (PlayerNotFoundException | ItemNotFoundException e) {
            System.out.println("Fejl fanget: " + e.getMessage());
        }

        System.out.println("\nTest 6: Forsøger at sælge en genstand spilleren IKKE har (skal give fejl)");
        try {
            Player p = Player.findPlayer(players, "Valdemar");
            p.sellItem("IkkeEksisterendeVåben");
        } catch (ItemNotFoundException e) {
            System.out.println("Fejl fanget: " + e.getMessage());
        } catch (PlayerNotFoundException e) {
            System.out.println("Fejl fanget: " + e.getMessage());
        }

        System.out.println("\n=============================================");
    }
}
