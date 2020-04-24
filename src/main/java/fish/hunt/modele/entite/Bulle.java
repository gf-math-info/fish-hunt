package fish.hunt.modele.entite;

/**
 * Cette classe représente les bulles.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class Bulle extends Entite {

    /**
     * Construit une bulle selon son rayon, sa position et sa vitesse verticale.
     * @param diametre  Le diamètre de la bulle.
     * @param x         La position verticale.
     * @param y         La position horizontale.
     * @param vy        La vitesse horizontale.
     */
    public Bulle(double diametre, double x, double y, double vy) {
        super(diametre, diametre, x, y, 0, vy);
    }

    //TODO : Implémentation des tests de Bulle.actualiser(double)
    /**
     * Actualise le déplacement de la bulle selon l'intervalle de temps depuis
     * la dernière actualisation.
     * @param deltaTemps    L'intervalle de temps.
     */
    @Override
    public void actualiser(double deltaTemps) {
        y += vy * deltaTemps;
    }

    /**
     * Accesseur du diamètre de la bulle.
     * @return  Le diamètre de la bulle.
     */
    public double getDiametre() {
        return largeur;
    }
}
