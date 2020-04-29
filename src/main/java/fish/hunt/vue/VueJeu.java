package fish.hunt.vue;

import fish.hunt.controleur.ControleurPartie;
import fish.hunt.controleur.ControleurPartieMulti;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.Socket;
import java.util.HashMap;

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

    private Image poissonScoreImage, cibleImage, etoileImage, crabeImage;
    private Image[] poissonImages;
    private Color fondColor, scoreColor, msgColor, bulleColor,
            projectileColor;
    private Color[] poissonCouleurs;
    private Font msgFont, scoreFont, msgMultiFont;
    private HashMap<Integer, Image> idImages;

    /**
     * Construit la fenêtre de jeu avec le stage principal de l'application.
     * @param stagePrincipal    Le stage principal de l'application.
     */

    public VueJeu(Stage stagePrincipal) {
        this(stagePrincipal, null);
    }

    public VueJeu(Stage stagePrincipal, Socket client) {
        this.stagePrincipal = stagePrincipal;
        largeur = stagePrincipal.getWidth();
        hauteur = stagePrincipal.getHeight();

        canvas = new Canvas(stagePrincipal.getWidth(),
                stagePrincipal.getHeight());
        graphicsContext = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        if(client == null)
            controleurPartie = new ControleurPartie(largeur, hauteur,
                    this);
        else
            controleurPartie = new ControleurPartieMulti(largeur, hauteur,
                    this, client);

        initOutilsDessin();
        initListeners();

        timer = new AnimationTimer() {
            long dernierMoment = System.nanoTime();
            final double MAX_DELTA_TEMPS = 0.003;

            @Override
            public void handle(long l) {
                controleurPartie.actualiser(
                        Math.max((l - dernierMoment) * 1e-9, MAX_DELTA_TEMPS));
                dessinerCible(cibleX, cibleY);
                dernierMoment = l;
            }
        };

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

        canvas.setOnMouseMoved(
                (event) -> setCiblePosition(event.getX(), event.getY())
        );

        canvas.setOnMouseDragged(
                (event) -> setCiblePosition(event.getX(), event.getY())
        );

        canvas.setOnMousePressed(
                (event) -> controleurPartie
                        .ajouterProjectile(event.getX(), event.getY())
        );
    }

    /**
     * Initialise les couleurs, les fonts et charge les images.
     */
    private void initOutilsDessin() {
        poissonImages = new Image[8];

        for(int i = 0; i < poissonImages.length; i++)
            poissonImages[i] = new Image("/images/fish/0" + i + ".png");

        poissonScoreImage = poissonImages[0];

        idImages = new HashMap<>();
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
     * Dessine un poisson à une certaine position et d'une certaine dimension.
     * @param x         La position horizontale.
     * @param y         La position verticale.
     * @param largeur   La largeur.
     * @param hauteur   La hauteur.
     */
    @Override
    public void dessinerPoisson(double x, double y,
                                double largeur, double hauteur,
                                boolean versDroite,
                                int numImage, int numCouleur) {
        Image img = poissonImages[numImage];

        img = ImageHelpers.colorize(img, poissonCouleurs[numCouleur]);
        if(!versDroite)
            img = ImageHelpers.flop(img);

        graphicsContext.drawImage(img, x, y, largeur, hauteur);
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
     * Accesseur du nombre d'images de poissons disponibles.
     * @return  Le nombre d'images de poissons disponibles.
     */
    @Override
    public int getNombreImagesPoissons() {
        return poissonImages.length;
    }

    /**
     * Accesseur du nombre de couleurs disponibles pour les différents poissons.
     * @return  Le nombre de couleurs disponibles pour les différents poissons.
     */
    @Override
    public int getNombreCouleurPoisson() {
        return poissonCouleurs.length;
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
