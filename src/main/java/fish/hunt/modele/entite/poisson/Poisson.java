package fish.hunt.modele.entite.poisson;

import fish.hunt.modele.entite.Entite;

/**
 * Cette classe représente un poisson dit normal.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class Poisson extends Entite {

    private final static double ACCELERATION_VERTICALE_DEFAUT = 100;

    protected double ay;

    /**
     * Construit un poisson avec tous les paramètres.
     * @param largeur   La largeur.
     * @param hauteur   La hauteur.
     * @param x         La position horizontale.
     * @param y         La position verticale.
     * @param vx        La vitesse horizontale.
     * @param vy        La vitesse verticale.
     */
    public Poisson(double largeur, double hauteur, double x, double y,
                   double vx, double vy) {
        super(largeur, hauteur, x, y, vx, vy);
        ay = ACCELERATION_VERTICALE_DEFAUT;
    }


    /**
     * Actualise le déplacement du poisson selon l'intervalle de temps depuis
     * la dernière actualisation.
     * @param deltaTemps    L'intervalle de temps.
     */
    @Override
    public void actualiser(double deltaTemps) {
        vy += ay * deltaTemps;
        x += vx * deltaTemps;
        y += vy * deltaTemps;
    }
}
