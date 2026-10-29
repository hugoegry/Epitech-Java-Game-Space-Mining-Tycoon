package ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import screens.GameScene;
import world.planet.Planet;
import static game.GameState.currentState;


/**
 * Menu permettant au joueur de choisir une planète sur laquelle atterrir.
 * <p>
 * Ce menu hérite de {@link GameMenu} et liste toutes les planètes disponibles
 * via {@link Planet#getPlanets()}, en indiquant la ressource principale de chacune.
 * <p>
 * Pour chaque planète, un bouton permet de voyager si le niveau de recherche du joueur
 * est suffisant et que le joueur n'y est pas déjà.
 */
public class TravelMenu extends GameMenu {

    /** Conteneur pour les éléments de planètes affichés dans le menu. */
    private final VBox planetsBox = new VBox(10.0);

    /**
     * Crée un menu de voyage pour un jeu donné.
     *
     * @param game L'instance de {@link GameScene} permettant de gérer le voyage.
     */
    public TravelMenu(GameScene game) {
        super("Choose a landing site");

        this.planetsBox.setAlignment(Pos.CENTER);
        this.getChildren().add(planetsBox);

        Button close = new Button("Close");
        close.setOnAction((e) -> this.hide());
        close.setStyle("-fx-font-size: 18px;-fx-padding: 8 20;-fx-background-color: linear-gradient(to bottom, #330000, #660000);-fx-text-fill: white;-fx-background-radius: 15;-fx-effect: dropshadow(gaussian, black, 10, 0.4, 0, 0);");

        this.getChildren().add(close);

        refresh(game);
    }

    /**
     * Met à jour la liste des planètes affichées dans le menu.
     * <p>
     * Chaque planète affiche son nom, sa ressource principale, et un bouton pour voyager.
     * Les boutons peuvent être désactivés si :
     * <ul>
     *     <li>Le joueur n'a pas le niveau de recherche requis pour la planète</li>
     *     <li>Le joueur se trouve déjà sur la planète</li>
     * </ul>
     *
     * @param game L'instance de {@link GameScene} utilisée pour initier le voyage.
     */
    public void refresh(GameScene game) {
        this.planetsBox.getChildren().clear();
        for (Planet planet : Planet.getPlanets()) {
            Label nameLabel = new Label(planet.getName() + " - " + planet.getResource().getName());
            nameLabel.setStyle("-fx-font-size: 24px;-fx-text-fill: #00E5FF;-fx-font-weight: bold;-fx-effect: dropshadow(gaussian, #003344, 5, 0.5, 0, 0);");
            nameLabel.setAlignment(Pos.CENTER);
            nameLabel.setMaxWidth(Double.MAX_VALUE);

            Button travelBtn = new Button("Land on " + planet.getName());
            travelBtn.setStyle("-fx-font-size: 16px;-fx-padding: 8 25;-fx-background-color: linear-gradient(to bottom, #003344, #00AACC);-fx-text-fill: white;-fx-background-radius: 20;-fx-effect: dropshadow(gaussian, #003344, 8, 0.4, 0, 1);");

            if (!currentState.canAccessPlanet(planet.getRequiredLevel())) {
                travelBtn.setDisable(true);
                travelBtn.setText("Locked");
            } else {
                travelBtn.addEventFilter(MouseEvent.MOUSE_RELEASED, (e) -> {
                    if (!travelBtn.isHover()) e.consume();
                });
                if (currentState.currentPlanet == planet) {
                    travelBtn.setDisable(true);
                    travelBtn.setText("Already here");
                }
                travelBtn.setOnAction((e) -> {
                    game.startTravelTo(planet);
                    this.hide();
                });
            }

            VBox card = new VBox(10.0, nameLabel, travelBtn);
            card.setAlignment(Pos.CENTER);
            card.setStyle("-fx-background-color: rgba(0, 20, 40, 0.6);-fx-background-radius: 15;-fx-border-radius: 15;-fx-border-color: rgba(0, 255, 255, 0.3);-fx-border-width: 1.5;-fx-padding: 20;-fx-effect: dropshadow(gaussian, black, 20, 0.3, 0, 0);");

            this.planetsBox.getChildren().add(card);
        }
    }
}
