package fish.hunt.modele.entite;

/**
 * Cette classe représente un crabe.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class Crabe extends Poisson {

    /**
     * Construit un crabe avec tous les paramètre.
     * @param largeur   La largeur.
     * @param hauteur   La hauteur.
     * @param x         La position horizontale.
     * @param y         La position verticale.
     * @param vx        La vitesse horizontale.
     * @param vy        La vitesse verticale.
     * @param ax        L'accélération horizontale.
     * @param ay        L'accélération verticale.
     */
    public Crabe(double largeur, double hauteur, double x,
                 double y, double vx, double vy, double ax, double ay) {
        super(largeur, hauteur, x, y, vx, vy, ax, ay);
    }
}
