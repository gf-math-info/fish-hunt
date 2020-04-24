package fish.hunt.vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Cette classe représente la page principale contenant le menu.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class VueAccueil extends VBox {

    Stage stagePrincipal;
    Button partieButton, scoreButton;
    ImageView logo;

    /**
     * Construit la page principale avec le stage principal de l'application.
     * @param stagePrincipal    Le stage principal de l'application.
     */
    public VueAccueil(Stage stagePrincipal) {
        this.stagePrincipal = stagePrincipal;
        setBackground(new Background(
                new BackgroundFill(Color.rgb(0, 0, 139),
                        CornerRadii.EMPTY, Insets.EMPTY)));

        //Initialisation du logo.
        logo = new ImageView("/images/logo.png");
        logo.setSmooth(true);
        logo.setFitHeight(300);

        //Initialisation des boutons.
        partieButton = new Button("Nouvelle Partie!");
        scoreButton = new Button("Meilleurs Scores");
        partieButton.setOnAction(event -> {
            stagePrincipal.getScene().setRoot(new VueJeu(stagePrincipal));
        });
        scoreButton.setOnAction(event -> {
            stagePrincipal.getScene().setRoot(new VueScore(stagePrincipal));
        });
        partieButton.setDefaultButton(true);

        //Ajout des marges.
        Insets margin = new Insets(12);
        VBox.setMargin(logo, margin);
        VBox.setMargin(partieButton, margin);
        VBox.setMargin(scoreButton, margin);

        getChildren().addAll(logo, partieButton, scoreButton);
        setAlignment(Pos.TOP_CENTER);
    }
}
