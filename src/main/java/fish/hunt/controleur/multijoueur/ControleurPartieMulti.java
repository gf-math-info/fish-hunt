package fish.hunt.controleur.multijoueur;

import fish.hunt.controleur.ControleurPartie;
import fish.hunt.modele.Partie;
import fish.hunt.modele.PartieMulti;
import fish.hunt.modele.Record;
import fish.hunt.vue.Dessinable;
import javafx.application.Platform;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Cette classe représente le contrôleur d'une partie en mode multijoueur.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class ControleurPartieMulti extends ControleurPartie {

    private final int ATTAQUE_POISSON_NORMAL_ENVOIE = 50;
    private final int ATTAQUE_POISSON_SPECIAL_ENVOIE = 51;
    private final int MISE_A_JOUR_SCORE_ENVOIE = 60;

    private final int TEMPS_MESSAGE_AUTRE = 2;
    private final int TEMPS_MESSAGE_SCORES = 1;

    private final Object cadenasDonneesAffichage = new Object();
    private ArrayList<Record> scores;
    private Comparator<Record> comparator;
    private ConnexionServeur connexion;
    private PrintWriter output;
    private Receveur receveur;

    /*
    Variables utilisées pour l'affichage des messages en mode multijoueur.
    */
    private boolean erreurConnexion, attaqueEnCours, deconnexionEnCours, connexionEnCours, attaqueSpeciale,
            lancementAttaque;
    private double deltaMessage;

    //Variables pour afficher le score des joueurs.
    private int indexScores;
    private double deltaScores;

    private String nomAttaquant, nomDeconnexion, nomConnexion;

    /**
     * Construit un contrôleur de jeu avec la largeur et la hauteur du plan de
     * jeu, ainsi que la classe dessinable.
     */
    public ControleurPartieMulti(double largeur, double hauteur, Dessinable dessinable) {
        super(largeur, hauteur, dessinable);
        partie = new PartieMulti(this);
        planJeu.setPartie(partie);

        scores = new ArrayList<>();
        indexScores = 0;
        comparator = new Comparator<Record>() {
            @Override
            public int compare(Record record1, Record record2) {
                return record2.compareTo(record1);
            }
        };

        receveur = new Receveur(this);

        try {

            connexion = ConnexionServeur.getInstance();
            output = connexion.getOutput();

            //On récupère le score des joueurs en ligne.
            int nombreJoueurs = connexion.getInput().read();
            if(nombreJoueurs == -1)
                throw new IOException();

            String pseudoJoueur;
            int scoreJoueur;
            for(int i = 0; i < nombreJoueurs; i++) {
                pseudoJoueur = connexion.getInput().readLine();
                if(pseudoJoueur == null)
                    throw new IOException();

                scoreJoueur = connexion.getInput().read();
                if(scoreJoueur == -1)
                    throw new IOException();

                scores.add(new Record(pseudoJoueur, scoreJoueur));
            }

            scores.sort(comparator);

        } catch (IOException ioException) {
            connexion.ferme();
            afficherErreur();
        }

        new Thread(receveur).start();
    }

    /**
     * Actualise la partie. Le contrôleur dicte également à la
     * classe dessinable quoi dessiner.
     * @param deltaTemps    L'intervalle de temps depuis la dernière
     *                      actualisation.
     */
    @Override
    public void actualiser(double deltaTemps) {
        dessinable.viderPlan();

        if(erreurConnexion) {

            deltaMessage += deltaTemps;
            if(deltaMessage < TEMPS_MESSAGE) {
                dessinable.dessinerErreurConnexionMultijoueur();
            } else {
                dessinable.partieTermine(partie.getScore());
            }


        }else if(augmenteNiveau) { // si le message Level X est en affichage.

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
                receveur.setPartieEnCours(false);
                connexion.ferme();
                dessinable.partieTermine(partie.getScore());
            }

        } else {

            //On actualise la partie et on l'affiche.
            planJeu.actualiser(deltaTemps);

            dessinerBulles();
            dessinerPoisson();
            dessinerProjectiles();
            dessinerInformations();

            synchronized (cadenasDonneesAffichage) {

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

                } else if (connexionEnCours) {

                    deltaMessage += deltaTemps;
                    if(deltaMessage >= TEMPS_MESSAGE_AUTRE) {
                        deltaMessage = 0;
                        connexionEnCours = false;
                    }

                    dessinable.dessinerMessageMultijoueur(nomConnexion + " vient de se connecter à la partie.");

                }else if(scores.size() > 0) {

                    deltaScores += deltaTemps;
                    if (deltaScores >= TEMPS_MESSAGE_SCORES) {
                        deltaScores = 0;
                        indexScores++;

                        if(indexScores >= scores.size())
                            indexScores = 0;
                    }

                    dessinable.dessinerMessageMultijoueur((indexScores + 1) + ". " + scores.get(indexScores));

                }

            }
        }
    }

    /**
     * Envoie au serveur un signal signifiant que le joueur "attaque" les autres joueurs avec un poisson normal.
     */
    public void attaquePoissonNormal() {
        synchronized (cadenasDonneesAffichage) {
            lancementAttaque = true;
            attaqueSpeciale = false;
        }

        new Thread(() -> {
            output.write(ATTAQUE_POISSON_NORMAL_ENVOIE);
            output.flush();
        }).start();
    }

    /**
     * Envoie au serveur um signal signifiant que le joueur "attque" les autres joueurs avec un poisson spécial.
     */
    public void attaquePoissonSpecial() {
        synchronized (cadenasDonneesAffichage) {
            lancementAttaque = true;
            attaqueSpeciale = true;
        }

        new Thread(() -> {
            output.write(ATTAQUE_POISSON_SPECIAL_ENVOIE);
            output.flush();
        }).start();
    }

    /**
     * Envoie un signal au serveur signifiant que le score du joueur a changé.
     */
    public void miseAJourScore() {
        int scoreAEnvoyer = partie.getScore();

        new Thread(() -> {
            output.write(MISE_A_JOUR_SCORE_ENVOIE);
            output.write(scoreAEnvoyer);
            output.flush();
        }).start();
    }

    /**
     * Signal au contrôleur de la partie que le joueur se fait "attaquer" par un autre joueur avec un poisson normal.
     * @param pseudoAttaquant   Le pseudo de l'attaquant.
     */
    public void attaquePoissonNormal(String pseudoAttaquant) {
        synchronized (cadenasDonneesAffichage) {
            attaqueEnCours = true;
            attaqueSpeciale = false;
            nomAttaquant = pseudoAttaquant;
            deltaMessage = 0;
        }

        Platform.runLater(() -> planJeu.ajouterPoissonNormal());
    }

    /**
     * Signal au contrôleur de la partie que le joueur se fait "attaquer" par un autre joueur avec un poisson spécial.
     * @param pseudoAttaquant   Le pseudo de l'attaquant.
     */
    public void attaquePoissonSpecial(String pseudoAttaquant) {
        synchronized (cadenasDonneesAffichage) {
            attaqueEnCours = true;
            attaqueSpeciale = true;
            nomAttaquant = pseudoAttaquant;
            deltaMessage = 0;
        }

        Platform.runLater(() -> planJeu.ajouterPoissonSpecial());
    }

    /**
     * Signal au contrôleur de la partie que le score d'un joueur a changé.
     * @param pseudo    Le joueur pour lequel le score a changé.
     * @param score     Le nouveau score du joueur.
     */
    public void miseAJourScore(String pseudo, int score) {
        synchronized (cadenasDonneesAffichage) {
            for(Record nScore : scores) {
                if(nScore.getNom().equals(pseudo)) {
                    nScore.setScore(score);
                    return;
                }
            }
            scores.sort(comparator);
        }
    }

    /**
     * Signal au contrôleur de la partie qu'un joueur vient de se déconnecter.
     * @param pseudo    Le pseudo du joueur qui vient de se déconnecter.
     */
    public void deconnexionJoueur(String pseudo) {
        synchronized (cadenasDonneesAffichage) {
            deconnexionEnCours = true;
            nomDeconnexion = pseudo;
            deltaMessage = 0;
            Record scoreARetirer = null;
            for(Record score : scores) {
                if(score.getNom().equals(pseudo)) {
                    scoreARetirer = score;
                    break;
                }
            }
            scores.remove(scoreARetirer);
            indexScores = 0;
            scores.sort(comparator);
        }
    }

    /**
     * Signal au contrôleur de la partie qu'un joueur vient de se connecter.
     * @param pseudo    Le pseudo du joueur qui vient de se connecter.
     */
    public void connexionJoueur(String pseudo) {
        synchronized (cadenasDonneesAffichage) {
            connexionEnCours = true;
            nomConnexion = pseudo;
            deltaMessage = 0;
            scores.add(new Record(pseudo, 0));
            indexScores = 0;
            scores.sort(comparator);
        }
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
            erreurConnexion = true;
            deltaMessage = 0;
            receveur.setPartieEnCours(false);
            connexion.ferme();
        });
    }
}
