package fish.hunt.modele.entite.poisson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EtoileMerTest{

    @Test
    public void testActualiser() {
        double deltaTemps = 1e-5;
        double accepte = 0.001;

        EtoileMer etoile = new EtoileMer(50, 0, 0, 100);

        assertEquals(0, etoile.getY(), accepte);

        for(int i = 0; i < 1e5; i++)
            etoile.actualiser(deltaTemps);

        assertEquals(100, etoile.getX(), accepte);
        assertEquals(0, etoile.getY(), accepte);

        for(int i = 0; i < 1e5 / 4; i++)
            etoile.actualiser(deltaTemps);

        assertEquals(125, etoile.getX(), accepte);
        assertEquals(50, etoile.getY(), accepte);

        for(int i = 0; i < 1e5 / 2; i++)
            etoile.actualiser(deltaTemps);

        assertEquals(175, etoile.getX(), accepte);
        assertEquals(-50, etoile.getY(), accepte);

        for(int i = 0; i < 1e5 / 4; i++)
            etoile.actualiser(deltaTemps);

        assertEquals(200, etoile.getX(), accepte);
        assertEquals(0, etoile.getY(), accepte);
    }
}