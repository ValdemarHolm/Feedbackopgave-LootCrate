# ⚔️ LootCrate System med Exception Handling

Et komplet Java-system udviklet som feedbackopgave, der demonstrerer **objektorienteret programmering**, **custom exception handling** (checked vs. unchecked), **filpersistering**, en **interaktiv CLI-menu** samt en **JavaFX GUI**.

---

## 📌 Indholdsfortegnelse
- [Formål](#formål)
- [Funktioner](#funktioner)
- [Exceptions i systemet](#exceptions-i-systemet)
- [Projektstruktur](#projektstruktur)
- [Kørselsvejledning](#kørselsvejledning)
- [Refleksionsspørgsmål & Teori](#refleksionsspørgsmål--teori)

---

## 🎯 Formål
Formålet med projektet er at opbygge et robust Loot Crate-system, hvor exceptions bruges aktivt til at forhindre, at programmet crasher ved ugyldig brugeradfærd eller uventede tilstande.

---

## ✨ Funktioner

### Kernefunktioner
1. **Spillerstyring (`Player`)**:
   - Hver spiller har et navn, en saldo af credits og et inventory.
   - Kan optjene/tilføjes credits og bruge credits på kister.
2. **Loot Crates (`LootCreate`)**:
   - Forskellige kister med unikke navne, sjældenheder (*Common*, *Rare*, *Legendary*) og priser.
   - Trækker tilfældige genstande ud fra kistens sjældenhed (f.eks. *Wooden Shield*, *Steel Sword*, *Mythic Dragon Blade*).

### Gennemførte udvidelser (Ekstra features)
* 🎒 **Spiller Inventory & Salg**: Genstande vundet fra crates tilføjes automatisk til spillerens inventory. Genstande kan efterfølgende sælges for credits.
* 💻 **Interaktiv CLI-menu (`Scanner`)**: Fuld tekstbaseret konsolmenu med 9 valgmuligheder og fuld inputvalidering.
* 💾 **Persistering i `.txt`-fil**: Alle spillere, deres credits og inventory gemmes i `players.txt` (format: `Navn;Credits;Item1,Item2`) og indlæses automatisk ved opstart.
* 🎨 **JavaFX Brugerflade**: Grafisk frontend med valg af spiller, kisteåbning, salg af items, logkonsol og visuelle fejlmeddelelser via JavaFX `Alert` dialoger.
* 🧪 **Automatiserede Exception-tests**: Indbygget test-suite, der med ét klik verificerer alle fejlscenarier.

---

## ⚠️ Exceptions i systemet

| Exception | Type | Hvornår kastes den? |
|---|---|---|
| **`NotEnoughCreditsException`** | Checked (`extends Exception`) | Når en spiller forsøger at åbne en kiste, som de ikke har råd til. |
| **`PlayerNotFoundException`** | Checked (`extends Exception`) | Når der søges efter en spiller via navn eller objekt, som ikke findes i systemet. |
| **`ItemNotFoundException`** | Checked (`extends Exception`) | Når en spiller forsøger at sælge en genstand, som ikke findes i deres inventory. |
| **`NegativeAmountException`** | Unchecked (`extends RuntimeException`) | Når der forsøges at tilføje et negativt beløb credits. |

---

## 📁 Projektstruktur

```text
Feedbackopgave LootCrate/
├── src/
│   ├── exceptions/
│   │   ├── ItemNotFoundException.java
│   │   ├── NegativeAmountException.java
│   │   ├── NotEnoughCreditsException.java
│   │   └── PlayerNotFoundException.java
│   ├── models/
│   │   ├── LootCreate.java
│   │   └── Player.java
│   ├── utils/
│   │   └── PlayerFileManager.java
│   ├── gui/
│   │   ├── LootCreateApp.java
│   │   └── LootCreateAppLauncher.java
│   └── LootCreateSystem.java
├── javafx-sdk-21.0.2/              # JavaFX SDK biblioteker
├── players.txt                     # Datafil til spillere og inventory
├── pom.xml                         # Maven build-konfiguration
└── README.md
```

---

## 🚀 Kørselsvejledning

### 1. Kør JavaFX GUI'en (Anbefalet)
* **I IntelliJ IDEA**:
  1. Højreklik på `src/gui/LootCreateAppLauncher.java` og vælg **Run 'LootCreateAppLauncher.main()'**.
  2. *Eller:* Vælg den foruddefinerede kørselsprofil **`LootCreateApp (GUI)`** øverst til højre og tryk på den grønne Play-knap.
* **Via Maven**:
  ```bash
  mvn clean javafx:run
  ```

### 2. Kør Terminal/CLI-versionen
* **I IntelliJ IDEA**:
  * Højreklik på `src/LootCreateSystem.java` og vælg **Run 'LootCreateSystem.main()'**.
* **Via Terminal**:
  ```bash
  javac -d out/production src/exceptions/*.java src/models/*.java src/utils/*.java src/LootCreateSystem.java
  java -cp out/production LootCreateSystem
  ```

---

## 💡 Refleksionsspørgsmål & Teori

### 1. Hvad er forskellen på checked og unchecked exceptions?
* **Checked Exceptions** (arver fra `Exception`, men ikke `RuntimeException`):
  * Java-compileren tvinger programmøren til eksplicit at håndtere dem med `try-catch` eller deklarere dem i metodens signatur med `throws`.
  * Bruges til situationer, som systemet realistisk kan forudse og komme sig over (f.eks. manglende credits, en spiller der ikke findes, eller en manglende fil).
* **Unchecked Exceptions** (arver fra `RuntimeException`):
  * Compileren kræver ikke eksplicit håndtering.
  * Bruges typisk til logiske programmeringsfejl eller ugyldige argumenter (f.eks. `NegativeAmountException`, `NullPointerException` eller `IllegalArgumentException`).

### 2. Hvornår skal man selv lave en exception-klasse?
Man bør lave sine egne custom exception-klasser, når:
1. De indbyggede Java exceptions (som fx `IllegalArgumentException` eller `Exception`) ikke er præcise nok til at forklare domænets fejl.
2. Man vil tillade det kaldende system at fange præcis denne type fejl separat (f.eks. fange `NotEnoughCreditsException` for at vise en *"Fyld din saldo op"* knap).
3. Det gør koden og fejlbeskederne mere læsbare og selvforklarende.

### 3. Hvad sker der, hvis du ikke fanger en exception?
Hvis en exception kastes og ikke fanges i en `try-catch`-blok, bobler den op igennem kaldstakken (*call stack*). Hvis den når hele vejen op til `main()` uden at blive fanget:
* Tråden/programmet terminerer øjeblikkeligt.
* Der udskrives et *stack trace* i konsollen, og programmet crasher for brugeren.

### 4. Hvordan ville systemet se ud uden exception handling?
Uden exceptions ville man være tvunget til at bruge defensive returværdier (f.eks. returnere `false`, `-1` eller `null` ved fejl) eller fejlkoder. 
Dette har flere ulemper:
* **Forurenet forretningslogik**: Metoder skal hele tiden bruge indlejrede `if-else` tjek for at se, om det forrige kald fejlede.
* **Tavse fejl**: Hvis en programmør glemmer at tjekke en returværdi, fortsætter programmet i en korrupt tilstand (f.eks. trækker penge uden at give en kiste).
* **Ingen kontekst**: Returkoder giver sjældent en meningsfuld fejlbesked om, *hvorfor* handlingen fejlede.
