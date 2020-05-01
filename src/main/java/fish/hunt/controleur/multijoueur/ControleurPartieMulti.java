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

    private final int TEMPS_MESSAGE_AUTRE = 1;
    private final int TEMPS_MESSAGE_SCORES = 1;

    private final Object cadenas = new Object();
    private Set<Record> scores;
    private ConnexionServeur connexion;

    /*
    Variables utilisées pour l'affichage des messages en mode multijoueur.
    */
    private boolean attaqueEnCours, deconnexionEnCours, attaqueSpeciale, lancementAttaque;
    private double deltaMessage;

    //Variables pour afficher le score des joueurs.
    private int indexScores;
    private Record scoreAffiche;
    private Iterator<Record> itScores;
    private double deltaScores;

    private String nomAttaquant, nomDeconnexion;
    private Alert erreurConnexionAlert;

    /**
     * Construit un contrôleur de jeu avec la largeur et la hauteur du plan de
     * jeu, ainsi que la classe dessinable.
     */
    public ControleurPartieMulti(double largeur, double hauteur, Dessinable dessinable) {
        super(largeur, hauteur, dessinable);
        partie = new PartieMulti(this);
        planJeu.setPartie(partie);

        scores = new TreeSet<>();
        indexScores = 1;
        erreurConnexionAlert = new Alert(Alert.AlertType.ERROR, "Un erreur de connexion s'est produit.");

        new Thread(() -> {

            try {

                connexion = ConnexionServeur.getInstance();

                //On récupère le score des joueurs en ligne.
                int nombreJoueurs = connexion.lireInt();
                if(nombreJoueurs == -1)
                    throw new IOException();

                String pseudoJoueur;
                int scoreJoueur;
                for(int i = 0; i < nombreJoueurs; i++) {
                    pseudoJoueur = connexion.lireString();
                    if(pseudoJoueur == null)
                        throw new IOException();

                    scoreJoueur = connexion.lireInt();
                    if(scoreJoueur == -1)
                        throw new IOException();

                    synchronized (cadenas) {
                        scores.add(new Record(pseudoJoueur, scoreJoueur));
                    }
                }

                synchronized (cadenas) {
                    //Il y a au moins un joueur de connecté : nous.
                    itScores = scores.iterator();
                    scoreAffiche = itScores.next();
                }

            } catch (IOException ioException) {
                connexion.ferme();
                afficherErreur();
            }
        }).start();
    }

    @Override
    public void actualiser(double deltaTemps) {
        super.actualiser(deltaTemps);

        synchronized (cadenas) {

            if (lancementAttaque) {

                deltaMessage += deltaTemps;
                if(deltaMessage >= TEMPS_MESSAGE_AUTRE) {
                    deltaMessage = 0;
                    lancementAttaque = false;
                }

                dessinable.dessinerMessageMultijoueur("Vous avez lancé une attaque" +
                        (attaqueSpeciale ? " spécial." : "."));

            } else if (attaqueEnCours) {

                deltaMessage += deltaTemps;
                if (deltaMessage >= TEMPS_MESSAGE_AUTRE) {
                    deltaMessage = 0;
                    attaqueEnCours = false;
                }

                dessinable.dessinerMessageMultijoueur(nomAttaquant + " vient de vous envoyer un poisson" +
                        (attaqueSpeciale ? " spécial." : "."));

            } else if (deconnexionEnCours) {

                deltaMessage += deltaTemps;
                if (deltaMessage >= TEMPS_MESSAGE_AUTRE) {
                    deltaMessage = 0;
                    deconnexionEnCours = false;
                }

                dessinable.dessinerMessageMultijoueur(nomDeconnexion + " vient de quitter la partie.");

            } else if(scores.size() > 0) {

                deltaScores += deltaTemps;
                if (deltaScores >= TEMPS_MESSAGE_SCORES) {
                    deltaScores = 0;

                    if(!itScores.hasNext()) {
                        itScores = scores.iterator();
                        indexScores = 1;
                    }

                    scoreAffiche = itScores.next();
                }

                dessinable.dessinerMessageMultijoueur(indexScores + ". " + scoreAffiche);

            }

        }
    }

    public void attaquePoissonNormal() {
        lancementAttaque = true;
        attaqueSpeciale = false;

        new Thread(() -> {

            connexion.ecrireInt(ATTAQUE_POISSON_NORMAL_ENVOIE);

        }).start();
    }

    public void attaquePoissonSpecial() {
        lancementAttaque = true;
        attaqueSpeciale = true;

        new Thread(() -> {

            connexion.ecrireInt(ATTAQUE_POISSON_SPECIAL_ENVOIE);

        }).start();
    }

    public void miseAJourScore() {
        int scoreAEnvoyer = partie.getScore();
        new Thread(() -> {

            connexion.ecrireInt(MISE_A_JOUR_SCORE_ENVOIE);
            connexion.ecrireInt(scoreAEnvoyer);

        }).start();
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
