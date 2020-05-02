package fish.hunt.controleur.multijoueur;

import fish.hunt.controleur.ControleurPartie;
import fish.hunt.modele.Partie;
import fish.hunt.modele.PartieMulti;
import fish.hunt.modele.Record;
import fish.hunt.vue.Dessinable;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class ControleurPartieMulti extends ControleurPartie {

    private final int ATTAQUE_POISSON_NORMAL_ENVOIE = 50;
    private final int ATTAQUE_POISSON_SPECIAL_ENVOIE = 51;
    private final int MISE_A_JOUR_SCORE_ENVOIE = 60;

    private final int TEMPS_MESSAGE_AUTRE = 1;
    private final int TEMPS_MESSAGE_SCORES = 1;

    private final Object cadenasDonneesAffichage = new Object();
    private Set<Record> scores;
    private ConnexionServeur connexion;
    private PrintWriter output;
    private Receveur receveur;

    /*
    Variables utilisées pour l'affichage des messages en mode multijoueur.
    */
    private boolean attaqueEnCours, deconnexionEnCours, connexionEnCours, attaqueSpeciale, lancementAttaque;
    private double deltaMessage;

    //Variables pour afficher le score des joueurs.
    private int indexScores;
    private Record scoreAffiche;
    private Iterator<Record> itScores;
    private double deltaScores;

    private String nomAttaquant, nomDeconnexion, nomConnexion;
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

            //Il y a au moins un joueur de connecté : nous.
            itScores = scores.iterator();
            scoreAffiche = itScores.next();

        } catch (IOException ioException) {
            connexion.ferme();
            afficherErreur();
        }

        new Thread(receveur).start();
    }

    @Override
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
    }

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

    public void miseAJourScore() {
        int scoreAEnvoyer = partie.getScore();

        new Thread(() -> {
            output.write(MISE_A_JOUR_SCORE_ENVOIE);
            output.write(scoreAEnvoyer);
            output.flush();
        }).start();
    }

    public void attaquePoissonNormal(String pseudoAttaquant) {
        synchronized (cadenasDonneesAffichage) {
            attaqueEnCours = true;
            attaqueSpeciale = false;
            nomAttaquant = pseudoAttaquant;
            deltaMessage = 0;
        }

        Platform.runLater(() -> planJeu.ajouterPoissonNormal());
    }

    public void attaquePoissonSpecial(String pseudoAttaquant) {
        synchronized (cadenasDonneesAffichage) {
            attaqueEnCours = true;
            attaqueSpeciale = true;
            nomAttaquant = pseudoAttaquant;
            deltaMessage = 0;
        }

        Platform.runLater(() -> planJeu.ajouterPoissonSpecial());
    }

    public void miseAJourScore(String pseudo, int score) {
        synchronized (cadenasDonneesAffichage) {
            for(Record nScore : scores) {
                if(nScore.getNom().equals(pseudo)) {
                    nScore.setScore(score);
                    return;
                }
            }
        }
    }

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
        }
    }

    public void connexionJoueur(String pseudo) {
        synchronized (cadenasDonneesAffichage) {
            connexionEnCours = true;
            nomConnexion = pseudo;
            deltaMessage = 0;
            scores.add(new Record(pseudo, 0));
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
            receveur.setPartieEnCours(false);
            erreurConnexionAlert.showAndWait();
            dessinable.partieTermine(partie.getScore());
        });
    }
}
