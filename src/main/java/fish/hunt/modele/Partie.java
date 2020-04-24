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
    private int nbViesRestantes;

    private final int NB_VIES_INIT = 3;
    private final int NB_POISSONS_NIVEAU = 5;

    /**
     * Contruit une partie.
     */
    public Partie() {
        this.score = 0;
        this.niveau = 1;
        this.etat = PartieEtat.EN_COURS;
        this.nbViesRestantes = NB_VIES_INIT;
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
        if(++score % NB_POISSONS_NIVEAU == 0)
            incrementerNiveau();
    }

    /**
     * Incrémente le nombre de vie.
     */
    public void incrementerVie() {
        if(nbViesRestantes < 3)
            this.nbViesRestantes++;
    }

    /**
     * Décrémente le nombre de vie.
     */
    public void decrementerVie() {
        if(--nbViesRestantes == 0)
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

    /**
     * Accesseur du nombres de vies restantes.
     * @return  Le nombre de vies restantes.
     */
    public int getNbViesRestantes() {
        return nbViesRestantes;
    }
}
