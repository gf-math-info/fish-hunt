package fish.hunt.modele.entite.poisson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PoissonTest{

    @Test
    public void testActualiser() {
        double accepte = 0.001;
        double deltaTemps = 1e-5;

        //Lorsque le poisson se déplace tranquillement vers la droite...
        Poisson poisson = new Poisson(50, 50, 0, 0,
                100, -200);
        for(int i = 0; i < 1e5; i++)
            poisson.actualiser(deltaTemps);

        assertEquals(100, poisson.getX(), accepte);
        assertEquals(-150, poisson.getY(), accepte);
        assertEquals(-100, poisson.getVy(), accepte);

        //Lorsque le poisson n'a aucune vitesse horizontale...
        poisson = new Poisson(50, 50, 0, 0, 0, 0);
        for(int i = 0; i < 1e5; i++)
            poisson.actualiser(deltaTemps);

        assertEquals(0, poisson.getX(), accepte);
        assertEquals(50, poisson.getY(), accepte);
        assertEquals(100, poisson.getVy(), accepte);

        //Lorsque le poisson a une vitesse verticale vers le bas...
        poisson = new Poisson(50, 50, 0, 0, 0, 100);
        for(int i = 0; i < 1e5; i++)
            poisson.actualiser(deltaTemps);

        assertEquals(0, poisson.getX(), accepte);
        assertEquals(150, poisson.getY(), accepte);
        assertEquals(200, poisson.getVy(), accepte);
    }
}