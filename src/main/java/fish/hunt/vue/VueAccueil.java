package fish.hunt.vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
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
    Button partieButton, scoreButton, multiButton;
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
        multiButton = new Button("Multijoueurs");

        partieButton.setOnAction(event -> {
            stagePrincipal.getScene().setRoot(new VueJeu(stagePrincipal));
        });

        scoreButton.setOnAction(event -> {
            stagePrincipal.getScene().setRoot(new VueScore(stagePrincipal));
        });

        multiButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog(
                    "pseudonyme"
            );
            dialog.setResizable(true);
            dialog.getDialogPane().setPrefHeight(200);
            dialog.showAndWait();
        });

        partieButton.setDefaultButton(true);

        //Ajout des marges.
        Insets margin = new Insets(7);
        VBox.setMargin(logo, margin);
        VBox.setMargin(partieButton, margin);
        VBox.setMargin(scoreButton, margin);
        VBox.setMargin(multiButton, margin);

        getChildren().addAll(
                logo,
                partieButton,
                scoreButton,
                multiButton
        );
        setAlignment(Pos.TOP_CENTER);
    }
}
