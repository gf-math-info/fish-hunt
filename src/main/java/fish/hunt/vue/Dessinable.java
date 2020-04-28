package fish.hunt.vue;

import fish.hunt.modele.Record;

/**
 * Cette interface doit être implémentée pour représenter le jeu.
 * Le contrôleur ou la vue appelleront ces méthodes pour animer le jeu.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public interface Dessinable {

    /**
     * Vide le plan de jeu. Seul le fond marin reste.
     */
    void viderPlan();

    /**
     * Gère la partie terminée avec le score final.
     * @param score Le score final de la partie.
     */
    void partieTermine(int score);

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
     * Dessine le score et le nombre de poissons qui restent au joueur.
     * @param score                 Le score de la partie.
     * @param nbPoissonsRestants    Le nombre de poissons restants du joueur.
     */
    void dessinerScore(int score, int nbPoissonsRestants);

    /**
     * Dessine une bulle d'une certaine dimension à une certaine position.
     * @param x             La position horizontale.
     * @param y             La position verticale.
     * @param diametre      Le rayon de la bulle.
     */
    void dessinerBulle(double x, double y, double diametre);

    /**
     * Dessine un poisson à une certaine position et d'une certaine dimension.
     * @param x             La position horizontale.
     * @param y             La position verticale.
     * @param largeur       La largeur.
     * @param hauteur       La hauteur.
     * @param versDroite    Vrai si le poisson se dirige vers la droite,
     *                      faux sinon.
     * @param numCouleur    Le numéro de la couleur du poisson.
     * @param numImage      Le numéro de l'image du poisson.
     */
    void dessinerPoisson(double x, double y,
                         double largeur, double hauteur, boolean versDroite,
                         int numImage, int numCouleur);

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

    /**
     * Dessine un projectile à une certaine position et d'un certain diamètre.
     * @param x         La position horizontale du projectile.
     * @param y         La position verticale du projectile.
     * @param diametre  Le diamètre du projectile.
     */
    void dessinerProjectile(double x, double y, double diametre);

    /**
     * Dessine le nombre de tire un-projectile-un-mort.
     * @param nbUnProjectileUnMort  Le nombre de tire un-projectile-un-mort.
     */
    void dessinerCombo(int nbUnProjectileUnMort);

    /**
     * Dessine les scores des participants en mode multijoueur
     * @param scores    Un tableau de scores.
     */
    void dessinerScoresMultijoueur(Record[] scores);

    /**
     * Dessine le record des meilleurs joueurs en mode multijoueur.
     * @param records   Un tableau de records.
     */
    void dessinerMeilleursScores(Record[] records);

    /**
     * Dessine un message d'information en mode multijoueur.
     * @param message   Un message d'information.
     */
    void dessinerMessageMultijoueur(String message);

    /**
     * Accesseur du nombre d'images de poissons disponibles.
     * @return  Le nombre d'images de poissons disponibles.
     */
    int getNombreImagesPoissons();

    /**
     * Accesseur du nombre de couleurs disponibles pour les différents poissons.
     * @return  Le nombre de couleurs disponibles pour les différents poissons.
     */
    int getNombreCouleurPoisson();
}
