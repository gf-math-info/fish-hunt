package fish.hunt.vue;

/**
 * Cette interface doit être implémenté pour représenter le jeu. Le contrôleur
 * ou la vue appelleront ces méthodes pour animer le jeu.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public interface Dessinable {

    /**
     * Vide le plan de jeu. Seul le fond marin reste.
     */
    void viderPlan();

    /**
     * Affiche un message au joueur lui expliquant que la partie est terminée.
     */
    void afficheFinPartie();

    /**
     * Affiche un message avec le niveau.
     * @param niveau    Le niveau du jeu.
     */
    void afficherNouveauNiveau(int niveau);

    /**
     * Gère la partie terminée avec le score final.
     * @param score Le score final de la partie.
     */
    void partieTermine(int score);

    /**
     * Dessine le score et le nombre de poissons qui restent au joueur.
     * @param score                 Le score de la partie.
     * @param nbPoissonsRestants    Le nombre de poissons restants du joueur.
     */
    void dessinerScore(int score, int nbPoissonsRestants);

    /**
     * Dessine une cible à une certaine position.
     * @param x La position horizontale.
     * @param y La position verticale.
     */
    void dessinerCible(double x, double y);

    /**
     * Dessine une bulle d'une certaine dimension à une certaine position.
     * @param x         La position horizontale.
     * @param y         La position verticale.
     * @param rayon     Le rayon de la bulle.
     */
    void dessinerBulle(double x, double y, double rayon);

    /**
     * Dessine un poisson à une certaine position et d'une certaine dimension.
     * @param x             La position horizontale.
     * @param y             La position verticale.
     * @param largeur       La largeur.
     * @param hauteur       La hauteur.
     * @param versDroite    Vrai si le poisson se dirige vers la droite,
     *                      faux sinon.
     */
    void dessinerPoisson(double x, double y,
                         double largeur, double hauteur, boolean versDroite);

    /**
     * Dessine une étoile de mer à une certaine position et d'une certaine
     * dimension.
     * @param x         La position horizontale.
     * @param y         La position verticale.
     * @param largeur   La largeur.
     * @param hauteur   La hauteur.
     */
    void dessinerEtoileMer(double x, double y,
                           double largeur, double hauteur);

    /**
     * Dessine un crabe à une certaine position et d'une certaine dimension.
     * @param x         La position horizontale.
     * @param y         La position verticale.
     * @param largeur   La largeur.
     * @param hauteur   La hauteur.
     */
    void dessinerCrabe(double x, double y,
                       double largeur, double hauteur);
}
