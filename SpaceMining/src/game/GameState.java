package game;

import entities.*;
import javafx.scene.input.KeyCode;
import ships.Ship_Speed;
import ships.Ship_Stock;
import ships.Ship_Discount;
import world.Zone;
import world.planet.Planet;
import java.util.*;

/**
 * Représente l'état global et persistant du jeu.
 * <p>
 * Cette classe centralise l'ensemble des informations liées à la progression du joueur :
 * crédits, vaisseau utilisé, niveau de recherche, planète actuelle, style intérieur,
 * ainsi que les key bindings configurables. Elle sert de référence unique pendant toute
 * la session et peut être réinitialisée pour démarrer une nouvelle partie.
 * <p>
 * L'instance {@link #currentState} agit comme un singleton simplifié permettant d'accéder
 * rapidement à l'état actif.
 */
public class GameState {
    public static boolean DEBUG = false;
    public static final String DEBUG_CODE = "1512";

    /**
     * Instance globale représentant l'état courant du jeu.
     * Elle est utilisée comme point d’accès central pour tous les systèmes.
     */
    public static GameState currentState = new GameState();

    //region Champs

    /** Mappage des actions du joueur vers les touches configurées. */
    public Map<String, KeyCode> keyBindings = new HashMap<>();

    /** Quantité actuelle de crédits possédée par le joueur. */
    public double credits = 100;

    /** Vaisseau actuellement utilisé par le joueur. */
    public Ship ship = new Ship_Speed();

    /** Niveau du joueur. */
    public int researchLevel = 1;

    /** Planète sur laquelle se trouve actuellement le joueur. */
    public Planet currentPlanet;

    //endregion

    /**
     * Charge un modèle de vaisseau basé sur une chaîne.
     * Trois types sont reconnus : "stock", "discount" et "speed" (par défaut).
     *
     * @param type nom du modèle de vaisseau à charger
     */
    public void loadShip(String type) {
        switch (type.toLowerCase()) {
            case "stock": ship = new Ship_Stock(); break;
            case "discount": ship = new Ship_Discount(); break;
            default: ship = new Ship_Speed();
        }
    }

    /** Styles d'intérieur disponibles pour le vaisseau actuel. */
    public enum ShipInteriorStyle { CLASSIC, INDUSTRIAL, FUTURISTIC }

    /** Style d'intérieur actuellement appliqué au vaisseau. */
    public ShipInteriorStyle interiorStyle = ShipInteriorStyle.CLASSIC;

    /**
     * Initialise un nouvel état de jeu avec :
     * <ul>
     *   <li>100 crédits</li>
     *   <li>un vaisseau de type {@link Ship_Speed}</li>
     *   <li>niveau de recherche 1</li>
     *   <li>aucune planète définie</li>
     *   <li>keybindings par défaut type clavier AZERTY</li>
     * </ul>
     */
    public GameState() {
        // Key bindings par défaut (modifiables en jeu + sauvegardables)
        keyBindings.put("UP", KeyCode.Z);
        keyBindings.put("DOWN", KeyCode.S);
        keyBindings.put("LEFT", KeyCode.Q);
        keyBindings.put("RIGHT", KeyCode.D);
        keyBindings.put("INTERACT", KeyCode.E);
        keyBindings.put("MENU", KeyCode.ESCAPE);
        keyBindings.put("SAVE", KeyCode.F2);
    }

    //region Accessors

    /** @return l'état actuel des crédits du joueur. */
    public double getCredits() { return this.credits; }

    public void setCredits(double credits) {
        if (DEBUG) {
            this.credits = Math.max(credits, 999_999_999);
            return;
        }
        this.credits = Math.max(0, credits);
    }

    public boolean spendCredits(double amount) {
        if (DEBUG) return true;
        if (credits < amount) return false;
        credits -= amount;
        return true;
    }

    public boolean canAccessPlanet(int requiredLevel) {
        return DEBUG || researchLevel >= requiredLevel;
    }

