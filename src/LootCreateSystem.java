import exceptions.NegativeAmountException;
import exceptions.NotEnoughCreditsException;
import exceptions.PlayerNotFoundException;
import models.LootCreate;
import models.Player;

import java.util.ArrayList;
import java.util.List;

public class LootCreateSystem {
    public static void main(String[] args) {
        // Liste af spillere
        Player valdemar = new Player("Valdemar", 1000);
        Player oliver = new Player("Oliver", 500);
        Player kasper = new Player("Kasper", 1000);
        Player jonas = new Player("Jonas", 1000);
        List<Player> players = new ArrayList<>(List.of(valdemar, oliver, kasper, jonas));

        // Liste af loot creates
        LootCreate lootCreate = new LootCreate("A", "B", 1000);
        LootCreate lootCreate2 = new LootCreate("C", "D", 500);
        LootCreate lootCreate3 = new LootCreate("E", "F", 100);
        LootCreate lootCreate4 = new LootCreate("G", "H", 10000);
        List<LootCreate> lootCreates = new ArrayList<>(List.of(lootCreate, lootCreate2, lootCreate3, lootCreate4));

        // 1. En spiller åbner en create (skal virke)
        try {
            lootCreate.openLootCreate(Player.findPlayer(players, "Valdemar"));
        } catch (NotEnoughCreditsException e) {
            System.out.println(e.getMessage());
        } catch (PlayerNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // 2. En spiller åbner create uden nok credits (Valdemar har nu 0 credits)
        try {
            lootCreate.openLootCreate(Player.findPlayer(players, "Valdemar"));
        } catch (NotEnoughCreditsException e) {
            System.out.println(e.getMessage());
        } catch (PlayerNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // 3. En spiller forsøger at give negativ credits (giver fejl)
        try {
            Player.findPlayer(players, "Valdemar").addCredits(-100);
        } catch (NegativeAmountException e) {
            System.out.println(e.getMessage());
        } catch (PlayerNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // 4. En spiller der IKKE findes i listen forsøger at åbne en crate (søger efter en String)
        try {
            lootCreate.openLootCreate(Player.findPlayer(players, "Peter"));
        } catch (NotEnoughCreditsException e) {
            System.out.println(e.getMessage());
        } catch (PlayerNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
