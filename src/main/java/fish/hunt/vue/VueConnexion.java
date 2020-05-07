package fish.hunt.vue;

import fish.hunt.controleur.multijoueur.ConnexionServeur;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Cette classe représente la page de connexion avant de lancer une partie en mode multijoueur. Elle permet à
 * l'utilisateur de se connecter au serveur.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class VueConnexion extends VBox {

    private ConnexionServeur connexion;

    private final int PSEUDO_ACCEPTE = 110;

    private Stage stagePrincipal;
    private Text informationsText;
    private TextField pseudoTextField, adresseTextField, portTextField;
    private Button validerButton, menuButton, connexionButton;
    private ProgressIndicator progressIndicator;
    private HBox connexionHBox;

    /**
     * Construit la vue de la page de connexion avec le stage principal de l'application.
     * @param stagePrincipal    Le stage principal de l'application.
     */
    public VueConnexion(Stage stagePrincipal) {
        this.stagePrincipal = stagePrincipal;
        setBackground(new Background(
                new BackgroundFill(Color.rgb(0, 0, 139),
                        CornerRadii.EMPTY, Insets.EMPTY)));
        setAlignment(Pos.CENTER);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefWidth(25);

        informationsText = new Text();
        informationsText.setTextAlignment(TextAlignment.CENTER);
        informationsText.setFont(Font.font(18));
        informationsText.setFill(Color.WHITE);

        //Création de la section avec le bouton de connexion.
        connexionHBox = new HBox();
        connexionHBox.setAlignment(Pos.CENTER);
        Text adresseText = new Text("Adresse :");
        adresseText.setFill(Color.WHITE);
        adresseTextField = new TextField(ConnexionServeur.getAdresse());
        Separator separator1 = new Separator(Orientation.VERTICAL);
        Text portText = new Text("Port :");
        portText.setFill(Color.WHITE);
        portTextField = new TextField(String.valueOf(ConnexionServeur.getPort()));
        Separator separator2 = new Separator(Orientation.VERTICAL);
        connexionButton = new Button("Connexion");
        connexionHBox.getChildren().addAll(adresseText, adresseTextField, separator1,
                portText, portTextField, separator2, connexionButton);

        //Création de la section avec le champ de texte pour entrer le pseudo.
        Text demandeText = new Text("Veuillez entrer votre pseudonyme." +
                System.lineSeparator() + "Il doit contenir au moins un " +
                System.lineSeparator() +
                "caractère et un maximum de 10 caractères.");
        demandeText.setTextAlignment(TextAlignment.CENTER);
        demandeText.setFill(Color.WHITE);
        demandeText.setFont(Font.font(14));

        pseudoTextField = new TextField("Votre pseudonyme");
        pseudoTextField.setMaxWidth(200);
        pseudoTextField.setDisable(true);

        HBox buttonHBox = new HBox();
        buttonHBox.setAlignment(Pos.TOP_CENTER);

        validerButton = new Button("Valider");
        validerButton.setDisable(true);
        validerButton.setDefaultButton(true);

        menuButton = new Button("Menu");
        menuButton.prefWidthProperty().bind(validerButton.widthProperty());

        buttonHBox.getChildren().addAll(validerButton, menuButton);

        //Ajout de margins.
        Insets margin = new Insets(10);
        VBox.setMargin(informationsText, margin);
        VBox.setMargin(demandeText, margin);
        VBox.setMargin(pseudoTextField, margin);
        HBox.setMargin(validerButton, margin);
        HBox.setMargin(menuButton, margin);
        HBox.setMargin(separator1, margin);
        HBox.setMargin(separator2, margin);

        getChildren().addAll(informationsText, connexionHBox, demandeText,
                pseudoTextField, buttonHBox);

        initListener();
    }

    /**
     * Initialise les écouteurs.
     */
    private void initListener() {
        connexionButton.setOnAction((event) -> {
            connexionButton.setDisable(true);

            String adresse = adresseTextField.getText();
            int port;
            try {
                port = Integer.parseInt(portTextField.getText());
                portTextField.setBorder(Border.EMPTY);
            }catch(NumberFormatException e) {

                portTextField.setBorder(new Border(
                        new BorderStroke(Color.RED, BorderStrokeStyle.SOLID,
                                CornerRadii.EMPTY, new BorderWidths(1.5))));
                connexionButton.setDisable(false);
                return;

            }

            informationsText.setText("Connexion en cours...");
            getChildren().add(1, progressIndicator);
            ConnexionServeur.setAdresse(adresse);
            ConnexionServeur.setPort(port);

            new Thread(() -> {

                try {

                    connexion = ConnexionServeur.getInstance();
                    Platform.runLater(() -> {
                        informationsText.setText("Connecté.");
                        getChildren().remove(progressIndicator);
                        pseudoTextField.setDisable(false);
                    });

                } catch (IOException ioException) {

                    Platform.runLater(() -> {
                        informationsText.setText("Erreur de connexion.");
                        getChildren().remove(progressIndicator);
                        connexionButton.setDisable(false);
                    });

                }

            }).start();
        });

        validerButton.setOnAction((event) -> {
            //On signifie à l'utilisateur qu'on communique avec le
            //serveur.
            validerButton.setDisable(true);
            menuButton.setDisable(true);
            getChildren().add(1, progressIndicator);
            informationsText.setText("Vérification du pseudo...");

            String pseudo = pseudoTextField.getText();

            new Thread(() -> {

                try {

                    //Envoie du pseudo au serveur et attente de sa réponse.
                    connexion.getOutput().println(pseudo);
                    connexion.getOutput().flush();
                    int reponse = connexion.getInput().read();
                    if(reponse == -1)
                        throw new IOException();

                    //On communique la réponse du server à l'utilisateur.
                    if(reponse == PSEUDO_ACCEPTE) {

                        Platform.runLater(() -> {
                            informationsText.setText("Pseudo accepté.");
                        });

                        //On laisse le temps à l'utilisateur de voir le message.
                        Thread.sleep(2000);

                        for(int i = 3; i > 0; i--) {
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
                    //On communique qu'il y a un erreur de connexion.
                    connexion.ferme();
                    Platform.runLater(() -> {
                        pseudoTextField.setDisable(true);
                        connexionButton.setDisable(false);
                        informationsText.setText("Erreur de connexion.");
                    });
                }

                Platform.runLater(() -> {
                    getChildren().remove(progressIndicator);
                    menuButton.setDisable(false);
                });
            }).start();
        });

        menuButton.setOnAction((event) -> {
            if(connexion != null)
                connexion.ferme();
            stagePrincipal.getScene().setRoot(new VueAccueil(stagePrincipal));
        });

        pseudoTextField.setOnKeyReleased((event) -> {
            validerButton.setDisable(pseudoTextField.getText().strip().length() == 0 ||
                    pseudoTextField.getText().length() > 10);
        });
    }
}
