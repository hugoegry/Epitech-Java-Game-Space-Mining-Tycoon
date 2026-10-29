package tools;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class wallEditor {

    private final List<Point2D> currentPolygon = new ArrayList<>();
    private final List<List<Point2D>> finishedPolygons = new ArrayList<>();

    private boolean enterPressedLastFrame = false;
    private boolean leftMouseLastFrame = false;
    private boolean rightMouseLastFrame = false;

    public wallEditor() {}

    // Update avec coordonnées souris
    public void update(Set<KeyCode> keys, boolean leftClick, boolean rightClick, double mouseX, double mouseY) {

        // Ajouter point clic gauche "just pressed"
        if (leftClick && !leftMouseLastFrame) {
            currentPolygon.add(new Point2D(mouseX, mouseY));
            System.out.println("Point added: " + mouseX + ", " + mouseY);
        }
        leftMouseLastFrame = leftClick;

        // Supprimer dernier point clic droit "just pressed"
        if (rightClick && !rightMouseLastFrame && !currentPolygon.isEmpty()) currentPolygon.remove(currentPolygon.size() - 1);

        rightMouseLastFrame = rightClick;

        // Fermer polygone ENTER
        if (keys.contains(KeyCode.ENTER) && !enterPressedLastFrame && currentPolygon.size() >= 3) {
            finishedPolygons.add(new ArrayList<>(currentPolygon));

            // Génération float[]
            StringBuilder sb = new StringBuilder("new float[]{");
            for (Point2D p : currentPolygon)
                sb.append((int)p.getX()).append(", ").append((int)p.getY()).append(", ");
            sb.append("}");
            System.out.println("Polygon GENERATED → " + sb);

            currentPolygon.clear();
        }
        enterPressedLastFrame = keys.contains(KeyCode.ENTER);
    }

    // Render
    public void render(GraphicsContext gc, double mouseX, double mouseY, boolean leftClick) {

        // Polygones finis → vert
        gc.setStroke(javafx.scene.paint.Color.GREEN);
        for (List<Point2D> poly : finishedPolygons)
            drawPolygon(gc, poly);

        // Polygone en cours → rouge
        gc.setStroke(javafx.scene.paint.Color.RED);
        drawPolygon(gc, currentPolygon);

        // Points
        gc.setFill(javafx.scene.paint.Color.RED);
        for (Point2D p : currentPolygon)
            gc.fillOval(p.getX() - 3, p.getY() - 3, 6, 6);
    }

    private void drawPolygon(GraphicsContext gc, List<Point2D> poly) {
        if (poly.size() < 2) return;
        for (int i = 0; i < poly.size() - 1; i++) {
            Point2D a = poly.get(i);
            Point2D b = poly.get(i + 1);
            gc.strokeLine(a.getX(), a.getY(), b.getX(), b.getY());
        }
    }

    public List<List<Point2D>> getFinishedPolygons() {
        return finishedPolygons;
    }
}
