package entities;

import game.GameState;

import java.util.*;
import utils.Inventory;


/**
 * Classe abstraite représentant un vaisseau générique dans le jeu.
 * Elle fournit l’ensemble des comportements communs aux différents types de vaisseaux,
 * tels que la gestion de la capacité, de la vitesse, de l’inventaire et des outils.
 */
public abstract class Ship implements IShip {

    /** Capacité de base du vaisseau avant amélioration. */
    protected int capacity = 50;

    /** Vitesse de base du vaisseau avant amélioration. */
    protected float speed = 1.0f;

    /** Niveau d'amélioration actuel du vaisseau. */
    protected int upgradeLevel = 1;

    /** Taux de remise sur les tools */
    protected float pourcentageRemise = 1.0f;

    /** Poids total actuellement transporté par le vaisseau. */
    protected float currentWeight = 0;

    /** Inventaire associant chaque ressource à sa quantité. */
    //protected Inventory<Ressource> inventory = new Inventory<>(capacity, Ressource::getWeight);

    protected Inventory<Ressource> inventory;

    /** Liste des outils équipés par le vaisseau. */
    protected List<Tool> tools = new ArrayList<>();


    /**
     * Constructeur de base pour un vaisseau.
     *
     * @param capacity capacité de stockage initiale
     * @param speed    vitesse initiale
     */
    public Ship(int capacity, float speed) {
        this.capacity = capacity;
        this.speed = speed;
        this.inventory = new Inventory<>(capacity, Ressource::getWeight);
    }

    /** {@inheritDoc} */
    @Override
    public int getUpgradeLevel() { return this.upgradeLevel; }

    /** {@inheritDoc} */
    @Override
    public void setUpgradeLevel(int level) { this.upgradeLevel = level; }

    /** {@inheritDoc} */
    @Override
    public int getCapacity() { return capacity + upgradeLevel; }

    @Override
    public void setCapacity() { this.capacity = capacity + upgradeLevel; }

    /** {@inheritDoc} */
    @Override
    public double upgradeCost() { return 300 * Math.pow(2, upgradeLevel); }

    /** {@inheritDoc} */
    @Override
    public float getSpeed() { return speed + (upgradeLevel * 0.5f); }

    @Override
    public float getPourcentageRemise() { return pourcentageRemise; }

    @Override
    public void setPourcentageRemise(float pourcentageRemise) { this.pourcentageRemise = pourcentageRemise; }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return switch (this.getClass().getSimpleName()) {
            case "SpeedShip" -> "speed";
            case "StockShip" -> "stock";
            case "DiscountShip" -> "discount";
            default -> "unknown";
        };
    }

    /** {@inheritDoc} */

    /*
    @Override
    public Map<Ressource, Integer> getInventory() {
        return this.inventory;
    }*/

    @Override
    public Map<Ressource, Integer> getInventory() {
        Map<Ressource, Integer> map = new HashMap<>();
        for (var entry : this.inventory.entrySet()) map.put(entry.getKey(), entry.getValue());
        return map;
    }

    /** {@inheritDoc} */
    @Override
    public float getCurrentWeight() { return this.inventory.getCurrentWeight(); }

    /** {@inheritDoc} */
    @Override
    public List<Tool> getTools() { return List.copyOf(tools); }

    /** {@inheritDoc} */
    @Override
    public boolean addTool(Tool t) {
        if (t == null || tools.contains(t)) return false;
        tools.add(t);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean addRessource(Ressource r, int quantity) {
        return inventory.add(r, quantity);
    }

    /** {@inheritDoc} */
    @Override
    public boolean removeRessource(Ressource r, int quantity) {
        return inventory.remove(r, quantity);
    }

    /** {@inheritDoc} */
    @Override
    public void sellAll(GameState state) {
        Ressource r;
        int quantity;
        for(Iterator var1 = this.getInventory().entrySet().iterator(); var1.hasNext(); state.credits += (double)((float)quantity * r.getPriceMax())) {
            Map.Entry<Ressource, Integer> entry = (Map.Entry)var1.next();
            r = (Ressource)entry.getKey();
            quantity = (Integer)entry.getValue();
        }

        this.clearInventory();
    }

    /** {@inheritDoc} */
    @Override
    public void clearInventory() { inventory.clear(); }

    /** {@inheritDoc} */
    @Override
    public void clearTools() { Tool.clear(); }
}
