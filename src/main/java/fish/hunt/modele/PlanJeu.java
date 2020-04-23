package fish.hunt.modele;

import fish.hunt.modele.entite.Bulle;
import fish.hunt.modele.entite.Projectile;
import fish.hunt.modele.entite.poisson.Poisson;

import java.util.ArrayList;

public class PlanJeu {

    private double largeur, hauteur;

    private ArrayList<Bulle> bulles;
    private ArrayList<Projectile> projectiles;
    private ArrayList<Poisson> poissons;

    public PlanJeu(double largeur, double hauteur) {
        this.largeur = largeur;
        this.hauteur = hauteur;

        bulles = new ArrayList<>();
        projectiles = new ArrayList<>();
        poissons = new ArrayList<>();
    }

    public void actualiser(double deltaTemps) {
        bulles.forEach(bulle -> bulle.actualiser(deltaTemps));
        projectiles.forEach(projectile -> projectile.actualiser(deltaTemps));
        poissons.forEach(poisson -> poisson.actualiser(deltaTemps));

        bulles.removeIf(bulle -> bulle.getY() + bulle.getHauteur() > 0);
        poissons.removeIf(poisson ->
                poisson.getVx() < 0
                        && poisson.getX() + poisson.getLargeur() < 0 ||
                poisson.getVx() > 0 && poisson.getX() > largeur);
    }

    public ArrayList<Bulle> getBulles() {
        return bulles;
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public ArrayList<Poisson> getPoissons() {
        return poissons;
    }
}
