package fish.hunt.vue;

import fish.hunt.controleur.ControleurPartie;
import fish.hunt.controleur.multijoueur.ControleurPartieMulti;
import fish.hunt.modele.entite.poisson.Poisson;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Cette classe représente la fenêtre de jeu. Elle est dessinable par le
 * controleur de la partie.
 * @see fish.hunt.vue.Dessinable
 * @see ControleurPartie
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class VueJeu extends Pane implements Dessinable{

    private Stage stagePrincipal;
    private Canvas canvas;
    private GraphicsContext graphicsContext;

    private double largeur, hauteur, cibleX, cibleY;

    private ControleurPartie controleurPartie;
    private AnimationTimer timer;
    private Thread constructeurImagesAleatoiresDroites, constructeurImagesAleatoiresGauches;

    private Image poissonScoreImage, cibleImage, etoileImage, crabeImage;
    private Color fondColor, scoreColor, msgColor, bulleColor,
            projectileColor;
    private Font msgFont, scoreFont, msgMultiFont;

    private Object cadenasTabImgsCouleurs;
    private Image[] poissonImages;
    private Color[] poissonCouleurs;
    private ArrayBlockingQueue<Image> poissonsAleatoiresImagesDroites, poissonsAleatoiresImagesGauches;
    private WeakHashMap<Poisson, Image> couplesPoissonsImages;

    /**
     * Construit la fenêtre de jeu avec le stage principal de l'application.
     * @param stagePrincipal    Le stage principal de l'application.
     */
    public VueJeu(Stage stagePrincipal) {
        this(stagePrincipal, false);
    }

    /**
     * Construit une fenêtre de jeu avec le stage principal et un drapeau qui est vrai si la partie est en mode
     * multijoueur.
     * @param stagePrincipal    Le stage principal de l'application.
     * @param multijoueur       Vrai, si la partie est en mode multijoueur, faux, sinon.
     */
    public VueJeu(Stage stagePrincipal, boolean multijoueur) {
        this.stagePrincipal = stagePrincipal;
        largeur = stagePrincipal.getWidth();
        hauteur = stagePrincipal.getHeight();

        canvas = new Canvas(stagePrincipal.getWidth(),
                stagePrincipal.getHeight());
        graphicsContext = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        if(multijoueur)
            controleurPartie = new ControleurPartieMulti(largeur, hauteur, this);
        else
            controleurPartie = new ControleurPartie(largeur, hauteur, this);

        initOutilsDessin();
        initListeners();

        timer = new AnimationTimer() {
            long dernierMoment = System.nanoTime();

            @Override
            public void handle(long l) {
                controleurPartie.actualiser((l - dernierMoment) * 1e-9);
                dessinerCible(cibleX, cibleY);
                dernierMoment = l;
            }
        };
        constructeurImagesAleatoiresDroites.start();
        constructeurImagesAleatoiresGauches.start();
        timer.start();
    }

    /**
     * Initialise les écouteurs pour le jeu.
     */
    private void initListeners() {
        stagePrincipal.getScene().setOnKeyPressed((event) -> {
            switch(event.getCode()) {

                case H:
                    controleurPartie.incrementerNiveau();
                    break;

                case J:
                    controleurPartie.incrementerScore();
                    break;

                case K:
                    controleurPartie.incrementerPoissonRestant();
                    break;

                case L:
                    controleurPartie.partiePerdue();
                    break;

            }
        });

        canvas.setOnMouseMoved((event) -> {
            setCiblePosition(event.getX(), event.getY());
        });

        canvas.setOnMouseDragged((event) -> {
            setCiblePosition(event.getX(), event.getY());
        });

        canvas.setOnMousePressed((event) -> {
            controleurPartie.ajouterProjectile(event.getX(), event.getY());
        });
    }

    /**
     * Initialise les couleurs, les fonts et charge les images.
     */
    private void initOutilsDessin() {
        poissonImages = new Image[8];

        for(int i = 0; i < poissonImages.length; i++)
            poissonImages[i] = new Image("/images/fish/0" + i + ".png");

        poissonScoreImage = poissonImages[0];

        cibleImage = new Image("/images/cible.png");
        crabeImage = new Image("/images/crabe.png");
        etoileImage = new Image("/images/star.png");

        fondColor = Color.rgb(49, 13, 166);
        scoreColor = Color.WHITE;
        msgColor = Color.WHITE;
        bulleColor = Color.rgb(100, 100, 255, 0.4);
        projectileColor = Color.BLACK;

        poissonCouleurs = new Color[]{
                Color.BEIGE, Color.CYAN, Color.FLORALWHITE, Color.LIGHTGREEN,
                Color.SALMON, Color.GREENYELLOW, Color.GREY, Color.LIGHTSALMON,
                Color.LIGHTPINK, Color.LIGHTSTEELBLUE, Color.PINK,
                Color.LIMEGREEN, Color.ORANGE, Color.WHITESMOKE};

        cadenasTabImgsCouleurs = new Object();
        couplesPoissonsImages = new WeakHashMap<>();
        poissonsAleatoiresImagesDroites = new ArrayBlockingQueue<>(2);
        poissonsAleatoiresImagesGauches = new ArrayBlockingQueue<>(2);

        /*
        Ce thread s'assure qu'il y a toujours des images de poissons, regardant vers la droite, qui ont été choisies
        aléatoirement et qui ont été coloriées avec une couleur choisie aléatoirement, dans la file
        poissonsAleatoiresImagesDroites.
         */
        constructeurImagesAleatoiresDroites = new Thread(() -> {

            Random random = new Random();

            while(true) {

                Image poissonImage = null;
                synchronized (cadenasTabImgsCouleurs) {
                    poissonImage = poissonImages[random.nextInt(poissonImages.length)];
                    poissonImage = ImageHelpers.colorize(poissonImage,
                            poissonCouleurs[random.nextInt(poissonCouleurs.length)]);
                }

                try {
                    poissonsAleatoiresImagesDroites.put(poissonImage);
                } catch (InterruptedException e) {}

            }

        });

        //Idem que constructeurImagesAleatoiresDroites, mais pour les images de poissons regardant vers la gauche.
        constructeurImagesAleatoiresGauches = new Thread(() -> {
            Random random = new Random();

            while(true) {

                Image poissonImage = null;
                synchronized (cadenasTabImgsCouleurs) {
                    poissonImage = poissonImages[random.nextInt(poissonImages.length)];
                    poissonImage = ImageHelpers.colorize(poissonImage,
                            poissonCouleurs[random.nextInt(poissonCouleurs.length)]);
                }
                poissonImage = ImageHelpers.flop(poissonImage);
                try {
                    poissonsAleatoiresImagesGauches.put(poissonImage);
                } catch (InterruptedException e) {}

            }
        });

        msgFont = Font.font(60);
        scoreFont = Font.font(25);
        msgMultiFont = Font.font(14);
    }

    /**
     * Vide le plan de jeu. Seul le fond marin reste.
     */
    @Override
    public void viderPlan() {
        graphicsContext.clearRect(0, 0, largeur, hauteur);
        graphicsContext.setFill(fondColor);
        graphicsContext.fillRect(0, 0, largeur, hauteur);
    }

    /**
     * Affiche au joueur un message lui indiquant que la partie est terminée.
     */
    @Override
    public void afficheFinPartie() {
        graphicsContext.setFill(msgColor);
        graphicsContext.setFont(msgFont);

        String msg = "Game Over";
        Text text = new Text(msg);
        text.setFont(msgFont);

        double x = (largeur - text.getLayoutBounds().getWidth()) / 2,
                y = (hauteur - text.getLayoutBounds().getHeight()) / 2;

        graphicsContext.fillText(msg, x, y);
    }

    /**
     * Affiche un message avec le niveau.
     * @param niveau    Le niveau du jeu.
     */
    @Override
    public void afficherNouveauNiveau(int niveau) {
        graphicsContext.setFill(msgColor);
        graphicsContext.setFont(msgFont);

        String msg = "Level " + niveau;
        Text text = new Text(msg);
        text.setFont(msgFont);

        graphicsContext.fillText(msg,
                (largeur - text.getLayoutBounds().getWidth()) / 2,
                (hauteur - text.getLayoutBounds().getHeight()) / 2);
    }

    /**
     * Gère la partie terminée avec le score final.
     * @param score Le score final de la partie.
     */
    @Override
    public void partieTermine(int score) {
        timer.stop();
        constructeurImagesAleatoiresDroites.interrupt();
        constructeurImagesAleatoiresGauches.interrupt();
        stagePrincipal.getScene().setRoot(new VueScore(stagePrincipal, score));
    }

    /**
     * Dessine le score et le nombre de poissons qui restent au joueur.
     * @param score                 Le score de la partie.
     * @param nbPoissonsRestants    Le nombre de poissons restants du joueur.
     */
    @Override
    public void dessinerScore(int score, int nbPoissonsRestants) {
        graphicsContext.setFill(scoreColor);
        graphicsContext.setFont(scoreFont);
        double dimPoisson = 35, insets = 15, xVie, yVie, yScore = 30;

        String nombre = String.valueOf(score);
        Text text = new Text(nombre);
        text.setFont(scoreFont);

        graphicsContext.fillText(String.valueOf(score),
                (largeur - text.getLayoutBounds().getWidth()) / 2, yScore);

        yVie = 40 + text.getLayoutBounds().getHeight();
        if(nbPoissonsRestants > 4) {
            String msg = nbPoissonsRestants + " \u2715 ";
            text.setText(msg);
            xVie = (largeur - text.getLayoutBounds().getWidth() -
                    dimPoisson) / 2;

            graphicsContext.fillText(msg, xVie, yVie +
                    (dimPoisson + text.getLayoutBounds().getHeight()) / 2);
            graphicsContext.drawImage(poissonScoreImage,
                    xVie + text.getLayoutBounds().getWidth(), yVie,
                    dimPoisson, dimPoisson);
        } else {
            xVie = (largeur - (dimPoisson * nbPoissonsRestants +
                    ((nbPoissonsRestants > 0) ?
                            insets * (nbPoissonsRestants - 1) :
                            0))) / 2;

            for (int i = 0; i < nbPoissonsRestants; i++)
                graphicsContext.drawImage(poissonScoreImage,
                        xVie + i * (dimPoisson + insets), yVie,
                        dimPoisson, dimPoisson);
        }
    }

    /**
     * Dessine une bulle d'une certaine dimension à une certaine position.
     * @param x             La position horizontale.
     * @param y             La position verticale.
     * @param diametre      Le diamètre de la bulle.
     */
    @Override
    public void dessinerBulle(double x, double y, double diametre) {
        graphicsContext.setFill(bulleColor);
        graphicsContext.fillOval(x, y, diametre, diametre);
    }

    /**
     * Dessine le poisson. Dans ce cas particulier, pour une augmentation considérable de la performance, on passe le
     * poisson en paramètre à la vue. C'est le seul endroit que la vue est en contact avec le modèle.
     * @param poisson   Le poisson à dessiner.
     */
    @Override
    public void dessinerPoisson(Poisson poisson) {

        if(!couplesPoissonsImages.containsKey(poisson)) {
            Image poissonImage = null;
            try {

                if(poisson.getVx() > 0)
                    poissonImage = poissonsAleatoiresImagesDroites.take();
                else
                    poissonImage = poissonsAleatoiresImagesGauches.take();

            } catch (InterruptedException e) {}
            couplesPoissonsImages.put(poisson, poissonImage);
        }

        graphicsContext.drawImage(couplesPoissonsImages.get(poisson), poisson.getX(), poisson.getY(),
                poisson.getLargeur(), poisson.getHauteur());

    }

    /**
     * Dessine une étoile de mer qui a une certaine position et une certaine
     * dimension.
     * @param x         La position horizontale.
     * @param y         La position verticale.
     * @param largeur   La largeur.
     * @param hauteur   La hauteur.
     */
    @Override
    public void dessinerEtoileMer(double x, double y,
                                  double largeur, double hauteur) {
        graphicsContext.drawImage(etoileImage, x, y, largeur, hauteur);
    }

    /**
     * Dessine un crabe qui a une certaine position et une certaine dimension.
     * @param x         La position horizontale.
     * @param y         La position verticale.
     * @param largeur   La largeur.
     * @param hauteur   La hauteur.
     */
    @Override
    public void dessinerCrabe(double x, double y,
                              double largeur, double hauteur) {
        graphicsContext.drawImage(crabeImage, x, y, largeur, hauteur);
    }

    /**
     * Dessine un projectile qui a une certaine position et un certain diamètre.
     * @param x         La position horizontale du projectile.
     * @param y         La position verticale du projectile.
     * @param diametre  Le diamètre du projectile.
     */
    @Override
    public void dessinerProjectile(double x, double y, double diametre) {
        double rayon = diametre / 2;
        graphicsContext.setFill(projectileColor);
        graphicsContext.fillOval(x - rayon, y - rayon, diametre, diametre);
    }

    /**
     * Dessine un message d'information en mode multijoueur.
     * @param message   Un message d'information.
     */
    @Override
    public void dessinerMessageMultijoueur(String message) {
        double espace = 10;
        Text texte = new Text(message);
        texte.setFont(msgMultiFont);

        graphicsContext.setFont(msgMultiFont);
        graphicsContext.setFill(msgColor);
        graphicsContext.fillText(message, espace,
                hauteur - texte.getLayoutBounds().getHeight() - 25,
                largeur - espace);
    }

    /**
     * Dessine un message signifiant aux utilisateurs que la connexion est rompue.
     */
    @Override
    public void dessinerErreurConnexionMultijoueur() {
        Font font = Font.font(18);
        graphicsContext.setFill(msgColor);
        graphicsContext.setFont(font);

        String msg = "Une erreur de connexion vient de se produire.";
        Text text = new Text(msg);
        text.setFont(font);

        graphicsContext.fillText(msg,
                (largeur - text.getLayoutBounds().getWidth()) / 2,
                (hauteur - text.getLayoutBounds().getHeight()) / 2);
    }

    /**
     * Change la position de la souris.
     * @param x La position horizontale.
     * @param y La position verticale.
     */
    private void setCiblePosition(double x, double y) {
        cibleX = x;
        cibleY = y;
    }

    /**
     * Dessine une cible à une certaine position.
     * @param x La position horizontale.
     * @param y La position verticale.
     */
    public void dessinerCible(double x, double y) {
        double dimCible = 50;
        graphicsContext.drawImage(cibleImage,
                x - dimCible / 2, y - dimCible / 2, dimCible, dimCible);
    }

    /**
     * Dessine le nombre de tire un-projectile-un-mort.
     * @param nbUnProjectileUnMort  Le nombre de tire un-projectile-un-mort.
     */
    @Override
    public void dessinerCombo(int nbUnProjectileUnMort) {
        graphicsContext.setFill(msgColor);
        graphicsContext.setFont(scoreFont);

        String msg = "Tir parfait \u2715 " + nbUnProjectileUnMort;
        Text text = new Text(msg);
        text.setFont(scoreFont);

        graphicsContext.fillText(msg,
                largeur - text.getLayoutBounds().getWidth() - 20, 30);
    }
}
