package world.planet;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import world.planet.PlanetView;
import static game.GameState.DEBUG;

import java.util.ArrayList;
import java.util.List;

/**
 * Outil intégré pour créer/modifier le chemin du Rover directement sur la scène.
 * - Clique gauche : ajouter un point
 * - Clique droit : supprimer le dernier point
 * - Les points sont affichés et imprimés pour copier-coller dans DEFAULT_PATH
 */
public class RoverPathEditor {

    private final Pane pane;
    private final List<Point2D> points = new ArrayList<>();
    private final PlanetView view;

    public RoverPathEditor(Pane pane, PlanetView view) {
        this.pane = pane;
        this.view = view;
        initMouseListeners();
    }

    private void initMouseListeners() {
        if (!DEBUG) return;
        pane.setOnMouseClicked(e -> {
            Point2D virtual = view.toVirtual(e.getSceneX(), e.getSceneY());
            if (e.getButton() == MouseButton.PRIMARY) {
                addPoint(virtual.getX(), virtual.getY());
            } else if (e.getButton() == MouseButton.SECONDARY) {
                removeLastPoint();
            }
        });
    }

    private void addPoint(double x, double y) {
        Point2D p = new Point2D(x, y);
        points.add(p);

        Circle c = new Circle(x, y, 5, Color.RED);
        pane.getChildren().add(c);

        printPoints();
    }

    private void removeLastPoint() {
        if (!points.isEmpty()) {
            points.remove(points.size() - 1);
            pane.getChildren().remove(pane.getChildren().size() - 1);
            printPoints();
        }
    }

    private void printPoints() {
        System.out.println("List.of(");
        for (Point2D p : points) System.out.printf("    new Point2D(%.0f, %.0f),%n", p.getX(), p.getY());
        System.out.println(");");
    }

    /** Retourne la liste des points actuellement créés */
    public List<Point2D> getPoints() {
        return List.copyOf(points);
    }
}