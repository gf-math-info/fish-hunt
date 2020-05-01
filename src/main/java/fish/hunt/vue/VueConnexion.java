package fish.hunt.vue;

import fish.hunt.controleur.multijoueur.ConnexionServeur;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;

public class VueConnexion extends VBox {

    private ConnexionServeur connexion;
    private boolean connecte;

    private final int PSEUDO_ACCEPTE = 110;

    private Stage stagePrincipal;
    private Text informationsText;
    private TextField pseudoTextField;
    private Button validerButton, menuButton;
    private ProgressIndicator progressIndicator;

    public VueConnexion(Stage stagePrincipal) {
        this.stagePrincipal = stagePrincipal;
        setBackground(new Background(
                new BackgroundFill(Color.rgb(0, 0, 139),
                        CornerRadii.EMPTY, Insets.EMPTY)));
        setAlignment(Pos.CENTER);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefWidth(25);

        informationsText = new Text("Connexion au serveur...");
        informationsText.setTextAlignment(TextAlignment.CENTER);
        informationsText.setFont(Font.font(18));
        informationsText.setFill(Color.WHITE);

        Text demandeText = new Text("Veuillez entrer votre pseudonyme." +
                System.lineSeparator() + "Il doit contenir au moins un " +
                System.lineSeparator() +
                "caractère et un maximum de 10 caractères.");
        demandeText.setTextAlignment(TextAlignment.CENTER);
        demandeText.setFill(Color.WHITE);
        demandeText.setFont(Font.font(14));

        pseudoTextField = new TextField("Votre pseudonyme");
        pseudoTextField.selectAll();
        pseudoTextField.setMaxWidth(200);

        HBox buttonHBox = new HBox();
        buttonHBox.setAlignment(Pos.TOP_CENTER);

        validerButton = new Button("Valider");
        validerButton.setDisable(true);

        menuButton = new Button("Menu");
        menuButton.prefWidthProperty().bind(validerButton.widthProperty());

        buttonHBox.getChildren().addAll(validerButton, menuButton);

        Insets margin = new Insets(10);
        VBox.setMargin(informationsText, margin);
        VBox.setMargin(demandeText, margin);
        VBox.setMargin(pseudoTextField, margin);
        HBox.setMargin(validerButton, margin);
        HBox.setMargin(menuButton, margin);

        getChildren().addAll(informationsText, progressIndicator, demandeText,
                pseudoTextField, buttonHBox);

        initListener();

        new Thread(() -> {

            try {
                connexion = ConnexionServeur.getInstance();
                Platform.runLater(() -> {
                    informationsText.setText("Serveur connecté.");
                    connecte = true;
                });
            } catch (IOException ioException) {
                Platform.runLater(() -> {
                    informationsText.setText("Erreur de connexion.");
                });
            }
            
            Platform.runLater(() -> getChildren().remove(progressIndicator));

        }).start();
    }
    
    private void initListener() {
        validerButton.setOnAction((event) -> {

            new Thread(() -> {

                Platform.runLater(() -> {
                    //On signifie à l'utilisateur qu'on communique avec le
                    //serveur.
                    getChildren().add(1, progressIndicator);
                    informationsText.setText("Vérification du pseudo...");
                });

                try {

                    //Envoie du pseudo au serveur et attente de sa réponse.
                    connexion.ecrireString(pseudoTextField.getText());
                    int reponse = connexion.lireInt();

                    //On communique la réponse du server à l'utilisateur.
                    if(reponse == PSEUDO_ACCEPTE) {

                        Platform.runLater(() -> {
                            informationsText.setText("Pseudo accepté.");
                        });

                        //On laisse le temps à l'utilisateur de voir le message.
                        Thread.sleep(2000);

                        for(int i = 5; i > 0; i--) {
                            int nbSec = i;
                            Platform.runLater(() -> {
                                informationsText.setText("La partie débute dans " + nbSec + " seconde" +
                                        ((nbSec > 1)?"s...":"..."));
                            });
                            Thread.sleep(1000);
                        }

                        Platform.runLater(() -> {
                            stagePrincipal.getScene().setRoot(new VueJeu(stagePrincipal, true));
                        });

                    } else {

                        Platform.runLater(() -> {
                            informationsText.setText("Pseudo refusé." + System.lineSeparator() +
                                    "Choisissez un autre pseudo. Il se peut qu'il soit déjà choisi.");
                        });

                    }

                } catch (IOException | InterruptedException ioException) {
                    connecte = false;
                    //On communique qu'il y a un erreur de connexion.
                    Platform.runLater(() -> {
                        informationsText.setText("Erreur de connexion.");
                    });
                }

                Platform.runLater(() -> getChildren().remove(progressIndicator));
            }).start();
        });

        menuButton.setOnAction((event) -> {
            if(connexion != null)
                connexion.ferme();
            stagePrincipal.getScene().setRoot(new VueAccueil(stagePrincipal));
        });

        pseudoTextField.setOnKeyReleased((event) -> {
            validerButton.setDisable(pseudoTextField.getText().strip().length() == 0 ||
                    pseudoTextField.getText().length() > 10 || !connecte);
        });
    }
}
