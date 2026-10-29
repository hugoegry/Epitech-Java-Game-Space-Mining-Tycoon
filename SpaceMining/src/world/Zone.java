package world;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import screens.GameScene;

import java.util.ArrayList;
import java.util.List;
import entities.Player;
import javafx.scene.canvas.GraphicsContext;
import java.util.Random;
import world.ZoneView;

/**
 * Représente une zone interactive dans le jeu.
 * <p>
 * Une zone peut être de type {@link ZoneType} (PILOT, HUBLOT, DOOR) et possède
 * une position, une taille et une icône. Toutes les instances sont conservées
 * dans une liste statique pour permettre un accès global et une vérification
 * des zones survolées par le joueur.
 */
public class Zone  {

    /** Types de zones disponibles. */
    public enum ZoneType { PILOT, HUBLOT, DOOR }

    /** Liste statique de toutes les zones créées dans le jeu. */
    private static final ArrayList<Zone> zones = new ArrayList<>();

    private String name;
    private Double x, y, w, h;
    private ZoneType type;
    private String icon = "✦"; // ✦ ◎ ⚙ ⌘ ⬒

    /**
     * Constructeur complet avec icône personnalisée.
     *
     * @param name Nom de la zone
     * @param x Coordonnée X du coin supérieur gauche
     * @param y Coordonnée Y du coin supérieur gauche
     * @param w Largeur de la zone
     * @param h Hauteur de la zone
     * @param type Type de la zone
     * @param icon Icône représentant la zone
     */
    public Zone(String name, Double x, Double y, Double w, Double h, ZoneType type, String icon){
        this(name, x, y, w, h, type);
        this.icon = icon;
    }

    /**
     * Constructeur standard sans icône personnalisée.
     *
     * @param name Nom de la zone
     * @param x Coordonnée X du coin supérieur gauche
     * @param y Coordonnée Y du coin supérieur gauche
     * @param w Largeur de la zone
     * @param h Hauteur de la zone
     * @param type Type de la zone
     */
    public Zone(String name, Double x, Double y, Double w, Double h, ZoneType type){
        this.name = name;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.type = type;
        zones.add(this);
    }

    /** @return le nom de la zone */
    public String getName(){ return name; }

    /** @return le type de la zone */
    public ZoneType getType(){ return type; }

    /** @return l'icône de la zone */
    public String getIcon(){ return icon; }

    /** @return la position X du coin supérieur gauche */
    public Double getX(){ return x; }

    /** @return la position Y du coin supérieur gauche */
    public Double getY(){ return y; }

    /** @return la largeur de la zone */
    public Double getWidth(){ return w; }

    /** @return la hauteur de la zone */
    public Double getHeight(){ return h; }

    /** @return la liste de toutes les zones créées */
    public static List<Zone> getZones(){ return zones; }

    /** Supprime toutes les zones de la liste statique */
    public static void clearZones(){ zones.clear(); }

    /**
     * Définit cette zone comme la zone actuellement survolée.
     * Déclenche la mise à jour visuelle via {@link ZoneView}.
     */
    public void setHoveredZone() { ZoneView.setHoveredZone(this); }

    /**
     * Vérifie si un point est à l'intérieur de cette zone.
     *
     * @param px Coordonnée X du point
     * @param py Coordonnée Y du point
     * @return true si le point est contenu dans la zone, false sinon
     */
    public boolean contains(Double px, Double py){
        return px >= x && px <= x+w && py >= y && py <= y+h;
    }

    /**
     * Vérifie si le joueur se trouve dans une zone et met à jour
     * la zone survolée en conséquence.
     *
     * @param playerPositionX Coordonnée X du joueur
     * @param playerPositionY Coordonnée Y du joueur
     */
    public  static void checkPlayerHoverZone(Double playerPositionX, Double playerPositionY) {
        for (Zone z : zones) {
            ZoneView.setHoveredZone(); // reset \\
            if (z.contains(playerPositionX, playerPositionY)) {
                z.setHoveredZone();
                break;
            }
        }
    }
}