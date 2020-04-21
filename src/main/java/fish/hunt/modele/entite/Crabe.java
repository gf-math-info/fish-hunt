package fish.hunt.modele.entite;

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
        avance = true;
    }

    //TODO : Implémenter les tests de Crabe.actualiser(double)
    /**
     * Actualise le déplacement de l'entité selon l'intervalle de temps depuis
     * la dernière actualisation.
     * @param deltaTemps    L'intervalle de temps.
     */
    @Override
    public void actualiser(double deltaTemps) {
        super.actualiser(deltaTemps);

        if(avance) {

            tempsAvance += deltaTemps;
            if(tempsAvance >= DELTA_TEMPS_AVANCE) {
                vy = -vy;
                avance = false;
                tempsAvance = 0;
            }

        } else {

            tempsRecule += deltaTemps;
            if(tempsRecule >= DELTA_TEMPS_RECULE) {
                vy = -vy;
                avance = true;
                tempsRecule = 0;
            }

        }
    }
}
