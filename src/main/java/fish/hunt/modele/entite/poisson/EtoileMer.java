package fish.hunt.modele.entite.poisson;

/**
 * Cette classe représente une étoile de mer.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class EtoileMer extends Poisson {

    private double yInit;
    private double tempsDepuisCreation;

    private final static double AMPLITUDE = 50;

    /**
     * Construit une étoile de mer avec tous les paramètres.
     * @param largeur   La largeur.
     * @param hauteur   La hauteur.
     * @param x         La position horizontale.
     * @param y         La position verticale.
     * @param vx        La vitesse horizontale.
     */
    public EtoileMer(double largeur, double hauteur, double x, double y,
                     double vx) {
        super(largeur, hauteur, x, y, vx, 0);
        yInit = y;
        ay = 0;
        tempsDepuisCreation = 0;
    }

    //TODO : Implémenter les tests de EtoileMer.actualiser(double)
    /**
     * Actualise le déplacement de l'étoile de mer selon l'intervalle de temps
     * depuis la dernière actualisation.
     * @param deltaTemps    L'intervalle de temps.
     */
    @Override
    public void actualiser(double deltaTemps) {
        tempsDepuisCreation += deltaTemps;
        x += vx * deltaTemps;
        y = AMPLITUDE * Math.sin(2 * Math.PI * tempsDepuisCreation) + yInit;
    }

}
