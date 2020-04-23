package fish.hunt.modele;

/**
 * La partie en cours. La partie et le plan de jeu sont deux entités distinctes.
 * @see PlanJeu
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class Partie {
    private int score;
    private int niveau;
    private PartieEtat etat;
    private int nbViesRestants;

    private final int NB_VIES_INIT = 3;

    /**
     * Contruit une partie.
     */
    public void Partie() {
        this.score = 0;
        this.niveau = 1;
        this.etat = PartieEtat.EN_PAUSE;
        this.nbViesRestants = NB_VIES_INIT;
    }

    /**
     * Incrémente le niveau de la partie.
     */
    public void incrementerNiveau() {
        this.niveau++;
    }

    /**
     * Incrémente le score de la partie.
     */
    public void incrementerScore() {
        this.score++;
    }

    /**
     * Incrémente le nombre de vie.
     */
    public void incrementerVie() {
        this.nbViesRestants++;
    }

    /**
     * Décrémente le nombre de vie.
     */
    public void decrementerVie() {
        if(--nbViesRestants == 0)
            etat = PartieEtat.PERDU;
    }

    /**
     * Mutateur de l'état de la partie.
     * @param etat  L'état de la partie.
     */
    public void setEtat(PartieEtat etat) {
        this.etat = etat;
    }

    /**
     * Accesseur du niveau de la partie.
     * @return  Le niveau de la partie.
     */
    public int getNiveau() {
        return niveau;
    }

    /**
     * Accesseur du score de la partie.
     * @return  Le score de la partie.
     */
    public int getScore() {
        return score;
    }

    /**
     * Accesseur de l'état de la partie.
     * @return  L'état de la partie.
     */
    public PartieEtat getEtat() {
        return etat;
    }
}
