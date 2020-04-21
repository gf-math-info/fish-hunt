package fish.hunt.modele.entite;

/**
 * Cette classe représente un poisson dit normal.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class Poisson extends Entite {

    /**
     * Construit un poisson avec tous les paramètres.
     * @param largeur   La largeur.
     * @param hauteur   La hauteur.
     * @param x         La position horizontale.
     * @param y         La position verticale.
     * @param vx        La vitesse horizontale.
     * @param vy        La vitesse verticale.
     * @param ax        L'accélération horizontale.
     * @param ay        L'accélération verticale.
     */
    public Poisson(double largeur, double hauteur, double x, double y,
                   double vx, double vy, double ax, double ay) {
        super(largeur, hauteur, x, y, vx, vy, ax, ay);
    }


    /**
     * Actualise le déplacement du poisson selon l'intervalle de temps depuis
     * la dernière actualisation.
     * @param deltaTemps    L'intervalle de temps.
     */
    @Override
    public void actualiser(double deltaTemps) {
        //TODO : Implémenter Poisson.actualiser(double)
    }
}
