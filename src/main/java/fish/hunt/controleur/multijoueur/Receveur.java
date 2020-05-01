package fish.hunt.controleur.multijoueur;

import fish.hunt.modele.Partie;
import javafx.application.Platform;

import java.io.IOException;

/**
 * Cette classe reçoit les commandes du serveur et met à jour le controleur de la partie.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class Receveur implements Runnable{

    private final int ATTAQUE_POISSON_NORMAL_RECU = 150;
    private final int ATTAQUE_POISSON_SPECIAL_RECU = 151;
    private final int MISE_A_JOUR_SCORE_RECU = 160;
    private final int DECONNEXION_JOUEUR_RECU = 190;

    private ControleurPartieMulti controleur;
    private Object cadenas;
    private Partie partie;
    private ConnexionServeur connexion;

    private boolean partieEnCours;
    /**
     * Construit le Receveur de commande avec le controleur de jeu.
     */
    public Receveur(ControleurPartieMulti controleur) {
        this.controleur = controleur;
        partie = controleur.getPartie();
        cadenas = controleur.getCadenas();
        partieEnCours = true;

        try {
            connexion = ConnexionServeur.getInstance();
        } catch (IOException exception) {
            controleur.afficherErreur();
        }
    }

    @Override
    public void run() {

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
    }
}
