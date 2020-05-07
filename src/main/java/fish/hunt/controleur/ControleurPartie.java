package fish.hunt.controleur;

import fish.hunt.modele.Partie;
import fish.hunt.modele.PlanJeu;
import fish.hunt.modele.entite.Bulle;
import fish.hunt.modele.entite.Projectile;
import fish.hunt.modele.entite.poisson.Crabe;
import fish.hunt.modele.entite.poisson.EtoileMer;
import fish.hunt.modele.entite.poisson.Poisson;
import fish.hunt.vue.Dessinable;

import java.util.Random;
import java.util.WeakHashMap;

/**
 * Cette classe est le contrôleur de jeu. La vue dit au controleur qu'elle
 * est prête à se mettre à jour, le contrôleur met à jour la partie et dicte
 * à la vue quoi dessiner. La vue ne fait affaire qu'avec cette classe.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class ControleurPartie {

    protected Partie partie;
    protected PlanJeu planJeu;
    protected Dessinable dessinable;

    protected boolean augmenteNiveau;
    protected double deltaMessage;
    protected final double TEMPS_MESSAGE = 3;

    /**
     * Construit un contrôleur de jeu avec la largeur et la hauteur du plan de
     * jeu, ainsi que la classe dessinable.
     * @param largeur       La largeur du plan de jeu.
     * @param hauteur       La hauteur du plan de jeu.
     * @param dessinable    La classe dessinable.
     */
    public ControleurPartie(double largeur, double hauteur, Dessinable dessinable) {
        this.dessinable = dessinable;
        partie = new Partie(this);
        planJeu = new PlanJeu(largeur, hauteur, partie);

        augmenteNiveau = true;
    }

    /**
     * Actualise la partie. Le contrôleur dicte également à la
     * classe dessinable quoi dessiner.
     * @param deltaTemps    L'intervalle de temps depuis la dernière
     *                      actualisation.
     */
    public void actualiser(double deltaTemps) {
        dessinable.viderPlan();
        if(augmenteNiveau) { // si le message Level X est en affichage.

            deltaMessage += deltaTemps;
            if(deltaMessage < TEMPS_MESSAGE)
                dessinable.afficherNouveauNiveau(partie.getNiveau());
            else {
                augmenteNiveau = false;
                deltaMessage = 0;
            }

        } else if(partie.estPerdue()){

            deltaMessage += deltaTemps;
            if(deltaMessage < TEMPS_MESSAGE)
                dessinable.afficheFinPartie();
            else {
                dessinable.partieTermine(partie.getScore());
            }

        } else {

            //On actualise la partie et on l'affiche.
            planJeu.actualiser(deltaTemps);

            dessinerBulles();
            dessinerPoisson();
            dessinerProjectiles();
            dessinerInformations();
        }
    }

    /**
     * Méthode appelée par la partie pour signaler au contrôleur que le niveau de la partie vient d'augmenter.
     */
    public void augmenteNiveau() {
        augmenteNiveau = true;
    }

    //Les méthodes suivantes sont utilisées par la vue pour les actions du joueur pour dessiner la partie.

    /**
     * Ajoute un projectile au plan de jeu.
     * @param x La position horizontale du projectile.
     * @param y La position verticale du projectile.
     */
    public void ajouterProjectile(double x, double y) {
        if(!augmenteNiveau && !partie.estPerdue())
            planJeu.getProjectiles().add(new Projectile(x, y));
    }

    /**
     * Incrémente la partie d'un niveau.
     */
    public void incrementerNiveau() {
        partie.incrementerNiveau();
        deltaMessage = 0;
    }

    /**
     * Incrémente la partie d'un point de score.
     */
    public void incrementerScore() {
        partie.incrementerScore();
    }

    /**
     * Incrémente le nombre de poissons de vie restants.
     */
    public void incrementerPoissonRestant() {
        partie.incrementerVie();
    }

    /**
     * Fait perdre la partie.
     */
    public void partiePerdue() {
        partie.setPerdue(true);
    }


    //Les méthodes suivantes sont utilisées par l'instance du contrôleur du jeu.

    /**
     * Demande à la vue de dessiner les bulles de la partie.
     */
    protected void dessinerBulles() {
        for(Bulle bulle : planJeu.getBulles())
            dessinable.dessinerBulle(bulle.getX(), bulle.getY(),
                    bulle.getDiametre());
    }

    /**
     * Demande à la vue de dessiner les poissons de la partie.
     */
    protected void dessinerPoisson() {
        for(Poisson poisson : planJeu.getPoissons()) {

            if(poisson instanceof EtoileMer)

                dessinable.dessinerEtoileMer(poisson.getX(), poisson.getY(),
                        poisson.getLargeur(), poisson.getHauteur());

            else if(poisson instanceof Crabe)

                dessinable.dessinerCrabe(poisson.getX(), poisson.getY(), poisson.getLargeur(), poisson.getHauteur());

            else

                dessinable.dessinerPoisson(poisson);
        }
    }

    /**
     * Demande à la vue de dessiner les projectiles de la partie.
     */
    protected void dessinerProjectiles() {
        for(Projectile projectile : planJeu.getProjectiles()) {
            dessinable.dessinerProjectile(projectile.getX(),
                    projectile.getY(), projectile.getDiametre());
        }
    }

    /**
     * Demande à la vue de dessiner le score, le nombre de vies restantes et le nombre de tirs parfaits de suite.
     */
    protected void dessinerInformations() {
        dessinable.dessinerScore(partie.getScore(), partie.getNbViesRestantes());

        if(partie.getNbUnProjectileUnMort() > 0)
            dessinable.dessinerCombo(partie.getNbUnProjectileUnMort());
    }
}