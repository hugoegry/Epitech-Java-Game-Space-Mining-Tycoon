package utils;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.Random;
import static game.GameState.DEBUG;

/**
 * Classe utilitaire pour l'affichage visuel des {@link InteractiveZone}.
 * <p>
 * Gère le rendu d'un halo pulsant autour de la zone et éventuellement d'icônes/particules.
 */
public class InteractiveZoneView {

    private static double pulseTimer = 0;
    private static final Random rnd = new Random();

    /**
     * Affiche toutes les zones interactives sur le contexte graphique.
     *
     * @param gc la surface graphique
     * @param zones liste de zones interactives
     */
    public static void render(GraphicsContext gc, Iterable<InteractiveZone> zones) {
        render(gc, zones, 1.0 / 60.0);
    }

    public static void render(GraphicsContext gc, Iterable<InteractiveZone> zones, double dt) {
        pulseTimer += Math.max(0, Math.min(dt, 0.05));

        for (InteractiveZone z : zones) {
            Rectangle r = z.rect;
            double centerX = r.getX() + r.getWidth()/2;
            double centerY = r.getY() + r.getHeight()/2;

            // Halo pulsant
            double pulse = 0.5 + Math.sin(pulseTimer * 2) * 0.5;
            double haloRadius = Math.max(r.getWidth(), r.getHeight()) * 1.2;

            gc.setFill(Color.color(1, 0.5, 0, 0.2 * pulse));
            gc.fillOval(centerX - haloRadius/2, centerY - haloRadius/2, haloRadius, haloRadius);

            if (DEBUG) { // Contour de la zone \\
                gc.setStroke(Color.YELLOW);
                gc.setLineWidth(2);
                gc.strokeRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
            }

            // Icône
            gc.setFill(Color.color(1, 1, 0, 0.8));
            gc.fillText("✦", centerX - 10, centerY + 10);
        }
    }

    /** Réinitialise le timer (utile pour changer de scène) */
    public static void resetPulseTimer() { pulseTimer = 0; }
}
