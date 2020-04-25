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
    private int nbPoissonsTouches;
    private PartieEtat etat;
    private int nbViesRestantes;
    private int unProjectileUnMort;

    private final int NB_VIES_INIT = 3;
    private final int NB_POISSONS_NIVEAU = 5;

    /**
     * Contruit une partie.
     */
    public Partie() {
        this.score = 0;
        this.niveau = 1;
        this.unProjectileUnMort = 0;
        this.nbViesRestantes = 0;
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
     * Incremente le nombre de poissons touchés.
     */
    public void incrementerNbPoissonsTouches() {
        if(++nbPoissonsTouches % NB_POISSONS_NIVEAU == 0)
            incrementerNiveau();
    }

    /**
     * Incrémente le score de la partie.
     */
    public void incrementerScore() {
        score++;
        incrementerNbPoissonsTouches();
    }

    /**
     * Incrémente le nombre de vies.
     */
    public void incrementerVie() {
        this.nbViesRestantes++;
    }

    /**
     * Décrémente le nombre de vies.
     */
    public void decrementerVie() {
        if(--nbViesRestantes == 0)
            etat = PartieEtat.PERDU;
    }

    /**
     * Signifie à la partie que le joueur a fait un autre un-projectile-un-mort.
     */
    public void incrementerUnProjectileUnMort() {
        switch (++unProjectileUnMort) {
            case 1:
                score += 3;
                break;
            case 2:
                score += 5;
                break;
            case 3:
                nbViesRestantes++;
                break;
            default:
                incrementerScore();
                initUnProjectileUnMort();
        }
        incrementerNbPoissonsTouches();
    }

    public void initUnProjectileUnMort() {
        unProjectileUnMort = 0;
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
