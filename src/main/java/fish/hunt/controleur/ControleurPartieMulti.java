package fish.hunt.controleur;

import fish.hunt.modele.Record;
import fish.hunt.vue.Dessinable;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class ControleurPartieMulti extends ControleurPartie{

    private final int ATTAQUE_POISSON_NORMAL_ENVOIE = 50;
    private final int ATTAQUE_POISSON_SPECIAL_ENVOIE = 51;
    private final int MISE_A_JOUR_SCORE_ENVOIE = 60;
    private final int ATTAQUE_POISSON_NORMAL_RECU = 150;
    private final int ATTAQUE_POISSON_SPECIAL_RECU = 151;
    private final int MISE_A_JOUR_SCORE_RECU = 160;
    private final int DECONNEXION_JOUEUR_RECU = 190;

    private final int TEMPS_MESSAGE_ATTAQUE = 1;
    private final int TEMPS_MESSAGE_SCORES = 1;
    private final int TEMPS_MESSAGE_DECONNEXION = 1;

    private final Object cadenas;
    private Set<Record> scores;
    private boolean attaqueEnCours, deconnexionEnCours, attaqueSpecial;
    private double deltaAttaque, deltaDeconnexion, deltaScores;
    private int indexScores;
    private ConnexionServeur connexion;
    private String nomAttaquant, nomDeconnexion;
    private Alert erreurConnexionAlert;

    /**
     * Construit un contrôleur de jeu avec la largeur et la hauteur du plan de
     * jeu, ainsi que la classe dessinable.
     *
     * @param largeur    La largeur du plan de jeu.
     * @param hauteur    La hauteur du plan de jeu.
     * @param dessinable La classe dessinable.
     */
    public ControleurPartieMulti(double largeur, double hauteur, Dessinable dessinable) {
        super(largeur, hauteur, dessinable);
        scores = new TreeSet<>();
        cadenas = new Object();
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

                //En entre dans la partie.
                while(!partie.estPerdue()) {

                    switch (connexion.getInput().read()) {

                        case ATTAQUE_POISSON_NORMAL_RECU:
                            String attaquant = connexion.getInput().readLine();
                            if(attaquant == null) {
                                afficherErreur();
                                return;
                            }

                            synchronized (cadenas) {
                                nomAttaquant = attaquant;
                                attaqueEnCours = true;
                                attaqueSpecial = false;
                                deltaAttaque = 0;
                            }

                            Platform.runLater(() -> {
                                planJeu.ajouterPoissonNormal();
                            });
                            break;

                        case ATTAQUE_POISSON_SPECIAL_RECU:
                            String attaquantSpecial = connexion.getInput().readLine();
                            if(attaquantSpecial == null) {
                                afficherErreur();
                                return;
                            }

                            synchronized (cadenas) {
                                nomAttaquant = attaquantSpecial;
                                attaqueEnCours = true;
                                attaqueSpecial = true;
                                deltaAttaque = 0;
                            }

                            Platform.runLater(() -> {
                                planJeu.ajouterPoissonSpecial();
                            });

                        case MISE_A_JOUR_SCORE_RECU:
                            String nomScore = connexion.getInput().readLine();
                            if(nomScore == null) {
                                afficherErreur();
                                return;
                            }

                            int score = connexion.getInput().read();
                            if(score == -1) {
                                afficherErreur();
                                return;
                            }

                            synchronized (cadenas) {
                                Record recordMiseAJour = null;
                                for(Record record : scores)
                                    if(record.getNom().equals(nomScore))
                                        recordMiseAJour = record;

                                if(recordMiseAJour == null)
                                    scores.add(new Record(nomScore, score));
                                else
                                    recordMiseAJour.setScore(score);


                            }
                            break;

                        case DECONNEXION_JOUEUR_RECU:
                            String nomJoueurDeconnexion = connexion.getInput().readLine();
                            if(nomJoueurDeconnexion == null) {
                                afficherErreur();
                                return;
                            }

                            synchronized (cadenas) {
                                nomDeconnexion = nomJoueurDeconnexion;
                                deconnexionEnCours = true;
                                deltaDeconnexion = 0;
                                scores.stream()
                                        .filter(record -> record.getNom().equals(nomJoueurDeconnexion))
                                        .findFirst().ifPresent(recordARetirer -> scores.remove(recordARetirer));
                            }
                            break;
                    }
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

                Platform.runLater(() -> {
                    dessinable.dessinerMessageMultijoueur(nomAttaquant + " vient de vous envoyer un poisson" +
                            (attaqueSpecial ? " spécial." : "."));
                });

            } else if (deconnexionEnCours) {

                deltaDeconnexion += deltaTemps;
                if (deltaDeconnexion >= TEMPS_MESSAGE_DECONNEXION) {
                    deltaDeconnexion = 0;
                    attaqueEnCours = false;
                }

                Platform.runLater(() -> {
                    dessinable.dessinerMessageMultijoueur(nomDeconnexion + " vient de quitter la partie.");
                });

            } else if(scores.size() > 0) {
                deltaScores += deltaTemps;
                if (deltaScores >= TEMPS_MESSAGE_SCORES) {
                    deltaScores = 0;
                    indexScores ++;
                    indexScores %= scores.size();
                }

                Platform.runLater(() -> {
                    synchronized (cadenas) {
                        dessinable.dessinerMessageMultijoueur((indexScores + 1) + ". " + scores);
                    }
                });

            }

        }
    }

    /**
     * Affiche une boite modale signifiant une erreur et met fin à la partie.
     */
    private void afficherErreur() {
        Platform.runLater(() -> {
            erreurConnexionAlert.showAndWait();
            dessinable.partieTermine(partie.getScore());
        });
    }
}
