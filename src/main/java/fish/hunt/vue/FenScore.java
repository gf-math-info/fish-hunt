package fish.hunt.vue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Comparator;

public class FenScore extends VBox {

    private ListView<String> listView;
    private ObservableList<String> scores;
    private Comparator<String> comparatorScores;
    private Button menuButton;

    public FenScore(Stage stagePrincipal) {
        setAlignment(Pos.CENTER);

        Text titre = new Text("Meilleurs Scores");
        titre.setFont(Font.font(30));

        comparatorScores = new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                int score1 = Integer.parseInt(s.split(" - ")[1]);
                int score2 = Integer.parseInt(t1.split(" - ")[1]);
                return score2 - score1;
            }
        };

        initScores();
        scores.sort(comparatorScores);
        for(int i = 1; i <= scores.size(); i++)
            scores.set(i, "#" + i + " - " + scores.get(i));
        listView = new ListView<>(scores);

        menuButton = new Button("Menu");
        menuButton.setOnAction((event) -> {
            stagePrincipal.getScene().setRoot(new Accueil(stagePrincipal));
        });
        VBox.setMargin(menuButton, new Insets(15));

        getChildren().addAll(titre, listView, menuButton);
    }

    public FenScore(Stage stagePrincipal, int score) {
        this(stagePrincipal);

        if(scores.size() == 10 &&
                Integer.parseInt(scores.get(9).split(" - ")[2]) < score) {

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);

            Text nomText = new Text("Votre nom :");
            TextField nomTextField = new TextField();
            Text pointText = new Text("<strong>a fait "
                    + score + " points!</strong>");
            Button ajouterButton = new Button("Ajouter!");

            hBox.getChildren().addAll(nomText, nomTextField, pointText,
                    ajouterButton);

            ajouterButton.setOnAction((event) -> {

            });

            getChildren().add(2, hBox);
        }
    }

    private void initScores() {
        boolean charge = false;
        scores = FXCollections.observableArrayList();

        while (!charge) {

            try (FileInputStream fileInputStream =
                         new FileInputStream("/scores.dat");
                 ObjectInputStream objectInput =
                         new ObjectInputStream(fileInputStream)){

                scores = (ObservableList<String>) objectInput.readObject();
                charge = true;

            } catch (FileNotFoundException fileNotFoundException) {

                scores = FXCollections.observableArrayList();
                charge = true;

            } catch (ClassNotFoundException classNotFoundException) {

                Alert alertModale = new Alert(Alert.AlertType.ERROR,
                        "Le fichier contenant les meilleurs scores " +
                                "semblent corrompus." + System.lineSeparator() +
                                "Voulez-vous essayer de le recharger?" +
                                System.lineSeparator() + "Si vous refusez, " +
                                "un nouveau fichier de sauvegarde sera créé.",
                        ButtonType.YES, ButtonType.NO);
                alertModale.showAndWait();
                ButtonType reponse = alertModale.getResult();
                if(reponse != ButtonType.YES) {
                    scores = FXCollections.observableArrayList();
                    charge = true;
                }

            } catch (IOException ioException) {

                Alert alertModale = new Alert(Alert.AlertType.ERROR,
                        "Une erreur s'est produit lors du chargement" +
                                " du fichier contenant les meilleurs scores." +
                                System.lineSeparator() + "Voulez-vous " +
                                "essayer de le recharger?",
                        ButtonType.YES, ButtonType.NO);
                alertModale.showAndWait();
                ButtonType reponse = alertModale.getResult();
                if(reponse != ButtonType.YES) {
                    scores = FXCollections.observableArrayList();
                    charge = true;
                }

            }

        }
    }
}
