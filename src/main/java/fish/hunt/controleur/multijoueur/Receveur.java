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
    private Object cadenas, cadenasDrapeau;
    private Partie partie;
    private ConnexionServeur connexion;

    //Drapeau signifiant si le thread doit continuer à écouter le serveur.
    private boolean partieEnCours;

    /**
     * Construit le Receveur de commande avec le controleur de jeu.
     */
    public Receveur(ControleurPartieMulti controleur) {
        this.controleur = controleur;
        partie = controleur.getPartie();
        cadenas = controleur.getCadenas();
        cadenasDrapeau = new Object();
        partieEnCours = true;

        try {
            connexion = ConnexionServeur.getInstance();
        } catch (IOException exception) {
            controleur.afficherErreur();
        }
    }

    /**
     * Accesseur du drapeau signifiant si la partie est toujours en cours.
     * @return  Vrai si la partie est en cours, faux, sinon.
     */
    public boolean estPartieEnCours() {
        boolean retour;
        synchronized (cadenasDrapeau) {
            retour = partieEnCours;
        }
        return retour;
    }

    /**
     * Mutateur du drapeau signifiant si la partie est toujours en cours.
     * @param partieEnCours Vrai si la partie est en cours, faux, sinon.
     */
    public void setPartieEnCours(boolean partieEnCours) {
        synchronized (cadenasDrapeau) {
            this.partieEnCours = partieEnCours;
        }
    }

    /**
     * Méthode redéfini qui s'éxécute lorsque "Thread start".
     */
    @Override
    public void run() {

        int code;

        try {
            while (estPartieEnCours()) {

                code = connexion.lireInt();

                switch (code) {

                    case ATTAQUE_POISSON_NORMAL_RECU:

                        String attaquantNormal = connexion.lireString();
                        if (attaquantNormal == null)
                            throw new IOException();

                        synchronized (cadenas) {
                            controleur.attaquePoissonNormal(attaquantNormal);
                        }

                        break;

                    case ATTAQUE_POISSON_SPECIAL_RECU:

                        String attaquantSpecial = connexion.lireString();
                        if (attaquantSpecial == null)
                            throw new IOException();

                        synchronized (cadenas) {
                            controleur.attaquePoissonSpecial(attaquantSpecial);
                        }

                        break;

                    case MISE_A_JOUR_SCORE_RECU:

                        String nomScore = connexion.lireString();
                        if (nomScore == null)
                            throw new IOException();

                        int score = connexion.lireInt();
                        if (score == -1)
                            throw new IOException();

                        synchronized (cadenas) {
                            controleur.miseAJourScore(nomScore, score);
                        }

                        break;

                    case DECONNEXION_JOUEUR_RECU:
                        String nomJoueurDeconnexion = connexion.lireString();
                        if (nomJoueurDeconnexion == null)
                            throw new IOException();

                        synchronized (cadenas) {
                            controleur.deconnexionJoueur(nomJoueurDeconnexion);
                        }

                        break;
                }
            }

        } catch (IOException ioException) {
            connexion.ferme();
            Platform.runLater(() -> controleur.afficherErreur());
        }

    }
}
