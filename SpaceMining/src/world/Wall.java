package world;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import static game.GameState.DEBUG;

/**
 * Représente un mur ou ensemble de murs dans le monde du jeu.
 * <p>
 * Contient un polygone principal délimitant l'aire du mur ainsi que
 * des murs internes supplémentaires pour des collisions plus fines.
 * Fournit des méthodes pour vérifier la collision avec un rectangle
 */
public class Wall {

    /** Coordonnées par défaut du polygone principal du mur. */
    private static final int[] coords = new int[] {
            740, 849, 834, 847, 851, 861, 850, 882, 1047, 882, 1047, 860,
            1057, 852, 1156, 855, 1155, 827, 1176, 818, 1250, 815, 1259, 802,
            1275, 798, 1315, 799, 1318, 825, 1270, 848, 1337, 845, 1370, 818,
            1371, 745, 1339, 738, 1334, 617, 1353, 599, 1364, 595, 1362, 540,
            1342, 530, 1341, 571, 1311, 592, 1254, 592, 1250, 556, 1083, 551,
            1080, 512, 1060, 511, 1055, 514, 1036, 505, 1031, 495, 871, 488,
            872, 500, 864, 508, 855, 512, 848, 514, 837, 504, 821, 503, 821,
            542, 815, 550, 809, 551, 654, 554, 653, 594, 592, 596, 559, 574,
            558, 530, 548, 540, 542, 596, 564, 618, 563, 732, 557, 740, 531,
            741, 528, 803, 529, 817, 561, 838, 630, 844, 632, 825, 645, 816,
            722, 816, 732, 824, 742, 828, 739, 849
    };

    /** Instance statique unique de mur pour usage global. */
    private static Wall walls = new Wall();

    /** Polygone principal représentant la forme externe du mur. */
    private final Polygon mainPolygon = new Polygon();

    /** Liste des polygones internes représentant les murs supplémentaires pour collisions fines. */
    private final List<Polygon> internalWalls = new ArrayList<>();

    /** Constructeur par défaut utilisant les coordonnées définies par défaut. */
    public Wall() { this(coords); }

    /**
     * Constructeur permettant de définir un polygone principal à partir de coordonnées personnalisées.
     * @param coords Tableau d'entiers [x1, y1, x2, y2, ..., xn, yn]
     */
    public Wall(int[] coords) {
        for (int i = 0; i < coords.length; i += 2) mainPolygon.getPoints().addAll((double) coords[i], (double) coords[i + 1]);
    }

    /**
     * Vérifie si une hitbox rectangulaire est entièrement contenue dans le polygone principal
     * et ne touche aucun des murs internes.
     *
     * @param centerX Centre X de la hitbox.
     * @param centerY Centre Y de la hitbox.
     * @param w Largeur de la hitbox.
     * @param h Hauteur de la hitbox.
     * @return true si la hitbox est complètement à l'intérieur et sans collision, false sinon.
     */
    public boolean isInsideMainPolygon(double centerX, double centerY, double w, double h) {
        double halfW = w / 2;
        double halfH = h / 2;

        // Vérifie les 4 coins de la hitbox \\
        double[][] corners = {
                {centerX - halfW, centerY - halfH},
                {centerX + halfW, centerY - halfH},
                {centerX - halfW, centerY + halfH},
                {centerX + halfW, centerY + halfH}
        };

        for (double[] corner : corners) if (!mainPolygon.contains(corner[0], corner[1])) return false; // verification de collision avec le mur principal \\
        for (Polygon p : internalWalls) if (polygonIntersectsBox(p, centerX - halfW, centerY - halfH, w, h)) return false; // verification de collision avec les murs internes \\
        return true;
    }

    /** @return le polygone principal du mur. */
    public Polygon getMainPolygon() { return this.mainPolygon; }

    /** @return la liste des polygones internes. */
    public List<Polygon> getInternalWalls() { return internalWalls; }

    /**
     * Vérifie si un polygone donné intersecte une boîte rectangulaire.
     * @param poly Polygone à vérifier
     * @param x Coordonnée X du coin supérieur gauche de la boîte
     * @param y Coordonnée Y du coin supérieur gauche de la boîte
     * @param w Largeur de la boîte
     * @param h Hauteur de la boîte
     * @return true si intersection détectée, false sinon
     */
    private boolean polygonIntersectsBox(Polygon poly, double x, double y, double w, double h) {
        List<Double> pts = poly.getPoints();
        for (int i = 0; i < pts.size(); i += 2) {
            double px = pts.get(i);
            double py = pts.get(i + 1);
            if (px >= x && px <= x + w && py >= y && py <= y + h) return true;
        }
        return false;
    }

    /**
     * Ajoute un mur interne à partir d'une liste de points.
     * @param wallPoints Liste des points représentant le polygone du mur
     */
    public void addInternalWall(List<Point2D> wallPoints) {
        Polygon p = new Polygon();
        for (Point2D pt : wallPoints) p.getPoints().addAll(pt.getX(), pt.getY());
        internalWalls.add(p);
    }

    // Static \\

    /** @return l'instance statique unique de murs. */
    public static Wall getWalls() { return walls; }

    /**
     * Affiche le polygone principal du mur sur le contexte graphique si DEBUG est activé.
     * @param gc Contexte graphique
     */
    public static void displayWall(GraphicsContext gc) {
        if (!DEBUG) return;
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        List<Double> pts = walls.getMainPolygon().getPoints();
        for (int i = 0; i < pts.size(); i += 2) {
            double x1 = pts.get(i);
            double y1 = pts.get(i + 1);
            double x2 = pts.get((i + 2) % pts.size());
            double y2 = pts.get((i + 3) % pts.size());
            gc.strokeLine(x1, y1, x2, y2);
        }
    }
}