package fish.hunt.vue;

import fish.hunt.modele.entite.Record;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;

/**
 * Cette classe représente la fenêtre de score.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class VueScore extends VBox {

    private ListView<Record> listView;
    private ObservableList<Record> scores;
    private Stage stagePrincipal;

    private boolean charge;

    private final String NOM_SAUVEGARDE = "scores.dat";

    /**
     * Construit une fenêtre de score. Ce constructeur permet seulement de
     * consulter les meilleurs scores.
     * @param stagePrincipal    Le stage principal de l'application.
     */
    public VueScore(Stage stagePrincipal) {
        this.stagePrincipal = stagePrincipal;
        initVueBase();

        Platform.runLater(() -> {
            chargerScores();
            FXCollections.sort(scores);
        });
    }

    /**
     * Construit une fenêtre de score. Si la liste de scores contient moins de 10
     * éléments, alors le joueur pourra entrer son score dans le tableau des
     * meilleurs scores. Si la liste de score contient déjà 10 éléments, alors
     * le joueur pourra entrer son score dans le tableau des meilleurs scores
     * seulement si son score est meilleur que le pire des scores du tableau.
     * Dans ce cas, le pire score sera supprimé du tableau.
     * @param stagePrincipal    Le stage principal de l'application.
     * @param score             Le score du joueur.
     */
    public VueScore(Stage stagePrincipal, int score) {
        this.stagePrincipal = stagePrincipal;
        initVueBase();

        Platform.runLater(() -> {
            chargerScores();
            FXCollections.sort(scores);
            //Si le score est assez élevé pour intégrer les 10 meilleurs, alors
            //on offre l'utilisateur d'ajouter son score à la liste.
            if(scores.size() < 10 ||
                    (scores.size() == 10 && scores.get(9).getScore() < score)) {
                initVueAjoutScore(score);
            }
        });
    }

    /**
     * Initialise la vue de base, c'est-à-dire la liste et le bouton pour
     * retourner au menu.
     */
    private void initVueBase() {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(10, 30, 10, 30));
        setSpacing(15);

        Text titre = new Text("Meilleurs Scores");
        titre.setFont(Font.font(30));

        //Initialise la liste.
        scores = FXCollections.observableArrayList();
        listView = new ListView<>();
        listView.setItems(scores);
        //Pour afficher le numéro de ligne...
        listView.setCellFactory(joueurListView -> new ListCell<>() {
            @Override
            protected void updateItem(Record item, boolean empty) {
                super.updateItem(item, empty);
                if(!empty)
                    setText("#" + (getIndex() + 1) + " - " + item);
                else {
                    setText(null);
                    setGraphic(null);
                }
            }
        });
        listView.setPrefHeight(245);

        Button menuButton = new Button("Menu");
        menuButton.setOnAction(event -> {
            stagePrincipal.getScene().setRoot(new VueAccueil(stagePrincipal));
        });

        getChildren().addAll(titre, listView, menuButton);
    }

    /**
     * Initialise la vue qui permet au joueur d'ajouter son score à la liste.
     * @param score Le score que le joueur a fait.
     */
    private void initVueAjoutScore(int score) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(10);

        Text nomText = new Text("Votre nom :");
        TextField nomTextField = new TextField();
        Text pointText = new Text("a fait "
                + score + " points!");
        pointText.setFont(Font.font(Font.getDefault().getFamily(),
                FontWeight.BOLD, Font.getDefault().getSize()));
        Button ajouterButton = new Button("Ajouter!");
        ajouterButton.setDisable(true);
        ajouterButton.setDefaultButton(true);

        nomTextField.setOnKeyPressed((event) -> {
            ajouterButton.setDisable(
                    nomTextField.getText().strip().length() == 0);
        });

        hBox.getChildren().addAll(nomText, nomTextField, pointText,
                ajouterButton);

        ajouterButton.setOnAction((event) -> {
            Record record = new Record(nomTextField.getText(), score);
            if(scores.size() == 10)
                scores.remove(9);
            scores.add(record);
            FXCollections.sort(scores);
            sauvegarderScores();
            getChildren().remove(hBox);
        });

        getChildren().add(2, hBox);
    }


    /**
     * Déséréalise le tableau de score. Si le fichier n'existe pas, alors une
     * nouvelle liste de meilleurs scores sera créé.
     */
    private void chargerScores() {
        Object[] scoreData;

        try (FileInputStream fileInputStream =
                     new FileInputStream(NOM_SAUVEGARDE);
             ObjectInputStream objectInput =
                     new ObjectInputStream(fileInputStream)){

            scoreData = (Object[])objectInput.readObject();
            for(Object score : scoreData)
                scores.add((Record)score);
            charge = true;

        } catch (FileNotFoundException fileNotFoundException) {

            charge = true;

        } catch (IOException | ClassNotFoundException exception) {

            Alert alertModale = new Alert(Alert.AlertType.ERROR,
                    "Une erreur s'est produite lors du chargement" +
                            " du fichier contenant les meilleurs scores." +
                            System.lineSeparator() + "Voulez-vous " +
                            "essayer de le recharger?",
                    ButtonType.YES, ButtonType.NO);
            alertModale.setResizable(true);
            alertModale.getDialogPane().setPrefHeight(200);
            alertModale.showAndWait();
            ButtonType reponse = alertModale.getResult();
            if(reponse != ButtonType.YES) {
                charge = true;
            }
        }
    }

    /**
     * Séréalise la liste des meilleurs scores.
     */
    private void sauvegarderScores() {
        Object[] scoreData = new Object[scores.size()];
        boolean sauvegarde = false;

        for(int i = 0; i < scoreData.length; i++)
            scoreData[i] = scores.get(i);

        while (!sauvegarde) {

            try(FileOutputStream fileOutputStream =
                        new FileOutputStream(NOM_SAUVEGARDE);
                ObjectOutputStream objectOutputStream =
                        new ObjectOutputStream(fileOutputStream)) {

                objectOutputStream.writeObject(scoreData);
                sauvegarde = true;

            }catch (IOException ioException) {

                Alert alertModale = new Alert(Alert.AlertType.ERROR,
                        "Une erreur s'est produite lors de la" +
                                " sauvegarde du fichier contenant les" +
                                " meilleurs scores." +
                                System.lineSeparator() + "Voulez-vous" +
                                " réessayer de sauvegarder votre score?",
                        ButtonType.YES, ButtonType.NO);
                ioException.printStackTrace();
                alertModale.setResizable(true);
                alertModale.getDialogPane().setPrefHeight(200);
                alertModale.showAndWait();
                ButtonType reponse = alertModale.getResult();
                if(reponse != ButtonType.YES) {
                    sauvegarde = true;
                }

            }

        }
    }
}
