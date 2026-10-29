package world.planet;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import world.asset.DecorData;
import java.util.ArrayList;
import java.util.List;

/**
 * Chargeur responsable de la création et de l’assemblage de tous les assets visuels
 * nécessaires au rendu d’un environnement planétaire.
 *
 * <p>
 * Cette classe prend en charge :
 * <ul>
 *     <li>le chargement de l’image d’arrière-plan ;</li>
 *     <li>l’intégration des éléments de décor placés devant ou derrière l’arrière-plan ;</li>
 *     <li>la gestion automatique de la taille et de la position des nœuds graphiques.</li>
 * </ul>
 *
 * <p>
 * Le chargeur construit un {@link Pane} entièrement prêt à être intégré dans le
 * graphe de scène JavaFX. Aucun système de cache n’est utilisé : chaque appel
 * reconstruit complètement l’arborescence graphique.
 * </p>
 *
 * <p><b>Important :</b> tous les chemins de ressources doivent pointer vers des
 * fichiers disponibles dans le classpath de l’application
 * (ex. dans <code>/resources</code>).</p>
 */
public class PlanetAssetsLoader {

    /**
     * Charge et assemble l’ensemble des composants graphiques constituant le
     * décor d’une planète.
     *
     * @param bgPath     chemin vers la ressource de l’arrière-plan.
     *                   Si {@code null}, aucun fond n’est ajouté.
     * @param decorList  liste d’objets décrivant les éléments de décor.
     *                   Chaque élément doit être une instance de {@link DecorData}.
     *                   Peut être {@code null}.
     * @param W          largeur virtuelle de la zone de rendu.
     * @param H          hauteur virtuelle de la zone de rendu.
     *
     * @return un {@link Pane} entièrement assemblé, contenant les éléments graphiques
     *         dans un ordre garantissant un rendu correct.
     *
     * @throws IllegalArgumentException si un élément de décor n’est pas de type {@link DecorData}.
     * @throws RuntimeException si un asset graphique ne peut pas être chargé
     *                          (chemin invalide ou ressource manquante).
     *
     * <p><b>Ordre de rendu :</b></p>
     * <ol>
     *     <li>Éléments de décor situés derrière l’arrière-plan.</li>
     *     <li>L’image d’arrière-plan (si fournie).</li>
     *     <li>Éléments de décor situés au-dessus de l’arrière-plan.</li>
     * </ol>
     */
    public Pane load(String bgPath, List<?> decorList, double W, double H) {
        Pane root = new Pane();
        root.setPrefSize(W, H);

        ImageView bgView = null;
        if (bgPath != null) {
            Image bg = new Image(getClass().getResourceAsStream(bgPath));
            bgView = new ImageView(bg);
            bgView.setFitWidth(W);
            bgView.setFitHeight(H);
        }

        List<ImageView> decorBehind = new ArrayList<>();
        List<ImageView> decorFront = new ArrayList<>();

        if (decorList != null) {
            for (Object obj : decorList) {
                if (obj instanceof DecorData d) {
                    ImageView v = new ImageView(new Image(getClass().getResourceAsStream(d.path)));
                    v.setLayoutX(d.x);
                    v.setLayoutY(d.y);
                    if (d.width > 0) v.setFitWidth(d.width);
                    if (d.height > 0) v.setFitHeight(d.height);

                    if (d.aboveBackground) decorFront.add(v);
                    else decorBehind.add(v);
                }
            }
        }

        // add dans le bon ordre
        for (ImageView v : decorBehind) root.getChildren().add(v);
        if (bgView != null) root.getChildren().add(bgView);
        for (ImageView v : decorFront) root.getChildren().add(v);

        return root;
    }
}
