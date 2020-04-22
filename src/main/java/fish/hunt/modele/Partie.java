package fish.hunt.modele;

public class Partie {
    private int score;
    private int niveau;
    private PartieEtat etat;
    private int nbPoissonRestant; // nbViesRestantes serait p ê mieux?
                                  // sinon, ajouter attribut nbViesRestantes
    private PlanJeu planJeu;
    private int NB_POISSON_INIT = 3;
    private int NB_POISSON_AUG_NIV = 5;

    public void Partie() {
        this.score = 0;
        this.niveau = 1;
        this.etat = PartieEtat.EN_COURS;
        this.nbPoissonRestant = NB_POISSON_INIT;
        this.planJeu = new PlanJeu(640, 480);
    }

    public void incrementerNiveau() {
        this.niveau++;
    }

    public void incrementerScore() {
        this.score++;
    }

    public void incrementerNbPoisRest() {
        this.nbPoissonRestant += NB_POISSON_AUG_NIV;
    }

    public void perdrePartie() {
        this.etat = PartieEtat.PERDU;
        // TODO: afficher "Game Over" pendant 3 secondes
        // TODO: passer ensuite à la fenêtre des meilleurs scores
        // TODO: sauvegarder le score
    }

    public void actualiser() {
        // TODO: gérer le déplacement de chaque poisson, vitesse, acceleration
    }
}
