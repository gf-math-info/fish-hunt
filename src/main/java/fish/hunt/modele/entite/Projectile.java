package fish.hunt.modele.entite;

/**
 * Cette classe représente un projectile à lancer.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class Projectile extends Entite {

    private boolean aNiveau;

    private final static double VITESSE_ELOIGNEMENT = 300;
    private final static double GRANDEUR_DEFAUT = 50;

    /**
     * Construit un projectile selon une position.
     * @param x La position horizontale.
     * @param y La position verticale.
     */
    public Projectile(double x, double y) {
        super(GRANDEUR_DEFAUT, GRANDEUR_DEFAUT, x, y, 0, 0, 0, 0);
    }

    //TODO : Implémenter les tests de Projectile.intersecte(Poisson)
    /**
     * Vérifie si le projectile est en contact avec le poisson. La logique de
     * l'algorithme est tiré du site Yellowafterlife.
     * @see             "https://yal.cc/rectangle-circle-intersection-test/"
     * @param poisson   Le poisson à évaluer.
     * @return          Vrai si le projectile est en contact avec le poisson,
     *                  faux sinon.
     */
    public boolean intersect(Poisson poisson) {
        double pointX = Math.max(poisson.getX(),
                Math.min(x, poisson.getX() + poisson.getLargeur()));
        double pointY = Math.max(poisson.getY(),
                Math.min(y, poisson.getY() + poisson.getHauteur()));

        double deltaX = x - pointX, deltaY = y - pointY;

        return (deltaX * deltaX + deltaY * deltaY) < largeur * largeur / 4;
    }

    //TODO : Implémenter les tests de Projectile.actualiser(double).
    /**
     * Actualise le déplacement du projectile selon l'intervalle de temps depuis
     * la dernière actualisation.
     * @param deltaTemps    L'intervalle de temps.
     */
    @Override
    public void actualiser(double deltaTemps) {
        double derniereLargeur = largeur;
        largeur -= VITESSE_ELOIGNEMENT * deltaTemps;
        hauteur -= VITESSE_ELOIGNEMENT * deltaTemps;

        if(largeur <= 0) {
            largeur = hauteur = 0;
            aNiveau = true;//Peut atteindre des poissons.
        }

        double deltaEspace = (derniereLargeur - largeur) / 2;

        x += deltaEspace;
        y += deltaEspace;
    }
}
