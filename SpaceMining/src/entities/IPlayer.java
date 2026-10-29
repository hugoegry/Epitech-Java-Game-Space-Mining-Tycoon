package entities;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import java.util.Set;

/**
 * Interface représentant les fonctionnalités principales d'un joueur dans le jeu.
 * <p>
 * Permet de définir la position, gérer les entrées utilisateur et déplacer le joueur
 * dans le monde en tenant compte de la physique et des collisions.
 * </p>
 */
public interface IPlayer {

    /**
     * Retourne la position actuelle du joueur.
     *
     * @return la position sous forme de Point2D
     */
    Point2D getPosition();

    /**
     * Définit la position du joueur.
     *
     * @param newPosition nouvelle position du joueur
     */
    void setPosition(Point2D newPosition);

    /**
     * Définit la position du joueur via des coordonnées X et Y.
     *
     * @param x coordonnée X
     * @param y coordonnée Y
     */
    void setPosition(double x, double y);

    /**
     * Définit les touches actuellement pressées par le joueur.
     * <p>
     * Ces entrées seront utilisées pour calculer le mouvement lors de l'appel à {@link #move(double)}.
     * </p>
     *
     * @param keys ensemble des touches pressées (KeyCode)
     */
    void setInputs(Set<KeyCode> keys);

    /**
     * Déplace le joueur selon les entrées actuelles et le temps écoulé.
     * <p>
     * Applique l'accélération, la friction et limite la vitesse maximale.
     * Gère également les collisions avec le monde.
     * </p>
     *
     * @param dt temps écoulé depuis le dernier appel, en secondes
     */
    void move(double dt);
}