    public void enableDebugMode() {
        DEBUG = true;
        researchLevel = Math.max(researchLevel, 99);
        credits = 999_999_999;
    }

    public void disableDebugMode() {
        DEBUG = false;
    }

    /** @return le vaisseau actuellement utilisé. */
    public Ship getShip() { return this.ship; }

    /** Définit un vaisseau. */
    public void setShip(Ship ship) { this.ship = ship; }

    /** @return le niveau de recherche actuel. */
    public int getResearchLevel() { return this.researchLevel; }

    /** Définit le niveau de recherche (min 1). */
    public void setResearchLevel(int researchLevel) { this.researchLevel = Math.max(1, researchLevel);}

    /** @return la planète actuelle où se trouve le joueur. */
    public Planet getCurrentPlanet() { return this.currentPlanet; }

    /** Change la planète actuelle. */
    public void setCurrentPlanet(Planet currentPlanet) { this.currentPlanet = currentPlanet; }

    /** @return le style intérieur du vaisseau. */
    public ShipInteriorStyle getInteriorStyle() { return this.interiorStyle; }

    /** Change le style intérieur du vaisseau. */
    public void setInteriorStyle(ShipInteriorStyle interiorStyle) { if (interiorStyle != null) this.interiorStyle = interiorStyle; }

    /** @return la map des key bindings actuels. */
    public Map<String, KeyCode> getKeyBindings() { return this.keyBindings; }

    /** Définit les key bindings (null interdit). */
    public void setKeyBindings(Map<String, KeyCode> bindings) { if (bindings != null) this.keyBindings = bindings;}

    /** Retourne une touche associée à une action. */
    public KeyCode getKeyBinding(String action) { return keyBindings.get(action); }

    /** Associe une nouvelle touche à une action. */
    public void setKeyBinding(String action, KeyCode key) { if (action != null && key != null) keyBindings.put(action, key); }
    //endregion

    /**
     * Réinitialise complètement l'état du jeu à ses valeurs d'origine.
     * <p>
     * Cette méthode :
     * <ul>
     *     <li>réinitialise les crédits</li>
     *     <li>réinstancie un vaisseau de type {@link Ship_Speed}</li>
     *     <li>réinitialise le niveau de recherche</li>
     *     <li>vide l'inventaire et les outils du vaisseau</li>
     *     <li>efface toutes les planètes et zones générées</li>
     *     <li>réinitialise toutes les ressources et outils globaux</li>
     *     <li>restaure les key bindings par défaut</li>
     * </ul>
     */
    public void clearState() {
        credits = DEBUG ? 999_999_999 : 100;
        ship = new Ship_Speed();
        //interiorStyle = ShipInteriorStyle.CLASSIC;
        researchLevel = DEBUG ? Math.max(1, 99) : 1;
        ship.clearInventory();
        ship.clearTools();
        Planet.clearPlanets();
        Zone.clearZones();
        Tool.resetTools();
        Ressource.mineralList.clear();
        //keyBindings = getDefaultKeyBindings();
    }

    /**
     * Génère un ensemble de key bindings par défaut.
     * @return une map contenant les raccourcis clavier standards du jeu
     */
    private Map<String, KeyCode> getDefaultKeyBindings() {
        Map<String, KeyCode> defaultBindings = new HashMap<>();
        defaultBindings.put("UP", KeyCode.Z);
        defaultBindings.put("DOWN", KeyCode.S);
        defaultBindings.put("LEFT", KeyCode.Q);
        defaultBindings.put("RIGHT", KeyCode.D);
        defaultBindings.put("INTERACT", KeyCode.E);
        defaultBindings.put("MENU", KeyCode.ESCAPE);
        defaultBindings.put("SAVE", KeyCode.F2);
        return defaultBindings;
    }

    /** @return l'instance globale représentant l'état courant du jeu. */
    public static GameState getCurrentState() { return currentState; }
}