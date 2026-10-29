package utils;

import javafx.scene.shape.Rectangle;

/**
 * Représente une zone interactive à l'écran qui déclenche une action lorsqu'elle est utilisée.
 * <p>
 * Chaque zone est définie par un {@link Rectangle} représentant sa position et sa taille,
 * ainsi qu'une action {@link Runnable} à exécuter lorsqu'elle est activée.
 */
public class InteractiveZone {

    /** Rectangle représentant la position et la taille de la zone interactive. */
    public Rectangle rect;

    /** Action à exécuter lorsque la zone est activée. */
    public Runnable action;

    /**
     * Crée une nouvelle zone interactive.
     *
     * @param rect   Rectangle définissant la taille et la position de la zone.
     * @param action Action à exécuter lorsque la zone est utilisée.
     */
    public InteractiveZone(Rectangle rect, Runnable action) {
        this.rect = rect;
        this.action = action;
    }
}
