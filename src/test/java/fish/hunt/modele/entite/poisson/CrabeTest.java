package fish.hunt.modele.entite.poisson;

import org.junit.Test;

import static org.junit.Assert.*;

public class CrabeTest {

    @Test
    public void actualiser() {
        double accepte = 0.0000001; // erreur dur à l'encodage binaire.
        double deltaTemps = 1e-5;

        //Lorsque le crabe se déplace tranquillement vers la droite...
        Crabe crabe = new Crabe(50, 0, 0, 100);
        Crabe crabe1 = new Crabe(50, 10, 30, 130);
        for(int i = 0; i < 1e5; i++) {
            crabe.actualiser(deltaTemps);
            crabe1.actualiser(deltaTemps);
        }

        assertEquals(50, crabe.getX(), accepte);
        assertEquals(0, crabe.getY(), 0);
        assertEquals(0, crabe.getVy(), 0);

        assertEquals(75, crabe1.getX(), accepte);
        assertEquals(30, crabe1.getY(), 0);
        assertEquals(0, crabe1.getVy(), 0);

        for (int i = 0; i < 1e5; i++) {
            crabe.actualiser(deltaTemps);
            crabe1.actualiser(deltaTemps);
        }

        assertEquals(100, crabe.getX(), accepte);
        assertEquals(140, crabe1.getX(), accepte);
    }
}