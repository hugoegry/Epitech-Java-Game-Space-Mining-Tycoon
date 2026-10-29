package game;

import entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ships.Ship_Discount;
import ships.Ship_Speed;
import ships.Ship_Stock;
import world.planet.Planet;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {

    private GameState state;

    @BeforeEach
    public void setup() {
        state = new GameState();
    }

    // --- 1 ---
    @Test
    public void testInitialCredits() {
        assertEquals(100, state.credits);
    }

    // --- 2 ---
    @Test
    public void testInitialShipIsSpeed() {
        assertTrue(state.ship instanceof Ship_Speed);
    }

    // --- 3 ---
    @Test
    public void testLoadShipSpeed() {
        state.loadShip("speed");
        assertTrue(state.ship instanceof Ship_Speed);
    }

    // --- 4 ---
    @Test
    public void testLoadShipStock() {
        state.loadShip("stock");
        assertTrue(state.ship instanceof Ship_Stock);
    }

    // --- 5 ---
    @Test
    public void testLoadShipDiscount() {
        state.loadShip("discount");
        assertTrue(state.ship instanceof Ship_Discount);
    }

    // --- 6 ---
    @Test
    public void testStorageInitialization() {
        for (Ressource r : Ressource.mineralList) {
            assertEquals(0, state.ship.getInventory().getOrDefault(r, 0));
        }
    }

    // --- 7 ---
    @Test
    public void testShipAddResource() {
        Ship ship = state.ship;

        boolean ok = ship.addRessource(Mineral.GOLD, 3);

        assertTrue(ok);
        assertEquals(3, ship.getInventory().get(Mineral.GOLD));
        assertTrue(ship.getCurrentWeight() > 0);
    }

    // --- 8 ---
    @Test
    public void testShipRemoveResource() {
        Ship ship = state.ship;

        ship.addRessource(Mineral.GOLD, 5);
        ship.removeRessource(Mineral.GOLD, 3);

        assertEquals(2, ship.getInventory().get(Mineral.GOLD));
    }

    // --- 9 ---
    @Test
    public void testShipCapacityLimit() {
        Ship ship = state.ship;

        boolean ok = ship.addRessource(Mineral.ORICHALCUM, 9999);

        assertFalse(ok);
    }

    // --- 10 ---
    @Test
    public void testToolUpgrade() {
        Tool tool = new Drill();

        int oldLevel = tool.getLevel();
        tool.upgrade();

        assertEquals(oldLevel + 1, tool.getLevel());
    }

    // --- 11 ---
    @Test
    public void testToolCostIncrease() {
        Tool tool = new Drill();

        double cost1 = tool.costForNext();
        tool.upgrade();
        double cost2 = tool.costForNext();

        assertTrue(cost2 > cost1);
    }

    // --- 12 ---
    @Test
    public void testAddToolToShip() {
        Ship ship = state.ship;
        Tool t = new Drill();

        boolean ok = ship.addTool(t);

        assertTrue(ok);
        assertTrue(ship.getTools().contains(t));
    }

    // --- 13 ---
    @Test
    public void testAddDuplicateToolFails() {
        Ship ship = state.ship;
        Tool t = new Drill();

        ship.addTool(t);
        boolean ok = ship.addTool(t);

        assertFalse(ok);
    }

    // --- 14 ---
    @Test
    public void testSellAllAddsCredits() {
        Ship ship = state.ship;

        ship.addRessource(Mineral.GOLD, 5);
        double before = state.credits;

        ship.sellAll(state);

        assertTrue(state.credits > before);
        assertEquals(0, ship.getInventory().size());
    }

    // --- 15 ---
    @Test
    public void testUpgradeShipCost() {
        Ship ship = state.ship;

        double cost = ship.upgradeCost();

        assertTrue(cost > 0);
    }

    @Test
    public void testFullGameFlow() {
        GameState g = new GameState();

        // 1) Vérification de l’état initial
        assertEquals(100, g.credits);
        assertTrue(g.ship instanceof Ship_Speed);
        assertEquals(0, g.ship.getTools().size());
        assertEquals(0, g.ship.getCurrentWeight());

        for (Ressource r : Ressource.mineralList) {
            assertEquals(null, g.ship.getInventory().get(r));
        }

        
        // 2) Change de planète (simulation)
        Planet fakePlanet = new Planet(
                "Mars",
                1,
                1,
                Planet.MoonType.MECHANICAL,
                Mineral.GOLD                      
        );
        g.currentPlanet = fakePlanet;

        assertEquals("Mars", g.currentPlanet.getName());

        // 3) Mine plusieurs ressources
        Ship ship = g.ship;

        boolean added1 = ship.addRessource(Mineral.GOLD, 2);     // +2 Gold
        boolean added2 = ship.addRessource(Mineral.DIAMOND, 1);  // +1 Diamond

        assertTrue(added1);
        assertTrue(added2);

        assertEquals(2, ship.getInventory().get(Mineral.GOLD));
        assertEquals(1, ship.getInventory().get(Mineral.DIAMOND));

        double weightAfterMining = ship.getCurrentWeight();
        assertTrue(weightAfterMining > 0);

        // 4) Tentative de surcharge
        boolean overTry = ship.addRessource(Mineral.ORICHALCUM, 9999);
        assertFalse(overTry);

        // 5) Sell all & gain de crédits
        double creditsBefore = g.credits;
        ship.sellAll(g);

        assertTrue(g.credits > creditsBefore);
        assertEquals(0, ship.getInventory().size());

        // 6) Upgrade du premier outil (Drill)
        g.ship.addTool(new Drill());
        Tool drill = g.ship.getTools().get(0);
        int oldLevel = drill.getLevel();

        drill.upgrade();

        assertEquals(oldLevel + 1, drill.getLevel());
        assertTrue(drill.costForNext() > 0);

        // 7) Changement complet du ship
        g.loadShip("stock");

        assertTrue(g.ship instanceof Ship_Stock);
        assertEquals(0, g.ship.getCurrentWeight());

        // 8) Ajout d’un second outil
        Tool newTool = new Drill();
        boolean addedTool = g.ship.addTool(newTool);

        assertTrue(addedTool);
        assertTrue(g.ship.getTools().contains(newTool));

        // 9) Ajout de ressources jusqu’à saturation
        boolean filled = false;
        int amount = 0;

        while (true) {
            boolean ok = g.ship.addRessource(Mineral.GOLD, 1);
            if (!ok) {
                filled = true;
                break;
            }
            amount++;
        }

        assertTrue(filled);
        assertTrue(amount > 10);  // On a rempli une bonne quantité
        assertTrue(g.ship.getCurrentWeight() >= ship.getCapacity());

        // 10) Séquence complète après upgrade + sell
        // Mine → Sell → Change planète → Mine
        double beforeCycleCredits = g.credits;

        // A) Sell everything
        g.ship.sellAll(g);
        assertTrue(g.credits > beforeCycleCredits);

        // B) Change planète
        g.currentPlanet = new Planet(
                "Europa",
                1,
                2,
                Planet.MoonType.MECHANICAL,
                Mineral.DIAMOND
        );
        assertEquals("Europa", g.currentPlanet.getName());

        // C) Mine encore
        boolean minedAgain = ship.addRessource(Mineral.DIAMOND, 3);
        assertTrue(minedAgain);
        assertEquals(3, ship.getInventory().get(Mineral.DIAMOND));

        // 11) Dernier sell pour finir le cycle
        double lastCreditsBefore = g.credits;
        ship.sellAll(g);

        assertTrue(g.credits > lastCreditsBefore);
    }


}
