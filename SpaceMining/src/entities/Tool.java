package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstraite représentant un outil dans le jeu.
 * <p>
 * Chaque outil possède un nom, un niveau, un coût de mise à niveau et un
 * multiplicateur de vitesse qui peut influencer les actions du joueur.
 * </p>
 */
public abstract class Tool implements ITool {

    private static final List<Tool> tools = new ArrayList<>();

    private int prixAchat = 5000;

    /** Nom de l'outil */
    private String name;

    /** Niveau actuel de l'outil */
    private int level = 1;

    /** Coût de base pour les upgrades */
    private int baseCost = 1000;

    /**
     * Constructeur.
     * @param name nom de l'outil
     */
    public Tool(String name) {
        this.name = name;
        addTool(this);
    }

    public static List<Tool> getTools() {
        return tools;
    }

    public static void addTool(Tool tool) {
        Tool.tools.add(tool);
    }

    public static void clear() {
        Tool.tools.clear();
    }

    public static void resetTools() {
        tools.clear();
    }

    /** {@inheritDoc} */
    @Override
    public double speedMultiplier() { return 1.0 + this.level * 0.5; }

    /** {@inheritDoc} */
    @Override
    public double costForNext() { return baseCost * Math.pow(2, this.level); }

    /** {@inheritDoc} */
    @Override
    public String getName() { return this.name; }

    /** {@inheritDoc} */
    @Override
    public int getLevel() { return this.level; }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    /** {@inheritDoc} */
    @Override
    public void upgrade() { this.level += 1; }

    public int getPrixAchat() {
        return prixAchat;
    }



}


