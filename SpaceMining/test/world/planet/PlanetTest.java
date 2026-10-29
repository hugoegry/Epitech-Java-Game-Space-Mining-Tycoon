package world.planet;

import entities.Mineral;
import entities.Ressource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.planet.Planet.MoonType;

import static org.junit.jupiter.api.Assertions.*;

public class PlanetTest {

    @BeforeEach
    void resetPlanetRegistry() {
        // On vide la liste statique avant chaque test
        Planet.getPlanets().clear();
    }

    // --- Test 1 : Constructeur de base sans ressource ---
    @Test
    void testConstructorBasic() {
        Planet p = new Planet("Mars", 2, 1, MoonType.MECHANICAL);

        assertEquals("Mars", p.getName());
        assertEquals(2, p.getRequiredLevel());
        assertEquals(1, p.getDifficulty());
        assertEquals(MoonType.MECHANICAL, p.getMoonType());
        assertNull(p.getResource());
    }

    // --- Test 2 : Constructeur avec ressource ---
    @Test
    void testConstructorWithResource() {
        Ressource gold = Mineral.GOLD;
        Planet p = new Planet("Europa", 3, 2, MoonType.MARKET, gold);

        assertEquals(gold, p.getResource());
    }

    // --- Test 3 : Vérification du temps de voyage en fonction de la difficulté ---
    @Test
    void testTravelTime() {
        Planet p1 = new Planet("Easy", 1, 1, MoonType.FARM);
        Planet p2 = new Planet("Medium", 1, 2, MoonType.FARM);
        Planet p3 = new Planet("Hard", 1, 3, MoonType.FARM);
        Planet p4 = new Planet("Unknown", 1, 99, MoonType.FARM);

        assertEquals(2, p1.getTravelTime());
        assertEquals(5, p2.getTravelTime());
        assertEquals(10, p3.getTravelTime());
        assertEquals(0, p4.getTravelTime());
    }

    // --- Test 4 : Vérifier que la planète s’ajoute à la liste statique ---
    @Test
    void testPlanetRegistry() {
        Planet p = new Planet("Titan", 1, 1, MoonType.FARM);

        assertEquals(1, Planet.getPlanets().size());
        assertSame(p, Planet.getPlanets().get(0));
    }

    // --- Test 5 : Fallback getPrimary() == getResource() ---
    @Test
    void testGetPrimaryFallback() {
        Ressource diamond = Mineral.DIAMOND;
        Planet p = new Planet("Crystal", 3, 2, MoonType.MARKET, diamond);

        assertEquals(diamond, p.getPrimary());
    }

    // --- Test 6 : Fallback getResearchNeeded() == getRequiredLevel() ---
    @Test
    void testGetResearchNeeded() {
        Planet p = new Planet("Ares", 4, 1, MoonType.FARM);

        assertEquals(4, p.getResearchNeeded());
    }


    // --- Test 8 : Test du callback onExitPlanet ---
    @Test
    void testOnExitPlanetCallback() {
        Planet p = new Planet("Ganymede", 1, 1, MoonType.MARKET);

        final boolean[] triggered = {false};

        p.setOnExitPlanet(() -> triggered[0] = true);

        p.exitPlanetView();

        assertTrue(triggered[0], "Le callback onExitPlanet doit être déclenché");
    }
}
