package world;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import entities.Player;
import javafx.scene.canvas.GraphicsContext;
import java.util.Random;
import static game.GameState.DEBUG;

/**
 * Classe utilitaire pour l'affichage et les effets visuels des {@link Zone} dans le jeu.
 * <p>
 * Gère le rendu des boîtes de collision, des halos pulsants autour des zones, des icônes et
 * éventuellement des particules de guidage lorsque le joueur est loin.
 * La classe conserve une zone survolée globalement via une référence statique.
 */
public class ZoneView {

    /** Timer utilisé pour créer un effet de pulsation des halos. */
    private static double pulseTimer = 0;

    /** Générateur aléatoire pour les effets de particules. */
    private static Random rnd = new Random();

    /** Zone actuellement survolée par le joueur. */
    private static Zone hoveredZone = null;

    /**
     * Retourne la zone actuellement survolée.
     * @return la zone survolée, ou null si aucune
     */
    public static Zone getHoveredZone() { return hoveredZone; }

    /** Réinitialise la zone survolée (aucune zone survolée). */
    public static void setHoveredZone() { hoveredZone = null; }

    /**
     * Définit une zone comme zone actuellement survolée.
     * @param z la zone à survoler
     */
    public static void setHoveredZone(Zone z) { hoveredZone = z; }

    /**
     * Rend toutes les zones et effets associés sur le {@link GraphicsContext}.
     *
     * @param gc le contexte graphique utilisé pour le rendu
     * @param player le joueur utilisé pour les effets dépendant de la distance
     */
    public static void render(GraphicsContext gc, Player player) {
        render(gc, player, 1.0 / 60.0);
    }

    /** Render avec vrai dt (evite un pulse qui derive si plusieurs loops tournent). */
    public static void render(GraphicsContext gc, Player player, double dt) {
        renderZoneBox(gc);
        renderZoneEffects(Math.max(0, Math.min(dt, 0.05)), gc, player);
    }

    /**
     * Dessine les contours des zones et la zone survolée (si DEBUG activé).
     *
     * @param gc le contexte graphique utilisé pour le rendu
     */
    private static void renderZoneBox(GraphicsContext gc) {
        if (!DEBUG) return;
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(2);
        for (Zone z : Zone.getZones()) {
            gc.strokeRect(z.getX(), z.getY(), z.getWidth(), z.getHeight());
        }

        if (getHoveredZone() != null) {
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(2);
            gc.strokeRect(hoveredZone.getX(), hoveredZone.getY(), hoveredZone.getWidth(), hoveredZone.getHeight());
        }
    }

    /**
     * Rendu des effets visuels pour toutes les zones.
     * Comprend le halo pulsant et les icônes.
     *
     * @param dt temps écoulé depuis le dernier rendu (en secondes)
     * @param gc contexte graphique
     * @param player joueur pour les effets dépendant de la distance
     */
    private static void renderZoneEffects(double dt, GraphicsContext gc, Player player) { renderZoneEffects(dt, gc, player, false); }

    /**
     * Rendu des effets visuels pour toutes les zones, avec option particules de guidage.
     *
     * @param dt temps écoulé depuis le dernier rendu
     * @param gc contexte graphique
     * @param player joueur pour calculer la distance
     * @param activeParticuleEffect si vrai, dessine des particules guidant le joueur vers la zone
     */
    private static void renderZoneEffects(double dt, GraphicsContext gc, Player player, boolean activeParticuleEffect) {
        pulseTimer += dt;
        for (Zone z : Zone.getZones()) {
            double centerX = z.getX() + z.getWidth() / 2;
            double centerY = z.getY() + z.getHeight() / 2;

            // Halo \\
            double pulse = 0.5 + Math.sin(pulseTimer * 2) * 0.5;  // 0 - 1 \\
            double haloRadius = Math.max(z.getWidth(), z.getHeight()) * 0.9; // Taille du halo \\

            gc.setFill(Color.color(1, 1, 0, 0.10 * pulse)); // Couleur halo
            gc.fillOval(centerX - haloRadius / 2, centerY - haloRadius / 2, haloRadius, haloRadius);

            // Icon \\
            gc.setFill(Color.color(1, 1, 0, 0.8));
            gc.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 26));
            gc.fillText(z.getIcon(), centerX - 10, centerY + 10);

            // Particule effet si le joueur est trop loin \\
            if (!activeParticuleEffect) continue;
            double dist = player.getPosition().distance(centerX, centerY);

            if (dist > 200) drawGuidingParticles(centerX, centerY, dist, gc);
        }
    }

    /**
     * Dessine des particules guidant le joueur vers une zone éloignée.
     *
     * @param zx coordonnée X du centre de la zone
     * @param zy coordonnée Y du centre de la zone
     * @param dist distance joueur-zone
     * @param gc contexte graphique
     */
    private static void drawGuidingParticles(double zx, double zy, double dist, GraphicsContext gc) {
        int count = 10;

        for (int i = 0; i < count; i++) {
            double angle = rnd.nextDouble() * Math.PI * 2;
            double radius = 80 + rnd.nextDouble() * 40;

            double px = zx + Math.cos(angle) * radius;
            double py = zy + Math.sin(angle) * radius;

            double dx = (zx - px) * 0.07;
            double dy = (zy - py) * 0.07;

            // Couleur \\
            gc.setFill(Color.color(1, 1, 0, 0.4));
            gc.fillOval(px + dx, py + dy, 4, 4);
        }
    }

    /**
     * Réinitialise le timer de pulsation des halos à zéro.
     * Utile lorsque l'on change de scène ou de contexte graphique.
     */
    public static void resetpulseTimer() {
        pulseTimer = 0;
    }
}