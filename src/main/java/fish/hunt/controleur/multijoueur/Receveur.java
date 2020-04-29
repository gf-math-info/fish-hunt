package fish.hunt.controleur.multijoueur;

import fish.hunt.modele.PartieMulti;

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
    private PartieMulti partie;

    /**
     * Construit le Receveur de commande avec le controleur de jeu.
     */
    public Receveur(ControleurPartieMulti controleur) {
        this.controleur = controleur;
    }

    @Override
    public void run() {

    }
}
