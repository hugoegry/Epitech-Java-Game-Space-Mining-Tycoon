package ui;

import entities.Ressource;
import entities.Tool;
import game.GameState;
import javafx.animation.FadeTransition;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.*;
import world.planet.Planet;
import static game.GameState.currentState;


/**
 * <p>
 * HUD (Head-Up Display) gère l'ensemble des éléments graphiques affichés par-dessus la scène de jeu :
 * panneaux d'information, inventaire, outils, notifications temporaires, barre de minage, etc.
 * </p>
 *
 * <p>
 * Cette classe n'est pas statique mais expose plusieurs méthodes statiques pour faciliter l'accès
 * à une instance globale courante (pattern simplifié de type singleton).
 * Elle sert principalement de couche de présentation des données du {@link GameState}.
 * </p>
 */
public class HUD {

    /** Instance HUD active. */
    private static HUD currentHud;

    /**
     * Retourne l'instance HUD courante.
     * @return l'instance actuelle du HUD.
     */
    public static HUD getHud() { return currentHud; }

    /**
     * Définit l'instance HUD à utiliser comme HUD global.
     * @param hud nouvelle instance active.
     */
    public static void setHud(HUD hud) { HUD.currentHud = hud; }

    /** Lance l interface de minage via l'instance HUD courante. */
    public static void staticStartMining() {
        if (currentHud != null) currentHud.startMining();
    }

    private static final Color COLOR_TEXT = Color.rgb(180, 220, 255);
    private static final Color COLOR_FRAME = Color.rgb(60, 130, 255, 0.4);
    private static final Color COLOR_BG = Color.rgb(5, 15, 30, 0.55);
    private static final Font FONT_TITLE = Font.font("Consolas", 18);
    private static final Font FONT_NORMAL = Font.font("Consolas", 14);
    private static final Font FONT_SMALL = Font.font("Consolas", 12);

    /** Structure interne représentant une notification affichée temporairement dans la partie inférieure de l'écran. */
    private static class Notification {

        /** Message à afficher. */
        String text;

        /** Horodatage du début d'affichage. */
        long startTime;

        /** Durée totale d'affichage en millisecondes. */
        final long duration = 3000;

        /** Opacité actuelle. */
        float opacity = 1f;

        /**
         * Crée une notification avec le texte fourni.
         * @param t contenu textuel de la notification
         */
        Notification(String t) {
            this.text = t;
            this.startTime = System.currentTimeMillis();
        }
    }

    /** Liste des notifications actives. */
    private final List<Notification> notifications = new ArrayList<>();

    /** Indique si un processus de minage est en cours. */
    private boolean mining = false;

    /** Progression du minage (0 à 1). */
    private float miningProgress = 0;

    /** Timestamp du début de la session. */
    private long gameStartTime = System.currentTimeMillis();

    /**
     * Ajoute une notification au HUD.
     * @param msg texte de la notification à afficher.
     */
    public void addNotification(String msg) {
        notifications.add(new Notification(msg));
    }

    /**
     * Active l interface de minage (active l’affichage de la barre)
     * et remet la progression à zéro.
     */
    public void startMining() {
        mining = true;
        miningProgress = 0;
    }

    /**
     * Met à jour la progression du minage.
     * La valeur est automatiquement bornée entre 0 et 1.
     * @param p nouvelle progression du minage.
     */
    public void updateMiningProgress(float p) {
        miningProgress = Math.max(0, Math.min(1, p));
        if (miningProgress >= 1) mining = false;
    }

    /**
     * Stoppe immédiatement le minage et réinitialise l'état interne.
     */
    public void stopMining() {
        mining = false;
        miningProgress = 0;
    }

    /**
     * Rend l'ensemble du HUD sur le contexte graphique JavaFX fourni.
     * @param gc contexte graphique du Canvas
     */
    public void render(GraphicsContext gc) {
        renderInfoPanel(gc);
        renderTools(gc);
        renderInventory(gc);
        renderNotifications(gc);
        renderMiningBar(gc);
    }

    /**
     * Affiche le panneau principal d’informations système :
     * planète actuelle, niveau du joueur, crédits, inventaire, etc.
     * @param gc contexte graphique
     */
    private void renderInfoPanel(GraphicsContext gc) {
        gc.setFill(COLOR_BG);
        gc.fillRoundRect(15, 15, 420, 140, 15, 15);

        gc.setStroke(COLOR_FRAME);
        gc.strokeRoundRect(15, 15, 420, 140, 15, 15);

        gc.setFont(FONT_TITLE);
        gc.setFill(COLOR_TEXT);

        gc.fillText("⟢ SYSTEM STATUS", 30, 40);

        gc.setFont(FONT_NORMAL);
        gc.fillText("Planet : " + currentState.currentPlanet.getName(), 30, 70);
        gc.fillText("Player level : " + currentState.researchLevel, 30, 95);

        //long timePlayedSec = (System.currentTimeMillis() - gameStartTime) / 1000;
        //gc.fillText("Playtime : " + timePlayedSec + "s", 30, 120);

        gc.fillText("Credits : " + (game.GameState.DEBUG ? "INF" : String.valueOf((int) currentState.credits)), 250, 70);
        if (game.GameState.DEBUG) {
            gc.setFill(javafx.scene.paint.Color.ORANGE);
            gc.fillText("DEBUG ON", 250, 120);
            gc.setFill(COLOR_TEXT);
        }

        float w = currentState.ship.getCurrentWeight();
        float max = currentState.ship.getCapacity();
        gc.fillText("Cargo : " + w + " / " + max, 250, 95);
    }

