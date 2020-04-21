package fish.hunt.modele.entite;

public class Projectile extends Entite {

    private final static double GRANDEUR_DEFAUT = 50;

    /**
     * Construit un projectile selon une position.
     * @param x La position horizontale.
     * @param y La position verticale.
     */
    public Projectile(double x, double y) {
        super(GRANDEUR_DEFAUT, GRANDEUR_DEFAUT, x, y, 0, 0, 0, 0);
    }

    /**
     * Actualise le déplacement du projectile selon l'intervalle de temps depuis
     * la dernière actualisation.
     * @param deltaTemps    L'intervalle de temps.
     */
    @Override
    public void actualiser(double deltaTemps) {
        //TODO : Implémenter Projectile.actualiser(double)
    }
}
