package world.planet;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import utils.InteractiveZone;
import java.util.ArrayList;
import java.util.List;
import static game.GameState.DEBUG;

/**
 * Gère toutes les zones interactives présentes sur une planète.
 *
 * <p>
 * Ce contrôleur :
 * <ul>
 *     <li>Enregistre et installe visuellement les zones interactives.</li>
 *     <li>Déclenche les actions associées lorsqu'un clic survient dans une zone.</li>
 *     <li>Permet à d'autres systèmes (comme le rover) d'accéder à la liste des zones.</li>
 * </ul>
 *
 * <p>
 * Chaque zone interactive est représentée par un objet {@link InteractiveZone}
 * contenant :
 * <ul>
 *     <li>Une zone graphique (rectangle JavaFX).</li>
 *     <li>Une action exécutée lors de l'interaction.</li>
 * </ul>
 *
 * <p><b>Note :</b> Ce contrôleur ne gère ni le survol, ni les animations, ni les effets visuels
 * avancés. Seules la mise en place et la détection de clics sont prises en charge.</p>
 */
public class InteractiveController {

    private final List<InteractiveZone> zones = new ArrayList<>();

    /**
     * Crée un contrôleur d'interactions et ajoute toutes les zones fournies
     * au panneau graphique.
     *
     * @param root  Le conteneur graphique dans lequel les zones doivent être affichées.
     * @param input La liste des zones interactives à enregistrer. Peut être {@code null}.
     *
     * <p>
     * Chaque zone est automatiquement colorée avec un jaune semi-transparent
     * afin d'être visible visuellement (pour le débogage ou la mise au point).
     * </p>
     */
    public InteractiveController(Pane root, List<InteractiveZone> input) {
        if (input != null) {
            for (InteractiveZone iz : input) {
                zones.add(iz);
                if (!DEBUG) continue;
                iz.rect.setFill(Color.color(1,1,0,0.25));
                root.getChildren().add(iz.rect);
            }
        }
    }

    /**
     * Traite un clic exprimé en coordonnées virtuelles.
     * Si le point se trouve dans une zone interactive, l'action associée est exécutée.
     *
     * @param virtPoint Le point cliqué, dans le repère virtuel (non mis à l'échelle).
     */
    public void handleClick(Point2D virtPoint) {
        for (InteractiveZone iz : zones) if (iz.rect.contains(virtPoint)) if (iz.action != null) iz.action.run();
    }

    /**
     * Retourne la liste des zones interactives enregistrées.
     *
     * @return Une liste non modifiable de zones interactives.
     */
    public List<InteractiveZone> getZones() { return this.zones; }
}
