package world.asset;


/**
 * Représente les données d'un décor dans le jeu, incluant son chemin de fichier,
 * sa position, ses dimensions et son placement par rapport au background.
 */
public class DecorData {

    /** Chemin vers le fichier image ou ressource du décor. */
    public String path;

    /** Position X et Y du décor et sa Largeur / Hauteur. */
    public double x, y, width, height;

    /**
     * Détermine si le décor est affiché au-dessus du background.
     * True = au-dessus du background, false = en dessous.
     */
    public boolean aboveBackground; // true = au-dessus du background, false = en-dessous

    /**
     * Crée un décor avec position, dimensions et chemin, affiché par défaut au-dessus du background.
     *
     * @param path   Chemin du fichier ressource du décor.
     * @param x      Position X.
     * @param y      Position Y.
     * @param width  Largeur du décor.
     * @param height Hauteur du décor.
     */
    public DecorData(String path, double x, double y, double width, double height) { this(path, x, y, width, height, true); } // Par défaut aboveBackground = true \\

    /**
     * Crée un décor avec position, dimensions, chemin et indication de placement par rapport au background.
     *
     * @param path            Chemin du fichier ressource du décor.
     * @param x               Position X.
     * @param y               Position Y.
     * @param width           Largeur du décor.
     * @param height          Hauteur du décor.
     * @param aboveBackground True si le décor doit être affiché au-dessus du background, false sinon.
     */
    public DecorData(String path, double x, double y, double width, double height, boolean aboveBackground) {
        this.path = path;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.aboveBackground = aboveBackground;
    }
}
