package entities;

import com.sun.javafx.collections.ElementObservableListDecorator;
import game.GameState;
import javafx.geometry.Point2D;
import world.Wall;
import javafx.scene.input.KeyCode;

import java.util.Map;
import java.util.Set;

import world.Wall;

/**
 * Classe représentant un joueur dans le jeu.
 * <p>
 * Le joueur possède une position dans le monde, un système de mouvement basé sur
 * l'accélération, la friction et une vitesse maximale, ainsi que la gestion des collisions
 * avec les murs.
 * </p>
 */
public class Player implements IPlayer {
    // Physique du player \\
    private Point2D velocity = new Point2D(0, 0);
    private final double acceleration = 1200;
    private final double friction = 0.85;
    private final double maxSpeed = 300;

    private Point2D position;

    private Set<KeyCode> keys;

    /**
     * Constructeur.
     * @param x position initiale en X
     * @param y position initiale en Y
     */
    public Player(float x, float y) {
        position = new Point2D(x, y);
    }

    /** {@inheritDoc} */
    @Override
    public Point2D getPosition() {
        return position;
    }

    /** {@inheritDoc} */
    @Override
    public void setPosition(Point2D newPosition) {
        this.position = newPosition;
    }

    /** {@inheritDoc} */
    @Override
    public void setPosition(double x, double y) { this.position = new Point2D(x, y); }

    /** {@inheritDoc} */
    @Override
    public void setInputs(Set<KeyCode> keys) { this.keys = keys; }

    /** {@inheritDoc} */
    @Override
    public void move(double dt) {
        double dx = 0, dy = 0;

        // Raccourcis
        Map<String, KeyCode> binds = GameState.currentState.keyBindings;

        // --- Direction ---
        if (keys.contains(binds.get("UP")))    dy -= 1;
        if (keys.contains(binds.get("DOWN")))  dy += 1;
        if (keys.contains(binds.get("LEFT")))  dx -= 1;
        if (keys.contains(binds.get("RIGHT"))) dx += 1;

        // Direction brute
        Point2D dir = new Point2D(dx, dy);

        // --- Accélération ou friction ---
        if (dir.magnitude() > 0) {
            dir = dir.normalize();
            velocity = velocity.add(dir.multiply(acceleration * dt));
        } else {
            velocity = velocity.multiply(friction);
        }

        // --- Vitesse max ---
        if (velocity.magnitude() > maxSpeed) {
            velocity = velocity.normalize().multiply(maxSpeed);
        }

        // Position prévue
        Point2D nextPos = position.add(velocity.multiply(dt));

        // --- Collision X ---
        if (Wall.getWalls().isInsideMainPolygon(nextPos.getX(), position.getY(), 64, 64)) {
            position = new Point2D(nextPos.getX(), position.getY());
        }

        // --- Collision Y ---
        if (Wall.getWalls().isInsideMainPolygon(position.getX(), nextPos.getY(), 64, 64)) {
            position = new Point2D(position.getX(), nextPos.getY());
        }
    }
}
