package entities;

import java.util.Map;
import java.util.List;

/**
 * Interface définissant le contrat de base d'un vaisseau dans le jeu.
 * Un vaisseau possède des caractéristiques propres (capacité, vitesse, niveau d'amélioration),
 * un inventaire de ressources, une liste d'outils et des interactions liées a sont gameplay.
 */
public interface IShip {

    // Caractéristiques \\

    /**
     * Retourne la capacité totale de chargement du vaisseau.
     * La capacité peut être influencée par le niveau d'amélioration.
     * @return capacité maximale de cargaison exprimée en poids.
     */
    int getCapacity();

    /**
     * Retourne la vitesse actuelle du vaisseau.
     * La vitesse peut également dépendre du niveau d'amélioration ou du type de vaisseau.
     * @return vitesse du vaisseau.
     */
    float getSpeed();

    float getPourcentageRemise();

    void setPourcentageRemise(float pourcentageRemise);

    /**
     * Retourne le type du vaisseau sous forme de chaîne.
     * Utilisé notamment pour identifier les bonus spécifiques
     * (ex. "speed", "stock", "discount").
     * @return type du vaisseau.
     */
    String getType();

    /**
     * Retourne le niveau d'amélioration actuel du vaisseau.
     * @return niveau d'amélioration (>= 1).
     */
    int getUpgradeLevel();

    /**
     * Définit le niveau d'amélioration (upgrade) du vaisseau.
     * @param level le nouveau niveau d'amélioration.
     */
    void setUpgradeLevel(int level);

    void setCapacity();

    /**
     * Calcule et retourne le coût nécessaire pour améliorer le vaisseau au niveau supérieur.
     * @return coût de l'amélioration en crédits.
     */
    double upgradeCost();

    // Inventaire \\

    /**
     * Retourne une vue de l'inventaire du vaisseau.
     * Chaque ressource est associée à une quantité.
     * @return une Map contenant les ressources et leur quantité.
     */
    Map<Ressource, Integer> getInventory();

    /**
     * Ajoute une quantité donnée d'une ressource au vaisseau,
     * si la capacité de chargement le permet.
     *
     * @param r        ressource à ajouter.
     * @param quantity quantité désirée.
     * @return true si l'ajout a réussi, false sinon.
     */
    boolean addRessource(Ressource r, int quantity);

    /**
     * Retire une quantité donnée d'une ressource du vaisseau,
     * si celle-ci est présente dans l'inventaire.
     *
     * @param r        ressource à retirer.
     * @param quantity quantité à retirer.
     * @return true si le retrait a été effectué, false sinon.
     */
    boolean removeRessource(Ressource r, int quantity);

    /**
     * Retourne le poids total actuellement embarqué par le vaisseau.
     *
     * @return poids total des ressources.
     */
    float getCurrentWeight();

    /**
     * Vide complètement l'inventaire du vaisseau.
     * Ne modifie pas la quantité des outils.
     */
    void clearInventory();

    // Outils \\

    /**
     * Retourne la liste des outils installés sur le vaisseau.
     * La liste retournée est généralement non modifiable.
     *
     * @return liste des outils.
     */
    List<Tool> getTools();

    /**
     * Ajoute un outil au vaisseau si celui-ci n'est pas déjà installé.
     *
     * @param t outil à ajouter.
     * @return true si l'ajout a réussi, false sinon.
     */
    boolean addTool(Tool t);

    // Gameplay \\

    /**
     * Vend toutes les ressources actuellement stockées dans l'inventaire
     * et ajoute les crédits correspondants au GameState fourni.
     * Le prix utilisé dépend de la logique interne de pricing des ressources.
     *
     * @param state objet représentant l'état global du jeu
     *              (utilisé pour ajouter les crédits).
     */
    void sellAll(game.GameState state);

    void clearTools();
}
