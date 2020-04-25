package fish.hunt.modele.entite;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BulleTest {

    @Test
    public void testActualiser() {
        double deltaTemps = 1e-5;
        double accepte = 0.001;

        Bulle bulle = new Bulle(50, 0, 0, 0);

        for(int i = 0; i < 1e5; i++)
            bulle.actualiser(deltaTemps);

        assertEquals(0, bulle.getX(), accepte);
        assertEquals(0, bulle.getY(), accepte);

        bulle = new Bulle(50, 0, 0, 100);

        for(int i = 0; i < 1e5; i++)
            bulle.actualiser(deltaTemps);

        assertEquals(0, bulle.getX(), accepte);
        assertEquals(100, bulle.getY(), accepte);

        bulle = new Bulle(50, 0,0, -250);

        for(int i = 0; i < 1e5; i++)
            bulle.actualiser(deltaTemps);

        assertEquals(0, bulle.getX(), accepte);
        assertEquals(-250, bulle.getY(), accepte);
    }
}