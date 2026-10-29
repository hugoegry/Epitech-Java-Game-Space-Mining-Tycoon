package entities;

/**
 * Représente le contrat d'une ressource exploitable dans le jeu.
 * Une ressource possède un nom, un prix minimum et maximum,
 * un poids physique, un outil nécessaire pour sa récolte
 */
public interface IRessource {

    // Identité

    /**
     * Retourne le nom de la ressource.
     * @return le nom de la ressource.
     */
    String getName();

    /**
     * Définit le nom de la ressource.
     * @param name nouveau nom de la ressource.
     */
    void setName(String name);

    // Prix

    /**
     * Retourne le prix minimum auquel cette ressource peut être vendue.
     * @return prix minimum.
     */
    float getPriceMin();

    /**
     * Définit le prix minimum de la ressource.
     * @param priceMin nouveau prix minimum.
     */
    void setPriceMin(float priceMin);

    /**
     * Retourne le prix maximum auquel cette ressource peut être vendue.
     * @return prix maximum.
     */
    float getPriceMax();

    /**
     * Définit le prix maximum de la ressource.
     * @param priceMax nouveau prix maximum.
     */
    void setPriceMax(float priceMax);

    /**
     * Retourne un prix aléatoire compris entre le prix minimum
     * et le prix maximum. Peut être utilisé pour simuler un marché
     * dynamique dont la valeur varie dans une fourchette donnée.
     * @return un prix aléatoire entre priceMin et priceMax.
     */
    float getPrice(); // optionnel mais parfois pratique

    // Poids

    /**
     * Retourne le poids de la ressource. Le poids impacte la capacité
     * de chargement des vaisseaux.
     * @return poids de la ressource.
     */
    float getWeight();

    /**
     * Définit le poids de la ressource.
     * @param weight nouveau poids.
     */
    void setWeight(float weight);

    // Outil nécessaire pour récolte

    /**
     * Retourne le nom de l’outil nécessaire pour récolter cette ressource.
     * Si aucun outil n'est nécessaire, peut retourner null.
     * @return nom de l’outil requis, ou null.
     */
    String getNecessaryTool();

    /**
     * Définit l’outil requis pour récolter cette ressource.
     * @param necessaryTool nom de l’outil requis.
     */
    void setNecessaryTool(String necessaryTool);

    // Impact sur le renement \\

    /**
     * Retourne l’impact sur le rendement de la ressource.
     * @return impact d’usure (valeur positive).
     */
    float getWearingImpact();

    /**
     * Définit l’impact sur le rendement de la ressource.
     * @param wearingImpact nouvelle valeur d’usure.
     */
    void setWearingImpact(float wearingImpact);
}
