package fish.hunt.modele.entite.poisson;

import org.junit.Test;

public class EtoileMerTest{

    @Test
    public void testActualiser() {
        double deltaTemps = 1e-5;
        double accepte = 0.1;

        EtoileMer etoile = new EtoileMer(50, 50,
                0, 0, 100);
        for(int i = 0; i < 1e5; i++)
            etoile.actualiser(deltaTemps);

        //TODO
    }
}