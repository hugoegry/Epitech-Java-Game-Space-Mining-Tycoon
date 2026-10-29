package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Classe générique représentant un inventaire pouvant stocker des objets de type {@code T}.
 * <p>
 * Elle gère l'ajout et la suppression d'objets en prenant en compte un poids maximal
 * total (capacité), et fournit un accès aux éléments stockés.
 * </p>
 *
 * @param <T> le type des objets stockés dans l'inventaire
 */
public class Inventory<T> {

    /** Map interne associant chaque élément à sa quantité */
    private final Map<T, Integer> items = new HashMap<>();
    /** Capacité maximale de l'inventaire */
    private final float capacity;

    /** Poids total actuellement stocké */
    private float currentWeight = 0;

    /** Fournisseur de poids pour le type générique T */
    private WeightProvider<T> weightProvider;

    /**
     * Crée un nouvel inventaire avec une capacité maximale et un fournisseur de poids.
     *
     * @param capacity le poids maximum que l'inventaire peut contenir
     * @param weightProvider un fournisseur de poids pour les éléments de type {@code T}
     */
    public Inventory(float capacity, WeightProvider<T> weightProvider) {
        this.capacity = capacity;
        this.weightProvider = weightProvider;
    }

    /**
     * Ajoute un certain nombre d'éléments à l'inventaire.
     *
     * @param item l'élément à ajouter
     * @param quantity la quantité à ajouter
     * @return {@code true} si l'ajout a réussi, {@code false} sinon (poids dépassé ou quantité invalide)
     */
    public boolean add(T item, int quantity) {
        if (item == null || quantity <= 0) return false;
        float addedWeight = weightProvider.getWeight(item) * quantity;
        if (currentWeight + addedWeight > capacity) return false;

        currentWeight += addedWeight;
        items.put(item, items.getOrDefault(item, 0) + quantity);
        return true;
    }

    /**
     * Supprime un certain nombre d'éléments de l'inventaire.
     *
     * @param item l'élément à supprimer
     * @param quantity la quantité à supprimer
     * @return {@code true} si la suppression a réussi, {@code false} sinon (élément absent ou quantité invalide)
     */
    public boolean remove(T item, int quantity) {
        if (!items.containsKey(item) || quantity <= 0) return false;

        int currentQty = items.get(item);
        currentWeight -= weightProvider.getWeight(item) * Math.min(quantity, currentQty);
        if (currentQty <= quantity) items.remove(item);
        else items.put(item, currentQty - quantity);
        return true;
    }

    /**
     * Retourne un ensemble de toutes les entrées de l'inventaire (élément et quantité).
     *
     * @return un {@link Set} des entrées de l'inventaire
     */
    public Set<Map.Entry<T, Integer>> entrySet() { return items.entrySet(); }

    /**
     * Vide complètement l'inventaire et réinitialise le poids total.
     */
    public void clear() {
        items.clear();
        currentWeight = 0;
    }

    /**
     * Retourne le poids total actuellement stocké dans l'inventaire.
     * @return le poids total
     */
    public float getCurrentWeight() { return currentWeight; }

    /**
     * Retourne la capacité maximale de l'inventaire.
     * @return la capacité maximale
     */
    public float getCapacity() { return capacity; }

    /**
     * Interface pour fournir le poids des objets stockés dans l'inventaire.
     * @param <T> le type des objets pour lesquels le poids est fourni
     */
    public interface WeightProvider<T> {

        /**
         * Retourne le poids d'un objet.
         *
         * @param item l'objet dont on souhaite connaître le poids
         * @return le poids de l'objet
         */
        float getWeight(T item);
    }
}
