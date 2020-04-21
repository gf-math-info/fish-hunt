package fish.hunt.modele.entite;

public class Bulle extends Entite {

    /**
     * Construit une bulle selon son rayon, sa position et sa vitesse verticale.
     * @param rayon Le rayon.
     * @param x     La position verticale.
     * @param y     La position horizontale.
     * @param vy    La vitesse horizontale.
     */
    public Bulle(double rayon, double x, double y, double vy) {
        super(rayon, rayon, x, y, 0, vy, 0, 0);
    }

    /**
     * Actualise le déplacement de la bulle selon l'intervalle de temps depuis
     * la dernière actualisation.
     * @param deltaTemps    L'intervalle de temps.
     */
    @Override
    public void actualiser(double deltaTemps) {
        //TODO : Implémenter Bulle.actualiser(double)
    }
}
