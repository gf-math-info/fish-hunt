package fish.hunt.modele.entite.poisson;

/**
 * Cette classe représente un crabe.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class Crabe extends Poisson {

    private final static double
            DELTA_TEMPS_AVANCE = 0.5,
            DELTA_TEMPS_RECULE = 0.25;

    private double tempsAvance, tempsRecule;
    private boolean avance;

    /**
     * Construit un crabe avec tous les paramètre.
     * @param largeur   La largeur.
     * @param hauteur   La hauteur.
     * @param x         La position horizontale.
     * @param y         La position verticale.
     * @param vx        La vitesse horizontale.
     */
    public Crabe(double largeur, double hauteur, double x, double y,
                 double vx) {
        super(largeur, hauteur, x, y, vx, 0);
        ay = 0;
        avance = true;
    }

    //TODO : Implémenter les tests de Crabe.actualiser(double)
    /**
     * Actualise le déplacement du crabe selon l'intervalle de temps depuis
     * la dernière actualisation.
     * @param deltaTemps    L'intervalle de temps.
     */
    @Override
    public void actualiser(double deltaTemps) {
        x += vx * deltaTemps;

        if(avance) {

            tempsAvance += deltaTemps;
            if(tempsAvance >= DELTA_TEMPS_AVANCE) {
                vx = -vx;
                avance = false;
                tempsAvance = 0;
            }

        } else {

            tempsRecule += deltaTemps;
            if(tempsRecule >= DELTA_TEMPS_RECULE) {
                vx = -vx;
                avance = true;
                tempsRecule = 0;
            }

        }
    }
}
