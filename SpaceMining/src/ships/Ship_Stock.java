package ships;

import entities.Ship;

/**
 * Classe représentant un vaisseau spécialisé dans le stockage.
 * <p>
 * Ce vaisseau a une capacité de transport très élevée mais une vitesse réduite.
 * Il est conçu pour maximiser l'inventaire transportable.
 * </p>
 */
public class Ship_Stock extends Ship {

    /**
     * Constructeur par défaut.
     * Initialise un vaisseau avec une capacité de 80 et une vitesse de 0.6.
     */
    public Ship_Stock() {
        super(80, 0.6f);
    }
}
