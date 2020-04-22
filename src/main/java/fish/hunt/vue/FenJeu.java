package fish.hunt.vue;

import fish.hunt.controleur.ControleurPartie;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Cette classe représente la fenêtre de jeu. Elle est dessinable par le
 * controleur de la partie.
 * @see fish.hunt.vue.Dessinable
 * @see ControleurPartie
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class FenJeu extends Pane implements Dessinable{

    private Stage stagePrincipal;
    private Canvas canvas;
    private GraphicsContext graphicsContext;

    private double largeur, hauteur;

    private ControleurPartie controleurPartie;

    private Image poissonScoreImage, cibleImage, etoileImage, crabeImage;
    private Image[] poissonImages;
    private Color fondColor, scoreColor, msgColor, bulleCouleur;
    private Color[] poissonCouleurs;
    private Font msgFont, scoreFont;

    /**
     * Construit la fenêtre de jeu avec le stage principal de l'application.
     * @param stagePrincipal    Le stage principal de l'application.
     */
    public FenJeu(Stage stagePrincipal) {
        this.stagePrincipal = stagePrincipal;
        largeur = stagePrincipal.getWidth();
        hauteur = stagePrincipal.getHeight();

        canvas = new Canvas(stagePrincipal.getWidth(),
                stagePrincipal.getHeight());
        graphicsContext = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        controleurPartie = new ControleurPartie(largeur, hauteur);

        initOutilsDessin();
        initListeners();
    }

    private void initListeners() {
        canvas.setOnKeyPressed((event) -> {
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
            dessinerCible(event.getX(), event.getY());
        });

        canvas.setOnMouseClicked((event) -> {
            controleurPartie.ajouterProjectile(event.getX(), event.getY());
        });
    }

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
        bulleCouleur = Color.rgb(0, 0, 255, 0.4);
        poissonCouleurs = new Color[]{
                Color.RED, Color.BEIGE, Color.BROWN, Color.CHOCOLATE,
                Color.CORNSILK, Color.CYAN, Color.FLORALWHITE, Color.GOLDENROD,
                Color.FUCHSIA, Color.SALMON, Color.FIREBRICK, Color.GREENYELLOW,
                Color.DEEPPINK, Color.GREY, Color.INDIGO, Color.LAVENDER,
                Color.LIMEGREEN, Color.MEDIUMORCHID, Color.ORANGE,
                Color.ORANGERED, Color.SLATEBLUE, Color.TOMATO,
                Color.WHITESMOKE};

        msgFont = Font.font(28);
        scoreFont = Font.font(18);
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
     * Affiche un message au joueur lui expliquant que la partie est terminée.
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
        //TODO
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

        String nombre = String.valueOf(score);
        Text text = new Text(nombre);
        text.setFont(scoreFont);

        graphicsContext.fillText(String.valueOf(score),
                (largeur - text.getLayoutBounds().getWidth()) / 2, 50);

        double dimPoisson = 70;
        double x = (largeur - dimPoisson * 3 + 60) / 2,
                y = 50 + text.getLayoutBounds().getHeight() + 20;

        for(int i = 0; i < nbPoissonsRestants; i++)
            graphicsContext.drawImage(poissonScoreImage, x + i, y,
                    dimPoisson, dimPoisson);
    }

    /**
     * Dessine une cible à une certaine position.
     * @param x La position horizontale.
     * @param y La position verticale.
     */
    @Override
    public void dessinerCible(double x, double y) {
        double dimCible = 60;
        graphicsContext.drawImage(cibleImage,
                x - dimCible / 2, y - dimCible / 2);
    }

    /**
     * Dessine une bulle d'une certaine dimension à une certaine position.
     * @param x         La position horizontale.
     * @param y         La position verticale.
     * @param rayon     Le rayon de la bulle.
     */
    @Override
    public void dessinerBulle(double x, double y, double rayon) {
        graphicsContext.setFill(bulleCouleur);
        double dim = rayon * 2;
        graphicsContext.fillRect(x, y, dim, dim);
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
                                boolean versDroite) {
        Color couleur = poissonCouleurs[
                (int)Math.floor(Math.random() * poissonCouleurs.length)];
        int nbImg = (int)Math.floor(Math.random() * 8);
        Image img = poissonImages[nbImg];

        img = ImageHelpers.colorize(img, couleur);
        if(!versDroite)
            img = ImageHelpers.flop(img);

        graphicsContext.drawImage(img, x, y, largeur, hauteur);
    }

    /**
     * Dessine une étoile de mer à une certaine position et d'une certaine
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
     * Dessine un crabe à une certaine position et d'une certaine dimension.
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
}
