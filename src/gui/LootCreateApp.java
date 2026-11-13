package gui;

import exceptions.ItemNotFoundException;
import exceptions.NegativeAmountException;
import exceptions.NotEnoughCreditsException;
import exceptions.PlayerNotFoundException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.LootCreate;
import models.Player;
import utils.PlayerFileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LootCreateApp extends Application {
    private static final String FILE_PATH = "players.txt";

    private List<Player> players;
    private List<LootCreate> lootCreates;

    // UI elementer
    private ComboBox<String> playerComboBox;
    private Label creditsLabel;
    private ListView<String> inventoryListView;
    private ListView<String> cratesListView;
    private TextArea logArea;

    @Override
    public void start(Stage primaryStage) {
        // 1. Data initialisering
        players = PlayerFileManager.loadPlayers(FILE_PATH);
        if (players.isEmpty()) {
            players.add(new Player("Valdemar", 1000));
            players.add(new Player("Oliver", 500));
            players.add(new Player("Kasper", 1000));
            players.add(new Player("Jonas", 1000));
            PlayerFileManager.savePlayers(players, FILE_PATH);
        }

        lootCreates = new ArrayList<>(List.of(
                new LootCreate("Bronze Crate", "Common", 100),
                new LootCreate("Silver Crate", "Rare", 500),
                new LootCreate("Gold Crate", "Rare", 1000),
                new LootCreate("Diamond Crate", "Legendary", 10000)
        ));

        // 2. Layout opbygning
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f4f6f9; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // Top Header
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 15, 0));
        Label titleLabel = new Label("⚔️ Loot Crate System 🎁");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label subtitleLabel = new Label("Exception Handling, Spillere & Inventory");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        headerBox.getChildren().addAll(titleLabel, subtitleLabel);
        root.setTop(headerBox);

        // Center split: Venstre = Spiller & Inventory, Højre = Loot Crates
        HBox centerBox = new HBox(15);
        VBox leftPlayerBox = createPlayerPanel();
        VBox rightCrateBox = createCratePanel();
        HBox.setHgrow(leftPlayerBox, Priority.ALWAYS);
        HBox.setHgrow(rightCrateBox, Priority.ALWAYS);
        centerBox.getChildren().addAll(leftPlayerBox, rightCrateBox);
        root.setCenter(centerBox);

        // Bottom: Log & Test-knapper
        VBox bottomBox = createBottomPanel();
        root.setBottom(bottomBox);

        // Opdater visning med første spiller
        updatePlayerSelection();

        // 3. Opret Scene & Vis Stage
        Scene scene = new Scene(root, 820, 680);
        primaryStage.setTitle("Loot Crate System (JavaFX)");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> PlayerFileManager.savePlayers(players, FILE_PATH));
        primaryStage.show();

        log("Systemet er startet. Spillere indlæst fra " + FILE_PATH);
    }

    private VBox createPlayerPanel() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        Label sectionTitle = new Label("👤 Vælg Spiller");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        playerComboBox = new ComboBox<>();
        playerComboBox.setMaxWidth(Double.MAX_VALUE);
        refreshPlayerComboBox();
        playerComboBox.setOnAction(e -> updatePlayerSelection());

        Button newPlayerBtn = new Button("➕ Opret ny spiller");
        newPlayerBtn.setMaxWidth(Double.MAX_VALUE);
        newPlayerBtn.setOnAction(e -> handleCreateNewPlayer());

        creditsLabel = new Label("Saldo: 0 credits");
        creditsLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        // Tilføj credits felt
        HBox addCreditsBox = new HBox(5);
        TextField creditsInput = new TextField();
        creditsInput.setPromptText("F.eks. 500 eller -100");
        Button addCreditsBtn = new Button("Tilføj");
        addCreditsBtn.setOnAction(e -> {
            try {
                Player p = getSelectedPlayer();
                if (p == null) throw new PlayerNotFoundException("Vælg venligst en gyldig spiller.");
                int amount = Integer.parseInt(creditsInput.getText().trim());
                p.addCredits(amount);
                updatePlayerSelection();
                creditsInput.clear();
                log("Tilføjede " + amount + " credits til " + p.getName());
            } catch (PlayerNotFoundException ex) {
                showErrorAlert("Spiller fejl", ex.getMessage());
            } catch (NegativeAmountException ex) {
                showErrorAlert("Negativt beløb fejl (Unchecked Exception)", ex.getMessage());
            } catch (NumberFormatException ex) {
                showErrorAlert("Input fejl", "Indtast venligst et gyldigt heltal for credits.");
            }
        });
        addCreditsBox.getChildren().addAll(creditsInput, addCreditsBtn);

        // Inventory
        Label invLabel = new Label("🎒 Spillerens Inventory:");
        invLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        inventoryListView = new ListView<>();
        inventoryListView.setPrefHeight(150);

        Button sellItemBtn = new Button("💰 Sælg valgt genstand");
        sellItemBtn.setMaxWidth(Double.MAX_VALUE);
        sellItemBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
        sellItemBtn.setOnAction(e -> handleSellItem());

        box.getChildren().addAll(
                sectionTitle,
                playerComboBox,
                newPlayerBtn,
                new Separator(),
                creditsLabel,
                new Label("Tilføj credits (test NegativeAmountException):"),
                addCreditsBox,
                new Separator(),
                invLabel,
                inventoryListView,
                sellItemBtn
        );
        return box;
    }

    private VBox createCratePanel() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        Label sectionTitle = new Label("📦 Tilgængelige Loot Crates");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        cratesListView = new ListView<>();
        ObservableList<String> crateItems = FXCollections.observableArrayList();
        for (LootCreate c : lootCreates) {
            crateItems.add(c.getLootCreateName() + " (" + c.getRarity() + ") - " + c.getPrice() + " credits");
        }
        cratesListView.setItems(crateItems);
        cratesListView.getSelectionModel().select(0);
        cratesListView.setPrefHeight(150);

        Button openCrateBtn = new Button("🎁 Åbn Valgt Loot Crate");
        openCrateBtn.setMaxWidth(Double.MAX_VALUE);
        openCrateBtn.setPrefHeight(45);
        openCrateBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-cursor: hand;");
        openCrateBtn.setOnAction(e -> handleOpenCrate());

        box.getChildren().addAll(
                sectionTitle,
                new Label("Vælg den kiste du ønsker at åbne:"),
                cratesListView,
                openCrateBtn
        );
        return box;
    }

    private VBox createBottomPanel() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(10, 0, 0, 0));

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        Button runTestsBtn = new Button("🧪 Kør automatiske Exception-tests");
        runTestsBtn.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-font-weight: bold;");
        runTestsBtn.setOnAction(e -> runAllAutomaticTests());

        Button saveBtn = new Button("💾 Gem til fil");
        saveBtn.setOnAction(e -> {
            PlayerFileManager.savePlayers(players, FILE_PATH);
            log("Spillere gemt i " + FILE_PATH);
            showInfoAlert("Gemt", "Spillerdata er gemt i " + FILE_PATH);
        });

        Button clearLogBtn = new Button("Ryd log");
        clearLogBtn.setOnAction(e -> logArea.clear());

        btnRow.getChildren().addAll(runTestsBtn, saveBtn, clearLogBtn);

        Label logLabel = new Label("📋 Konsol & Exception Log:");
        logLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(120);
        logArea.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12px;");

        box.getChildren().addAll(btnRow, logLabel, logArea);
        return box;
    }

    private void handleOpenCrate() {
        try {
            Player player = getSelectedPlayer();
            if (player == null) {
                throw new PlayerNotFoundException("Vælg venligst en spiller først!");
            }

            int selectedIndex = cratesListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex < 0 || selectedIndex >= lootCreates.size()) {
                showErrorAlert("Valg fejl", "Vælg venligst en Loot Crate fra listen.");
                return;
            }

            LootCreate crate = lootCreates.get(selectedIndex);
            String item = crate.openLootCreate(player);

            updatePlayerSelection();
            PlayerFileManager.savePlayers(players, FILE_PATH);

            log("SUCCES: " + player.getName() + " åbnede " + crate.getLootCreateName() + " og fik: " + item + "!");
            showInfoAlert("Loot Crate Åbnet!", "Tillykke! " + player.getName() + " modtog:\n✨ " + item + " ✨\n(Trukket " + crate.getPrice() + " credits)");

        } catch (PlayerNotFoundException ex) {
            log("FEJL: " + ex.getMessage());
            showErrorAlert("PlayerNotFoundException", ex.getMessage());
        } catch (NotEnoughCreditsException ex) {
            log("FEJL: " + ex.getMessage());
            showErrorAlert("NotEnoughCreditsException", ex.getMessage());
        }
    }

    private void handleSellItem() {
        try {
            Player player = getSelectedPlayer();
            if (player == null) {
                throw new PlayerNotFoundException("Vælg venligst en spiller først!");
            }

            String selectedItem = inventoryListView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                throw new ItemNotFoundException("Vælg venligst en genstand i listen over inventory, som du vil sælge.");
            }

            // Rens item-teksten (hvis den indeholder prisangivelse)
            String cleanItemName = selectedItem.split(" \\(")[0].trim();
            int earned = player.sellItem(cleanItemName);

            updatePlayerSelection();
            PlayerFileManager.savePlayers(players, FILE_PATH);

            log("SOLGT: " + player.getName() + " solgte " + cleanItemName + " for " + earned + " credits.");
            showInfoAlert("Genstand solgt", "Du solgte " + cleanItemName + " for " + earned + " credits!\nNy saldo: " + player.getCredits());

        } catch (PlayerNotFoundException ex) {
            log("FEJL: " + ex.getMessage());
            showErrorAlert("PlayerNotFoundException", ex.getMessage());
        } catch (ItemNotFoundException ex) {
            log("FEJL: " + ex.getMessage());
            showErrorAlert("ItemNotFoundException", ex.getMessage());
        }
    }

    private void handleCreateNewPlayer() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ny spiller");
        dialog.setHeaderText("Opret en ny spiller i systemet");
        dialog.setContentText("Spillernavn:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            name = name.trim();
            if (!name.isEmpty()) {
                Player newPlayer = new Player(name, 1000);
                players.add(newPlayer);
                refreshPlayerComboBox();
                playerComboBox.getSelectionModel().select(name);
                updatePlayerSelection();
                PlayerFileManager.savePlayers(players, FILE_PATH);
                log("Oprettede ny spiller: " + name + " med 1000 start-credits.");
            }
        });
    }

    private void runAllAutomaticTests() {
        log("\n--- STARTER AUTOMATISKE TESTS ---");

        LootCreate cheap = lootCreates.get(0);
        LootCreate expensive = lootCreates.get(3);

        // Test 1: Spiller åbner crate (succes)
        try {
            Player p = Player.findPlayer(players, "Valdemar");
            cheap.openLootCreate(p);
            log("Test 1 [Åbn crate]: SUCCES - Valdemar åbnede en crate.");
        } catch (Exception ex) {
            log("Test 1 fejl: " + ex.getMessage());
        }

        // Test 2: Ikke nok credits
        try {
            Player p = Player.findPlayer(players, "Oliver");
            expensive.openLootCreate(p);
            log("Test 2 [NotEnoughCreditsException]: FEJLEDE (skulle have kastet exception)");
        } catch (NotEnoughCreditsException ex) {
            log("Test 2 [NotEnoughCreditsException]: SUCCES - Fanget: " + ex.getMessage());
        } catch (Exception ex) {
            log("Test 2 uventet fejl: " + ex.getMessage());
        }

        // Test 3: Negative credits
        try {
            Player p = Player.findPlayer(players, "Valdemar");
            p.addCredits(-100);
            log("Test 3 [NegativeAmountException]: FEJLEDE (skulle have kastet exception)");
        } catch (NegativeAmountException ex) {
            log("Test 3 [NegativeAmountException]: SUCCES - Fanget: " + ex.getMessage());
        } catch (Exception ex) {
            log("Test 3 uventet fejl: " + ex.getMessage());
        }

        // Test 4: Spiller findes ikke
        try {
            Player ghost = Player.findPlayer(players, "UkendtSpiller999");
            cheap.openLootCreate(ghost);
            log("Test 4 [PlayerNotFoundException]: FEJLEDE (skulle have kastet exception)");
        } catch (PlayerNotFoundException ex) {
            log("Test 4 [PlayerNotFoundException]: SUCCES - Fanget: " + ex.getMessage());
        } catch (Exception ex) {
            log("Test 4 uventet fejl: " + ex.getMessage());
        }

        // Test 5: Item findes ikke
        try {
            Player p = Player.findPlayer(players, "Valdemar");
            p.sellItem("LaserGun9000");
            log("Test 5 [ItemNotFoundException]: FEJLEDE (skulle have kastet exception)");
        } catch (ItemNotFoundException ex) {
            log("Test 5 [ItemNotFoundException]: SUCCES - Fanget: " + ex.getMessage());
        } catch (Exception ex) {
            log("Test 5 uventet fejl: " + ex.getMessage());
        }

        log("--- ALLE TESTS GENNEMFØRT ---\n");
        updatePlayerSelection();
    }

    private Player getSelectedPlayer() {
        String name = playerComboBox.getValue();
        if (name == null) return null;
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    private void updatePlayerSelection() {
        Player p = getSelectedPlayer();
        if (p != null) {
            creditsLabel.setText("Saldo: " + p.getCredits() + " credits");
            ObservableList<String> items = FXCollections.observableArrayList();
            for (String item : p.getInventory()) {
                items.add(item + " (Salgsværdi: " + Player.getItemValue(item) + " cr)");
            }
            inventoryListView.setItems(items);
        } else {
            creditsLabel.setText("Saldo: -");
            inventoryListView.setItems(FXCollections.observableArrayList());
        }
    }

    private void refreshPlayerComboBox() {
        ObservableList<String> names = FXCollections.observableArrayList();
        for (Player p : players) {
            names.add(p.getName());
        }
        playerComboBox.setItems(names);
        if (!names.isEmpty()) {
            playerComboBox.getSelectionModel().select(0);
        }
    }

    private void log(String msg) {
        logArea.appendText(msg + "\n");
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("⚠️ Exception Fanget: " + title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
