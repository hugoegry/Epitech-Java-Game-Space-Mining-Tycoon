package world.planet;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.geometry.Point2D;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import java.security.cert.PolicyNode;

/**
 * Gère la création, l’affichage et la mise à l’échelle adaptative
 * d’une vue de planète dans une scène JavaFX.
 *
 * <p>
 * Cette classe encapsule :
 * <ul>
 *     <li>Un conteneur racine représentant l’espace virtuel 1920×1080.</li>
 *     <li>Un groupe capable d’être mis à l’échelle dynamiquement.</li>
 *     <li>Un conteneur externe permettant de centrer et ajuster la vue.</li>
 * </ul>
 *
 * <p>
 * L’objectif principal est de garantir que la scène virtuelle conserve ses proportions,
 * peu importe la taille de la fenêtre réelle.
 * </p>
 */
public class PlanetView {

    private Pane rootPane;
    private Group scaled;
    private Pane container;
    private Scene scene;

    /**
     * Initialise la hiérarchie graphique de la vue.
     *
     * @param root Le panneau racine représentant la scène virtuelle (1920×1080).
     */
    public void initialize(Pane root) {
        this.rootPane = root;
        this.scaled = new Group(rootPane);
        this.container = new Pane(scaled);
    }

    /**
     * Construit la scène JavaFX contenant la vue de la planète.
     *
     * @return Une scène nouvellement créée de taille 1920×1080, automatiquement mise à l’échelle.
     */
    public Scene buildScene() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        this.scene = new Scene(container, bounds.getWidth(), bounds.getHeight());
        scene.widthProperty().addListener((obs, o, n) -> adjustScale());
        scene.heightProperty().addListener((obs, o, n) -> adjustScale());
        adjustScale();
        return this.scene;
    }

    /**
     * Convertit des coordonnées écran en coordonnées virtuelles (avant mise à l’échelle).
     *
     * @param sx Position X en coordonnées écran.
     * @param sy Position Y en coordonnées écran.
     * @return Un point exprimé dans l’espace virtuel (coordonnées logiques).
     */
    public Point2D toVirtual(double sx, double sy) {
        return rootPane.sceneToLocal(sx, sy);
    }

    /**
     * Ajuste dynamiquement l’échelle et le centrage de la vue
     * pour conserver les proportions du rendu virtuel 1920×1080.
     * <p>
     * Cette méthode doit être appelée lorsqu’une variation de taille
     * de la fenêtre est détectée.
     * </p>
     */
    public void adjustScale() {
        if (scene == null) return;

        double sw = scene.getWidth();
        double sh = scene.getHeight();

        double scale = Math.min(sw / 1920, sh / 1080);

        scaled.setScaleX(scale);
        scaled.setScaleY(scale);

        double ox = (sw - 1920 * scale) / 2;
        double oy = (sh - 1080 * scale) / 2;

        container.setLayoutX(ox);
        container.setLayoutY(oy);
    }

    public Pane getRootPane() {
        return this.rootPane;
    }

}