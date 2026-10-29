package ships;

import entities.Ship;

/**
 * Classe représentant un vaisseau spécialisé dans la vitesse.
 * <p>
 * Ce vaisseau a une capacité standard relativement faible mais bénéficie d'une vitesse
 * supérieure à celle des autres vaisseaux. L'upgrade de vitesse est modérée.
 * </p>
 */
public class Ship_Speed extends Ship {

    /**
     * Constructeur par défaut.
     * Initialise un vaisseau avec une capacité de 40 et une vitesse de 1.5.
     */
    public Ship_Speed() { super(40, 1.5f); }

    /**
     * Retourne la vitesse actuelle du vaisseau en tenant compte de son niveau d'upgrade.
     * @return la vitesse actuelle du vaisseau
     */
    @Override
    public float getSpeed() { return speed + upgradeLevel * 0.3f; }
}
