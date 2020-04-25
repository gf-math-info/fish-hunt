package fish.hunt.modele.entite;

import fish.hunt.modele.entite.poisson.Poisson;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProjectileTest {

    private double deltaTemps = 1e-5;
    private double accepte = 0.001;
    private double tempsArrive = 1e5 / 6;

    @Test
    public void testIntersect() {

        Poisson poisson = new Poisson(100, 0, 0, 0, 0);

        Projectile projectile = new Projectile(0, 0);
        assertTrue(projectile.intersect(poisson));

        projectile = new Projectile(-1, 0);
        assertFalse(projectile.intersect(poisson));

        projectile = new Projectile(50, 50);
        assertTrue(projectile.intersect(poisson));

        projectile = new Projectile(101, 101);
        assertFalse(projectile.intersect(poisson));

        projectile = new Projectile(50, 101);
        assertFalse(projectile.intersect(poisson));
    }

    @Test
    public void testActualiser() {
        Projectile projectile = new Projectile(0, 0);
        for(int i = 0; i < tempsArrive; i++)
            projectile.actualiser(deltaTemps);

        assertEquals(0, projectile.getDiametre(), accepte);

        projectile = new Projectile(0, 0);
        for(int i = 0; i < tempsArrive + 1e4; i++)
            projectile.actualiser(deltaTemps);

        assertEquals(0, projectile.getDiametre(), accepte);

        projectile = new Projectile(0, 0);
        for(int i = 0; i < 20; i++)
            projectile.actualiser(deltaTemps);

        assertEquals(49.94, projectile.getDiametre(), accepte);
    }
}