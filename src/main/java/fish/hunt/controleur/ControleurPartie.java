package fish.hunt.controleur;

import fish.hunt.modele.Partie;
import fish.hunt.modele.PartieEtat;
import fish.hunt.modele.PlanJeu;
import fish.hunt.modele.entite.Projectile;
import fish.hunt.modele.entite.poisson.Crabe;
import fish.hunt.modele.entite.poisson.EtoileMer;
import fish.hunt.modele.entite.poisson.Poisson;
import fish.hunt.vue.Dessinable;

import java.util.WeakHashMap;

public class ControleurPartie {

    private Partie partie;
    private PlanJeu planJeu;
    private Dessinable dessinable;

    private WeakHashMap<Poisson, Integer> poissonsCouleurs;
    private WeakHashMap<Poisson, Integer> poissonsImages;
    private boolean augmenteNiveau, partiePerdue;
    private double deltaMessage;
    private int dernierNiveau;
    private final double TEMPS_MESSAGE = 3;

    public ControleurPartie(double largeur, double hauteur,
                            Dessinable dessinable) {
        this.dessinable = dessinable;
        partie = new Partie();
        planJeu = new PlanJeu(largeur, hauteur, partie);
        augmenteNiveau = true;
        dernierNiveau = partie.getNiveau();
        poissonsImages = new WeakHashMap<>();
        poissonsCouleurs = new WeakHashMap<>();
    }

    public void actualiser(double deltaTemps) {
        dessinable.viderPlan();
        if(augmenteNiveau) {

            deltaMessage += deltaTemps;
            if(deltaMessage < TEMPS_MESSAGE)
                dessinable.afficherNouveauNiveau(partie.getNiveau());
            else {
                augmenteNiveau = false;
                deltaMessage = 0;
            }

        } else if(partiePerdue){

            deltaMessage += deltaTemps;
            if(deltaMessage < TEMPS_MESSAGE)
                dessinable.afficheFinPartie();
            else {
                dessinable.partieTermine(partie.getScore());
            }

        } else {

            //On actualise la partie et on l'affiche.
            planJeu.actualiser(deltaTemps);
            planJeu.getBulles().forEach(bulle ->
                    dessinable.dessinerBulle(bulle.getX(), bulle.getY(),
                            bulle.getDiametre()));
            planJeu.getPoissons().stream()
                    .filter(poisson -> !poissonsCouleurs.containsKey(poisson) &&
                            !(poisson instanceof EtoileMer) &&
                            !(poisson instanceof Crabe))
                    .forEach(poisson -> {
                        poissonsImages.put(poisson,
                                (int)Math.floor(Math.random() *
                                        dessinable.getNombreImagesPoissons()));
                        poissonsCouleurs.put(poisson,
                                (int)Math.floor(Math.random() *
                                        dessinable.getNombreCouleurPoisson()));
                    });
            planJeu.getPoissons().forEach(poisson -> {
                if(poisson instanceof EtoileMer) {
                    dessinable.dessinerEtoileMer(poisson.getX(), poisson.getY(),
                            poisson.getLargeur(), poisson.getHauteur());
                } else if(poisson instanceof  Crabe) {
                    dessinable.dessinerCrabe(poisson.getX(), poisson.getY(),
                            poisson.getLargeur(), poisson.getHauteur());
                } else {
                    dessinable.dessinerPoisson(poisson.getX(), poisson.getY(),
                            poisson.getLargeur(), poisson.getHauteur(),
                            poisson.getVx() > 0,
                            poissonsImages.get(poisson),
                            poissonsCouleurs.get(poisson));
                }
            });
            planJeu.getProjectiles().forEach(projectile ->
                    dessinable.dessinerProjectile(
                            projectile.getX(), projectile.getY(),
                            projectile.getDiametre()));
            dessinable.dessinerScore(partie.getScore(),
                    partie.getNbViesRestantes());

            partiePerdue = partie.getEtat() == PartieEtat.PERDU;

            if(partie.getNiveau() != dernierNiveau) {
                dernierNiveau = partie.getNiveau();
                augmenteNiveau = true;
            }
        }
    }

    public void ajouterProjectile(double x, double y) {
        if(!augmenteNiveau && !partiePerdue)
            planJeu.getProjectiles().add(new Projectile(x, y));
    }

    public void incrementerNiveau() {
        partie.incrementerNiveau();
    }

    public void incrementerScore() {
        partie.incrementerScore();
    }

    public void incrementerPoissonRestant() {
        partie.incrementerVie();
    }

    public void partiePerdue() {
        partie.setEtat(PartieEtat.PERDU);
    }
}
