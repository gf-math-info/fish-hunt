package fish.hunt.controleur.multijoueur;

import fish.hunt.controleur.ControleurPartie;
import fish.hunt.modele.Partie;
import fish.hunt.modele.PartieMulti;
import fish.hunt.modele.Record;
import fish.hunt.vue.Dessinable;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class ControleurPartieMulti extends ControleurPartie {

    private final int ATTAQUE_POISSON_NORMAL_ENVOIE = 50;
    private final int ATTAQUE_POISSON_SPECIAL_ENVOIE = 51;
    private final int MISE_A_JOUR_SCORE_ENVOIE = 60;

    private final int TEMPS_MESSAGE_ATTAQUE = 1;
    private final int TEMPS_MESSAGE_SCORES = 1;
    private final int TEMPS_MESSAGE_DECONNEXION = 1;

    private final Object cadenas = new Object();
    private Set<Record> scores;

    private boolean attaqueEnCours, deconnexionEnCours, attaqueSpecial;
    private double deltaAttaque, deltaDeconnexion, deltaScores;
    private int indexScores;
    private Record scoreAffiche;
    private Iterator<Record> itRecord;
    private ConnexionServeur connexion;
    private String nomAttaquant, nomDeconnexion;
    private Alert erreurConnexionAlert;

    /**
     * Construit un contrôleur de jeu avec la largeur et la hauteur du plan de
     * jeu, ainsi que la classe dessinable.
     * @param largeur    La largeur du plan de jeu.
     * @param hauteur    La hauteur du plan de jeu.
     * @param dessinable La classe dessinable.
     */
    public ControleurPartieMulti(double largeur, double hauteur, Dessinable dessinable) {
        super(largeur, hauteur, dessinable);
        partie = new PartieMulti(this);
        planJeu.setPartie(partie);

        scores = new TreeSet<>();
        erreurConnexionAlert = new Alert(Alert.AlertType.ERROR, "Un erreur de connexion s'est produit.");

        new Thread(() -> {

            try {

                connexion = ConnexionServeur.getInstance();

                //On récupère le score des joueurs en ligne.
                int nombreJoueurs = connexion.getInput().read();
                if(nombreJoueurs == -1) {
                    afficherErreur();
                    return;
                }

                String pseudoJoueur;
                int scoreJoueur;
                for(int i = 0; i < nombreJoueurs; i++) {
                    pseudoJoueur = connexion.getInput().readLine();
                    if(pseudoJoueur == null) {
                        afficherErreur();
                        return;
                    }
                    scoreJoueur = connexion.getInput().read();
                    if(scoreJoueur == -1) {
                        afficherErreur();
                        return;
                    }

                    synchronized (cadenas) {
                        scores.add(new Record(pseudoJoueur, scoreJoueur));
                    }
                }

                synchronized (cadenas) {

                }



            } catch (IOException ioException) {
                afficherErreur();
                ioException.printStackTrace();
            }
        }).start();
    }

    @Override
    public void actualiser(double deltaTemps) {
        super.actualiser(deltaTemps);

        synchronized (cadenas) {

            if (attaqueEnCours) {

                deltaAttaque += deltaTemps;
                if (deltaAttaque >= TEMPS_MESSAGE_ATTAQUE) {
                    deltaAttaque = 0;
                    attaqueEnCours = false;
                }

                dessinable.dessinerMessageMultijoueur(nomAttaquant + " vient de vous envoyer un poisson" +
                        (attaqueSpecial ? " spécial." : "."));

            } else if (deconnexionEnCours) {

                deltaDeconnexion += deltaTemps;
                if (deltaDeconnexion >= TEMPS_MESSAGE_DECONNEXION) {
                    deltaDeconnexion = 0;
                    attaqueEnCours = false;
                }

                dessinable.dessinerMessageMultijoueur(nomDeconnexion + " vient de quitter la partie.");

            } else if(scores.size() > 0) {
                deltaScores += deltaTemps;
                if (deltaScores >= TEMPS_MESSAGE_SCORES) {
                    deltaScores = 0;
                    //TODO
                }

                dessinable.dessinerMessageMultijoueur((indexScores + 1) + ". " + scores);

            }

        }
    }

    public void attaquePoissonNormal() {
        //TODO
    }

    public void attaquePoissonSpecial() {
        //TODO
    }

    public void miseAJourScore() {
        //TODO
    }

    public void attaquePoissonNormal(String pseudoAttaquant) {
        //TODO
    }

    public void attaquePoissonSpecial(String pseudoAttaquant) {
        //TODO
    }

    public void miseAJourScore(String pseudo, int score) {
        //TODO
    }

    public void deconnexionJoueur(String pseudo) {
        //TODO
    }

    /**
     * Accesseur du cadenas.
     * @return  Le cadenas pour modifier les attributs du controleur.
     */
    public Object getCadenas() {
        return cadenas;
    }

    /**
     * Accesseur de la partie en cours.
     * @return  La partie en cours.
     */
    public Partie getPartie() {
        return partie;
    }

    /**
     * Affiche une boite modale signifiant une erreur et met fin à la partie.
     */
    public void afficherErreur() {
        Platform.runLater(() -> {
            erreurConnexionAlert.showAndWait();
            dessinable.partieTermine(partie.getScore());
        });
    }
}
