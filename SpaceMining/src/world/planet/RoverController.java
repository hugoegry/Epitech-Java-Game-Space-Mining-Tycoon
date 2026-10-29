package world.planet;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import utils.InteractiveZone;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Contrôle le rover visible sur les planètes de type « ferme ».
 *
 * <p>
 * Ce contrôleur gère :
 * <ul>
 *     <li>Le déplacement du rover le long d’un chemin prédéfini (type spline).</li>
 *     <li>Les entrées clavier (Q/D pour bouger, E pour interagir).</li>
 *     <li>Les collisions avec les objets {@link InteractiveZone}.</li>
 *     <li>L’exécution d’actions contextuelles lorsque le rover interagit avec une zone.</li>
 * </ul>
 *
 * <p>
 * Le mouvement repose sur un paramètre normalisé {@code t ∈ [0,1]} représentant
 * la progression sur le chemin. L’interpolation est linéaire entre les points.
 * </p>
 *
 * <p><b>Note :</b> Le rover ne tourne pas en fonction de sa direction.
 * Seule la position est mise à jour. Toute gestion d’orientation ou d’animation
 * doit être ajoutée séparément si nécessaire.</p>
 */
public class RoverController {

     /**
     * Chemin de navigation par défaut du rover.
     * Tous les calculs de déplacement et d’interpolation se basent sur cette liste.
     * Les coordonnées doivent être exprimées dans le même repère que le {@link Pane} racine.
     */
    private final List<Point2D> DEFAULT_PATH = List.of(
             new Point2D(583, 620),
             new Point2D(645, 576),
             new Point2D(732, 537),
             new Point2D(791, 517),
             new Point2D(847, 502),
             new Point2D(881, 499),
             new Point2D(940, 498),
             new Point2D(1008, 498),
             new Point2D(1073, 508)
    );

    private AnimationTimer loop;
    private final Set<KeyCode> keys = new HashSet<>();

    private final List<Point2D> path = DEFAULT_PATH;
    private final double speed = 150;

    private final ImageView sprite;
    private double t = 0;

    private final InteractiveController interactions;

    /**
     * Crée un nouveau contrôleur de rover et ajoute son sprite au panneau fourni.
     *
     * @param root Le panneau servant de conteneur visuel pour le rover.
     *             Le {@link ImageView} du rover y est automatiquement ajouté.
     *
     * @param inter Le contrôleur d’interactions utilisé pour détecter
     *              et gérer les collisions avec les zones interactives.
     *
     * @throws RuntimeException si la ressource du sprite du rover ne peut pas être chargée.
     */
    public RoverController(Pane root, InteractiveController inter) {
        this.interactions = inter;

        sprite = new ImageView(new Image(getClass().getResourceAsStream("/assets/roverV1.png")));
        sprite.setFitWidth(118);
        sprite.setFitHeight(118);

        Point2D p = pointOnPath(0);
        sprite.setLayoutX(p.getX());
        sprite.setLayoutY(p.getY());
        root.getChildren().add(sprite);

        loop = new AnimationTimer() {
            private long last = 0;
            @Override
            public void handle(long now) {
                if (last > 0) update((now - last)/1e9);
                last = now;
            }
        };
    }

    /**
     * Démarre la boucle d’animation du rover.
     */
    public void start() { loop.start(); }

    /**
     * Arrête la boucle d’animation du rover.
     */
    public void stop() { loop.stop(); }

    /**
     * Enregistre un appui sur une touche pour le déplacement ou une interaction.
     *
     * @param k la touche pressée.
     */
    public void onKeyPressed(KeyCode k) { keys.add(k); }

    /**
     * Enregistre le relâchement d’une touche.
     *
     * @param k la touche relâchée.
     */
    public void onKeyReleased(KeyCode k) { keys.remove(k); }

    /**
     * Met à jour le déplacement du rover et vérifie les interactions.
     * Le mouvement dépend des touches 'Q' et 'D'.
     *
     * @param dt Temps écoulé depuis la dernière frame (en secondes).
     */
    private void update(double dt) {
        double dir = 0;
        if (keys.contains(KeyCode.Q)) dir = -1;
        if (keys.contains(KeyCode.D)) dir = 1;

        // gerer le sens du sprite \\
        if (dir > 0) sprite.setScaleX(-1);
        else if (dir < 0) sprite.setScaleX(1);

        t += (speed * dt * dir) / totalLength();
        t = Math.max(0, Math.min(1, t));

        Point2D p = pointOnPath(t);
        sprite.setLayoutX(p.getX());
        sprite.setLayoutY(p.getY());

        for (InteractiveZone iz : interactions.getZones()) {
            if (sprite.getBoundsInParent().intersects(iz.rect.getBoundsInParent())) {
                iz.rect.setFill(javafx.scene.paint.Color.color(0,1,0,0.4));
                if (keys.contains(KeyCode.E)) {
                    iz.action.run();
                    keys.remove(KeyCode.E);
                }
            } else {
                iz.rect.setFill(javafx.scene.paint.Color.color(1,1,0,0.25));
            }
        }
    }

    /**
     * Calcule la longueur totale du chemin de navigation.
     *
     * @return La somme de toutes les longueurs de segments.
     */
    private double totalLength() {
        double sum = 0;
        for (int i=0; i<path.size()-1; i++)
            sum += path.get(i).distance(path.get(i+1));
        return sum;
    }

    /**
     * Calcule la position du rover pour une valeur normalisée {@code t ∈ [0,1]}.
     *
     * <p>
     * Réalise une interpolation linéaire entre chaque paire de points
     * jusqu’à atteindre la longueur d’arc cible.
     * </p>
     *
     * @param t La position normalisée sur le chemin.
     *
     * @return Le point interpolé correspondant.
     */
    private Point2D pointOnPath(double t) {
        double L = totalLength();
        double target = t * L;
        double acc = 0;
        for (int i=0;i<path.size()-1;i++) {
            Point2D a = path.get(i), b = path.get(i+1);
            double seg = a.distance(b);
            if (acc + seg >= target){
                double alpha = (target - acc)/seg;
                return a.multiply(1-alpha).add(b.multiply(alpha));
            }
            acc += seg;
        }
        return path.get(path.size()-1);
    }
}
