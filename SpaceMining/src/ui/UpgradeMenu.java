package ui;

import entities.Tool;
import game.GameState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * Menu d'amélioration et d'achat d'outils du vaisseau.
 * <p>
 * Ce menu hérite de {@link GameMenu} et affiche tous les outils disponibles via {@link Tool#getTools()}.
 * Pour chaque outil, il affiche :
 * <ul>
 *     <li>Le nom et le niveau actuel si possédé</li>
 *     <li>Le coût d'achat ou de mise à niveau</li>
 *     <li>Un bouton "Buy" ou "Upgrade"</li>
 * </ul>
 * <p>
 * Les boutons sont désactivés si le joueur n'a pas assez de crédits pour l'action.
 * L'interface est automatiquement rafraîchie après chaque achat ou amélioration.
 */
public class UpgradeMenu extends GameMenu {

    /** Conteneur affichant tous les outils sous forme de cartes. */
    private final VBox toolsBox = new VBox(20);

    /** Référence à l'état actuel du jeu. */
    private final GameState gameState;

    /**
     * Crée le menu d'upgrade pour l'état de jeu donné.
     *
     * @param state L'instance de {@link GameState} utilisée pour accéder au vaisseau et aux crédits.
     */
    public UpgradeMenu(GameState state) {
        super("Upgrade Terminal");
        this.gameState = state;

        this.setPrefSize(960, 1080);
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(30);
        this.setPadding(new Insets(30, 0, 30, 0));
        this.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.2);" +
                        "-fx-border-color: rgba(0,255,255,0.3);" +
                        "-fx-border-width: 2 2 2 0;" +
                        "-fx-effect: dropshadow(gaussian, black, 30, 0.5, 0, 0);"
        );

        ScrollPane scrollPane = new ScrollPane(toolsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxHeight(700);
        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background: transparent;"
        );

        // Assurer que la viewport est transparente
        toolsBox.setStyle("-fx-background-color: transparent;");
        toolsBox.setFillWidth(false);
        toolsBox.setAlignment(Pos.TOP_CENTER);

        this.getChildren().add(scrollPane);

        refresh();
    }

    /**
     * Rafraîchit l'affichage des outils.
     * <p>
     * Cette méthode vide le conteneur et réaffiche toutes les cartes d'outils,
     * en tenant compte du niveau de possession et des crédits du joueur.
     */
    public void refresh() {
        toolsBox.getChildren().clear();
        for (Tool tool : Tool.getTools()) addToolCard(tool);
    }

    /**
     * Crée une carte graphique pour un outil donné et l'ajoute à {@link #toolsBox}.
     * <p>
     * La carte affiche le nom, le niveau ou l'état verrouillé, le coût, et un bouton
     * permettant d'acheter ou d'améliorer l'outil.
     *
     * @param tool L'outil à afficher.
     */
    private void addToolCard(Tool tool) {
        boolean owned = gameState.ship.getTools().contains(tool);
        float cost = owned ? (float) (tool.costForNext() * gameState.ship.getPourcentageRemise()) : tool.getPrixAchat()* gameState.ship.getPourcentageRemise();

        Label name = new Label(tool.getName() + (owned ? " (Level: " + tool.getLevel() + ")" : " (Locked)"));
        name.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #00E5FF;" +
                        "-fx-effect: dropshadow(gaussian, #003344, 5, 0.5, 0, 0);"
        );

        Label price = new Label((owned ? "Upgrade cost: " : "Buy cost: ") + cost + " credits");
        price.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        Button actionBtn = new Button(owned ? "Upgrade" : "Buy");
        styleButton(actionBtn, cost);

        actionBtn.setOnAction(e -> {
            if (gameState.spendCredits(cost)) {
                if (!owned) {
                    gameState.ship.addTool(tool);
                } else {
                    tool.upgrade();
                }
                refresh();
            }
        });

        VBox card = new VBox(10, name, price, actionBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(760);
        card.setMaxWidth(760);
        card.setStyle(
                "-fx-background-color: rgba(0, 20, 40, 0.6);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-radius: 15;" +
                        "-fx-border-color: rgba(0, 255, 255, 0.3);" +
                        "-fx-border-width: 1.5;" +
                        "-fx-effect: dropshadow(gaussian, black, 15, 0.3, 0, 0);"
        );

        toolsBox.getChildren().add(card);
    }

    /**
     * Applique le style visuel à un bouton d'achat ou d'amélioration.
     * <p>
     * Si le joueur n'a pas assez de crédits pour l'action, le bouton est désactivé
     * et son apparence est grisée.
     *
     * @param btn  Le bouton à styliser.
     * @param cost Le coût associé à l'action.
     */
    private void styleButton(Button btn, double cost) {
        btn.setStyle("-fx-font-size: 18px; -fx-padding: 8 25; -fx-background-radius: 15;");
        if (!game.GameState.DEBUG && gameState.credits < cost) {
            btn.setDisable(true);
            btn.setStyle(
                    "-fx-background-color: #222;" +
                            "-fx-text-fill: gray;" +
                            "-fx-font-size: 18px;" +
                            "-fx-padding: 8 25;" +
                            "-fx-background-radius: 15;"
            );
        } else {
            btn.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #003344, #00AACC);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 18px;" +
                            "-fx-padding: 8 25;" +
                            "-fx-background-radius: 15;"
            );
        }
    }
}
