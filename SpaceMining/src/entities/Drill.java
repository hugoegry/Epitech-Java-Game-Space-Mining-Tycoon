package entities;

/**
 * Classe représentant l'outil de type "Drill".
 * <p>
 * Hérite de la classe abstraite {@link Tool}.
 * Cet outil utilise les caractéristiques de base d'un Tool,
 * comme le niveau, le multiplicateur de vitesse et le coût d'upgrade.
 * </p>
 */
public class Drill extends Tool {

    /**
     * Constructeur par défaut.
     * <p> Initialise l'outil avec le nom "Drill". </p>
     */
    public Drill() {
        super("Drill");
    }
}
