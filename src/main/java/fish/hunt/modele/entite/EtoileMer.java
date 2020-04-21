package fish.hunt.modele.entite;

/**
 * Cette classe représente une étoile de mer.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class EtoileMer extends Poisson{

    /**
     * Construit une étoile de mer avec tous les paramètres.
     * @param largeur   La largeur.
     * @param hauteur   La hauteur.
     * @param x         La position horizontale.
     * @param y         La position verticale.
     * @param vx        La vitesse horizontale.
     * @param vy        La vitesse verticale.
     * @param ax        L'accélération horizontale.
     * @param ay        L'accélération verticale.
     */
    public EtoileMer(double largeur, double hauteur, double x, double y,
                     double vx, double vy, double ax, double ay) {
        super(largeur, hauteur, x, y, vx, vy, ax, ay);
    }
}