    /**
     * Affiche la liste des outils équipés sur le vaisseau.
     * La hauteur du panneau s'adapte dynamiquement au nombre d’éléments.
     * @param gc contexte graphique
     */
    private void renderTools(GraphicsContext gc) {
        List<Tool> tools = currentState.ship.getTools();

        // Calcul dynamique de la hauteur
        int baseHeight = 60;    // marge + header
        int lineHeight = 22;    // hauteur par outil
        int itemCount = Math.max(1, tools.size());
        int dynamicHeight = baseHeight + (itemCount * lineHeight) + 20; // padding bas

        double x = 470;
        double y = 15;
        double w = 300;
        double h = dynamicHeight;

        // Fond du panneau
        gc.setFill(COLOR_BG);
        gc.fillRoundRect(x, y, w, h, 15, 15);

        // Bordure
        gc.setStroke(COLOR_FRAME);
        gc.strokeRoundRect(x, y, w, h, 15, 15);

        // Titre
        gc.setFont(FONT_TITLE);
        gc.setFill(COLOR_TEXT);
        gc.fillText("🛠 TOOLS", x + 20, y + 30);

        // Contenu
        gc.setFont(FONT_NORMAL);
        int drawY = (int) (y + 65);

        if (tools.isEmpty()) {
            gc.fillText("(none)", x + 20, drawY);
            return;
        }

        for (Tool t : tools) {
            gc.fillText("- " + t.getName() + " [LVL " + t.getLevel() + "]", x + 20, drawY);
            drawY += lineHeight;
        }
    }

    /**
     * Affiche l'inventaire du vaisseau (cargo).
     * La taille du panneau est calculée en fonction de la quantité d’items.
     * @param gc contexte graphique
     */
    private void renderInventory(GraphicsContext gc) {

        Map<Ressource, Integer> inv = currentState.ship.getInventory();

        // Calcul dynamique de la hauteur
        int baseHeight = 80;     // espace pour le header + marge
        int lineHeight = 22;     // hauteur par ligne d'item
        int itemCount = Math.max(1, inv.size());  // min 1 pour "(empty)"
        int dynamicHeight = baseHeight + (itemCount * lineHeight) + 20; // padding bas

        double x = 1550;
        double y = 15;
        double w = 350;
        double h = dynamicHeight;

        // Fond dynamique
        gc.setFill(COLOR_BG);
        gc.fillRoundRect(x, y, w, h, 15, 15);

        gc.setStroke(COLOR_FRAME);
        gc.strokeRoundRect(x, y, w, h, 15, 15);

        // Titre
        gc.setFont(FONT_TITLE);
        gc.setFill(COLOR_TEXT);
        gc.fillText("CARGO HOLD", x + 20, y + 30);

        // Contenu
        gc.setFont(FONT_NORMAL);
        int drawY = (int) (y + 65);

        if (inv.isEmpty()) {
            gc.fillText("(empty)", x + 20, drawY);
            return;
        }

        for (Map.Entry<Ressource, Integer> e : inv.entrySet()) {
            gc.fillText(e.getKey().getName() + " : " + e.getValue(), x + 20, drawY);
            drawY += lineHeight;
        }
    }

    /**
     * Rend les notifications temporaires situées en bas de l’écran.
     * Chaque message disparaît progressivement via une réduction d’opacité.
     * @param gc contexte graphique
     */
    private void renderNotifications(GraphicsContext gc) {

        int y = 850;
        Iterator<Notification> iter = notifications.iterator();

        while (iter.hasNext()) {
            Notification n = iter.next();
            long alive = System.currentTimeMillis() - n.startTime;

            if (alive > n.duration) {
                iter.remove();
                continue;
            }

            float alpha = 1f;
            if (alive > n.duration - 800) {
                alpha = (n.duration - alive) / 800f;
            }

            // Mesure automatique du texte
            javafx.scene.text.Text textMeasure = new javafx.scene.text.Text(n.text);
            textMeasure.setFont(FONT_NORMAL);
            double textWidth = textMeasure.getLayoutBounds().getWidth();

            double padding = 20;
            double boxWidth = textWidth + padding * 2;
            double boxHeight = 30;

            double x = 20;
            double boxY = y - 20;

            gc.setGlobalAlpha(alpha);

            // Rectangle auto-largeur
            gc.setFill(Color.rgb(20, 60, 130, 0.7));
            gc.fillRoundRect(x, boxY, boxWidth, boxHeight, 10, 10);

            gc.setFill(Color.WHITE);
            gc.setFont(FONT_NORMAL);
            gc.fillText(n.text, x + padding, y);

            gc.setGlobalAlpha(1);
            y -= 40;
        }
    }

    /**
     * Affiche la barre de progression du minage si une action est en cours.
     * @param gc contexte graphique
     */
    private void renderMiningBar(GraphicsContext gc) {
        if (!mining) return;

        double x = 600;
        double y = 950;
        double w = 700;
        double h = 20;

        gc.setFill(Color.rgb(40, 80, 150, 0.4));
        gc.fillRoundRect(x, y, w, h, 10, 10);

        gc.setFill(Color.CYAN);
        gc.fillRoundRect(x, y, w * miningProgress, h, 10, 10);

        gc.setStroke(Color.WHITE);
        gc.strokeRoundRect(x, y, w, h, 10, 10);

        gc.setFont(FONT_NORMAL);
        gc.setFill(Color.WHITE);
        gc.fillText("Mining " + (int) (miningProgress * 100) + "%", x, y - 8);
    }
}