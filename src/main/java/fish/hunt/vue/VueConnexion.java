package fish.hunt.vue;

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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class VueConnexion extends VBox {

    private Socket client;

    private final String ADRESSE = "127.0.0.1";
    private final int PORT = 1337;
    private final int PSEUDO_ACCEPTE = 10;
    private final int PSEUDO_REFUSE = 11;

    private Stage stagePrincipal;

    public VueConnexion(Stage stagePrincipal) {
        this.stagePrincipal = stagePrincipal;
        setBackground(new Background(
                new BackgroundFill(Color.rgb(0, 0, 139),
                        CornerRadii.EMPTY, Insets.EMPTY)));
        setAlignment(Pos.CENTER);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefWidth(25);

        Text informationsText = new Text("Connexion au serveur...");
        informationsText.setFont(Font.font(18));
        informationsText.setFill(Color.WHITE);

        Text demandeText = new Text("Veuillez entrer votre pseudonyme." +
                System.lineSeparator() + "Il doit contenir au moins un " +
                System.lineSeparator() +
                "caractère et un maximum de 10 caractère.");
        demandeText.setFill(Color.WHITE);
        demandeText.setFont(Font.font(20));

        TextField pseudonymeTextField = new TextField("Votre pseudonyme");
        pseudonymeTextField.selectAll();
        pseudonymeTextField.setMaxWidth(200);

        HBox buttonHBox = new HBox();
        buttonHBox.setAlignment(Pos.TOP_CENTER);

        Button validerButton = new Button("Valider");
        validerButton.setDisable(true);

        pseudonymeTextField.setOnKeyReleased((event) -> {
            validerButton.setDisable(
                    pseudonymeTextField.getText().strip().length() == 0 ||
                            pseudonymeTextField.getText().length() > 10);
        });

        Button menuButton = new Button("Menu");
        menuButton.setOnAction((event) -> {
            if(client != null && client.isConnected()) {
                try {
                    client.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            stagePrincipal.getScene().setRoot(new VueAccueil(stagePrincipal));
        });
        menuButton.prefWidthProperty().bind(validerButton.widthProperty());

        Insets margin = new Insets(10);
        VBox.setMargin(informationsText, margin);
        VBox.setMargin(demandeText, margin);
        VBox.setMargin(pseudonymeTextField, margin);
        HBox.setMargin(validerButton, margin);
        HBox.setMargin(menuButton, margin);

        buttonHBox.getChildren().addAll(validerButton, menuButton);

        getChildren().addAll(informationsText, progressIndicator, demandeText,
                pseudonymeTextField, buttonHBox);

        new Thread(() -> {

            try {
                client = new Socket(ADRESSE, PORT);
                Platform.runLater(() -> {
                    informationsText.setText("Serveur connecté.");
                });
            } catch (IOException ioException) {
                Platform.runLater(() -> {
                    informationsText.setText("Erreur de connexion.");
                });
                getChildren().remove(progressIndicator);
            }

        }).start();
    }
}
