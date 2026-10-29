
package ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Classe abstraite représentant un menu de jeu générique.
 * <p>
 * Ce menu est un conteneur vertical {@link VBox} stylisé avec :
 * <ul>
 *     <li>Un titre affiché en haut du menu</li>
 *     <li>Un fond semi-transparent et une bordure blanche</li>
 *     <li>Espacement automatique entre les éléments</li>
 * </ul>
 * <p>
 * Les sous-classes peuvent ajouter leurs propres boutons ou éléments
 * interactifs à ce menu.
 */
public abstract class GameMenu extends VBox {

    /**
     * Crée un menu avec un titre spécifique.
     *
     * @param title Le texte à afficher comme titre du menu.
     */
    public GameMenu(String title) {
        this.setAlignment(Pos.CENTER);
        this.setSpacing((double)20.0F);
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 40px; -fx-border-color: white; -fx-border-width: 2;");
        this.setPrefWidth((double)400.0F);
        this.setPrefHeight((double)300.0F);
        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(new Font((double)28.0F));
        this.getChildren().add(titleLabel);
    }

    /** Affiche le menu en le rendant visible. */
    public void show() {
        this.setVisible(true);
    }

    /** Masque le menu en le rendant invisible. */
    public void hide() {
        this.setVisible(false);
    }
}
