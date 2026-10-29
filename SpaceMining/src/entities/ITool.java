package entities;

/**
 * Interface représentant un outil dans le jeu.
 * <p>
 * Un outil possède un nom, un niveau, un coût de mise à niveau et un multiplicateur
 * de vitesse qui peut influencer les actions dans le jeu.
 * </p>
 */
public interface ITool {

    /**
     * Retourne le nom de l'outil.
     *
     * @return le nom de l'outil
     */
    String getName();

    /**
     * Retourne le niveau actuel de l'outil.
     *
     * @return le niveau actuel
     */
    int getLevel();

    /**
     * Calcule le multiplicateur de vitesse associé à l'outil.
     * <p>
     * Généralement utilisé pour améliorer l'efficacité de certaines actions.
     * </p>
     *
     * @return le multiplicateur de vitesse actuel
     */
    double speedMultiplier();

    /**
     * Calcule le coût pour passer au niveau suivant.
     *
     * @return le coût de l'upgrade pour le niveau suivant
     */
    double costForNext();

    void setLevel(int level);

    /**
     * Augmente le niveau de l'outil de 1.
     */
    void upgrade();
}
