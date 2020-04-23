package fish.hunt.modele.entite;

import java.io.Serializable;

/**
 * Cette classe est une structure pour garder les données en mémoire.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class Joueur implements Serializable, Comparable<Joueur> {

    String nom;
    int score;

    /**
     * Construit un joueur avec un nom et un score.
     * @param nom   Le nom.
     * @param score Le score.
     */
    public Joueur(String nom, int score) {
        this.nom = nom;
        this.score = score;
    }

    /**
     * Accesseur du nom du joueur.
     * @return  Le nom du joueur.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Accesseur du score du joueur.
     * @return  Le score du joueur.
     */
    public int getScore() {
        return score;
    }

    /**
     * Compare deux joueurs entre eux. On compare les scores entre eux.
     * @param joueur    L'autre joueur.
     * @return          La différence entre les deux joueurs.
     */
    @Override
    public int compareTo(Joueur joueur) {
        return joueur.score - score;
    }

    /**
     * Permet d'afficher la représentation d'un joueur.
     * @return  La chaine de carctère représentant le joueur.
     */
    @Override
    public String toString() {
        return nom + " - " + score;
    }
}
