package fish.hunt.modele;

import fish.hunt.modele.entite.Bulle;
import fish.hunt.modele.entite.Projectile;
import fish.hunt.modele.entite.poisson.Crabe;
import fish.hunt.modele.entite.poisson.EtoileMer;
import fish.hunt.modele.entite.poisson.Poisson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Cette classe représente le plan du jeu. Elle contient tous les éléments du
 * jeu.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class PlanJeu {

    private double largeur, hauteur;

    private Partie partie;
    private ArrayList<Bulle> bulles;
    private ArrayList<Projectile> projectiles;
    private ArrayList<Poisson> poissons;

    private Random random;
    private double deltaBulle, deltaPoisson, deltaPoissonSpecial;

    // ici, nous réunissons toutes les constantes décrivant chaque poisson
    // afin de faciliter la coordination des modifications au jeu.
    private final double DELAIS_BULLE = 3;
    private final int NB_GROUPES_BULLES = 3;
    private final double DIST_GROUPE_BULLE = 20;
    private final int NB_BULLES = 5;
    private final double BULLE_RAYON_MIN = 10;
    private final double BULLE_RAYON_MAX = 40;
    private final double BULLE_VITESSE_MIN = -350;
    private final double BULLE_VITESSE_MAX = -450;
    private final double DELAIS_POISSON = 3;
    private final double DELAIS_POISSON_SPECIAL = 3;
    private final double POISSON_VITESSE_MIN = -100; //TODO : mettre dans classe
    private final double POISSON_VITESSE_MAX = -200; //TODO : mettre dans classe
    private final double POISSON_GRANDEUR_MIN = 70;  //TODO : mettre dans classe
    private final double POISSON_GRANDEUR_MAX = 100; //TODO : mettre dans classe
    private final double POISSON_Y_MAX_RATIO = 4/5.0; //TODO : mettre dans classe
    private final double POISSON_Y_MIN_RATIO = 1/5.0; //TODO : mettre dans classe

    //Un crabe est plus large que haut.
    private final double RATIO_CRABE_HAUTEUR_LARGEUR = 367/477.0; //TODO : mettre dans classe
    private final double VITESSE_CRABE = 1.3; //TODO : mettre dans classe

    /**
     * Construit un plan de jeu selon certaines dimensions et une partie.
     * @param largeur   La largeur du plan de jeu.
     * @param hauteur   La hauteur du plan de jeu.
     * @param partie    La partie de jeu.
     */
    public PlanJeu(double largeur, double hauteur, Partie partie) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.partie = partie;

        random = new Random();
        deltaBulle = 0;
        deltaPoisson = 0;
        deltaPoissonSpecial = 0;

        bulles = new ArrayList<>();
        projectiles = new ArrayList<>();
        poissons = new ArrayList<>();
    }

    /**
     * Actualise tous les éléments dans le jeu.
     * @param deltaTemps    L'intervalle de temps depuis la dernière
     *                      actualisation.
     */
    public void actualiser(double deltaTemps) {
        //On actualise la position des objects du plan de jeu.
        bulles.forEach(bulle -> bulle.actualiser(deltaTemps));
        projectiles.forEach(projectile -> projectile.actualiser(deltaTemps));
        poissons.forEach(poisson -> poisson.actualiser(deltaTemps));

        //On met à jour les contacts des projectiles avec les poissons.
        Projectile projectile;
        for(int i = 0; i < projectiles.size();) {
            projectile = projectiles.get(i);

            //Si un projectile est au niveau des poissons, alors...
            if(projectile.getLargeur() == 0) {
                List<Poisson> poissonsTouches = new LinkedList<>();

                poissons.stream()
                        .filter(projectile::intersect)
                        .forEach(poisson -> {
                            partie.incrementerScore();
                            poissonsTouches.add(poisson);
                        });

                poissons.removeAll(poissonsTouches);
                projectiles.remove(projectile);
            } else
                i++;
        }

        //On retire les bulles à l'extérieur du plan et on rajoute de nouvelles
        //bulles lorsque le moment est venu.
        deltaBulle += deltaTemps;
        bulles.removeIf(bulle -> bulle.getY() + bulle.getHauteur() > 0);
        if(deltaBulle >= DELAIS_BULLE) {
            deltaBulle = 0;
            ajouterBulles();
        }

        //On retire les poissons qui ont passé à travers le plan de jeu sans
        //se faire toucher.
        Poisson poisson;
        for(int i = 0; i < poissons.size();) {

            poisson = poissons.get(i);
            if((poisson.getVx() < 0 &&
                    poisson.getX() + poisson.getLargeur() < 0) ||
                    (poisson.getVx() > 0 && poisson.getX() > largeur)) {
                poissons.remove(poisson);
                partie.decrementerVie();
            } else
                i++;

        }

        //On ajoute les poissons après un certain délais.
        deltaPoisson += deltaTemps;
        if(deltaPoisson >= DELAIS_POISSON) {
            deltaPoisson = 0;
            ajouterPoissonNormal();
        }

        //On ajoute les poissons spécials après un certain délais.
        if(partie.getNiveau() > 1) {
            deltaPoissonSpecial += deltaTemps;
            if(deltaPoissonSpecial >= DELAIS_POISSON_SPECIAL) {
                deltaPoissonSpecial = 0;
                ajouterPoissonSpecial();
            }
        }
    }

    /**
     * Accesseur des bulles.
     * @return  La liste de bulle que le plan de jeu contient.
     */
    public ArrayList<Bulle> getBulles() {
        return bulles;
    }

    /**
     * Accesseur des projectiles.
     * @return  La liste de projectiles que le plan de jeu contient.
     */
    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    /**
     * Accesseur de poissons que le plan de jeu contient.
     * @return
     */
    public ArrayList<Poisson> getPoissons() {
        return poissons;
    }

    /**
     * Ajoute une bulle dans le plan jeu. Les attributs de la bulles sont
     * choisis aléatoirement.
     */
    private void ajouterBulles() {
        double posGroupe, posBulle, diametreBulle, vitesseBulle;
        for(int i = 0; i < NB_GROUPES_BULLES; i++) {

            posGroupe = random.nextDouble() * largeur;

            for(int j = 0; j < NB_BULLES; j++) {

                posBulle = random.nextDouble() * 2 * DIST_GROUPE_BULLE -
                        DIST_GROUPE_BULLE + posGroupe;
                diametreBulle = random.nextDouble() *
                        (BULLE_RAYON_MAX - BULLE_RAYON_MIN) +
                        BULLE_RAYON_MIN;
                vitesseBulle = random.nextDouble() *
                        (BULLE_VITESSE_MAX - BULLE_VITESSE_MIN) +
                        BULLE_VITESSE_MIN;
                bulles.add(new Bulle(diametreBulle,
                        posBulle, hauteur + diametreBulle, vitesseBulle));

            }
        }
    }

    /**
     * Ajoute un poisson normal au plan de jeu. Les attributs du poisson sont
     * choisis alatoirement.
     */
    private void ajouterPoissonNormal() {
        boolean versDroite = random.nextBoolean();
        double grandeur = random.nextDouble() *
                (POISSON_GRANDEUR_MAX - POISSON_GRANDEUR_MIN) +
                POISSON_GRANDEUR_MIN;
        double vy = random.nextDouble() *
                (POISSON_VITESSE_MAX - POISSON_VITESSE_MIN) +
                POISSON_VITESSE_MIN;
        double vx = vitesseLevel(partie.getNiveau());
        double y = random.nextDouble() *
                (POISSON_Y_MAX_RATIO - POISSON_Y_MIN_RATIO) *
                (hauteur - grandeur) + POISSON_Y_MIN_RATIO * hauteur;
        double x;
        if(!versDroite) {
            vx = -vx;
            x = largeur;
        } else
            x = -grandeur;

        poissons.add(new Poisson(grandeur, grandeur, x, y, vx, vy));
    }

    /**
     * Ajoute un poisson spécial au plan de jeu. Les attributs du poisson sont
     * choisis alatoirement.
     */
    private void ajouterPoissonSpecial() {
        boolean versDroite = random.nextBoolean();
        double largeurPoisson = random.nextDouble() *
                (POISSON_GRANDEUR_MAX - POISSON_GRANDEUR_MIN) +
                POISSON_GRANDEUR_MIN;
        double x;
        if(!versDroite)
            x = largeur;
        else
            x = - largeurPoisson;

        if(random.nextBoolean()) {//Un crabe...

            double hauteurPoisson = RATIO_CRABE_HAUTEUR_LARGEUR *
                    largeurPoisson;
            double y = random.nextDouble() *
                    (POISSON_Y_MAX_RATIO - POISSON_Y_MIN_RATIO) *
                    (hauteur - hauteurPoisson) + POISSON_Y_MIN_RATIO * hauteur;
            double vx = VITESSE_CRABE * vitesseLevel(partie.getNiveau());
            if(!versDroite)
                vx = -vx;
            Crabe crabe = new Crabe(largeurPoisson, hauteurPoisson, x, y, vx);
            poissons.add(crabe);

        } else {//... ou une étoile de mer ...

            double y = random.nextDouble() *
                    (POISSON_Y_MAX_RATIO - POISSON_Y_MIN_RATIO) *
                    (hauteur - largeurPoisson) + POISSON_Y_MIN_RATIO * hauteur;
            double vx = 100 * Math.pow(partie.getNiveau(), 1/3.0) + 200;
            if(!versDroite)
                vx = -vx;
            poissons.add(
                    new EtoileMer(largeurPoisson, largeurPoisson, x, y, vx));

        }
    }

    private double vitesseLevel(int niveau) {
        return 100 * Math.pow(niveau, 1/3.0) + 200;
    }
}
