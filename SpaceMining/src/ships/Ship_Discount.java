package ships;

import entities.Ship;

/**
 * Classe représentant un vaisseau orienté "discount".
 * <p>
 * Ce type de vaisseau a une capacité modérée et une vitesse relativement faible,
 * mais bénéficie d'un coût d'amélioration réduit par rapport aux autres vaisseaux.
 * </p>
 */
public class Ship_Discount extends Ship {

    /**
     * Constructeur par défaut.
     * Initialise un vaisseau avec une capacité de 40 et une vitesse de 0.6f.
     */
    public Ship_Discount() { super(40, 0.6f);
    setPourcentageRemise(0.9f);
    }

}