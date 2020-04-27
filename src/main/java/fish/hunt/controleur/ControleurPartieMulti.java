package fish.hunt.controleur;

import fish.hunt.vue.Dessinable;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ControleurPartieMulti extends ControleurPartie{

    private final int ATTAQUE_POISSON_ENVOIE = 50;
    private final int MISE_A_JOUR_SCORE_ENVOIE = 60;
    private final int ATTAQUE_POISSON_RECU = 150;
    private final int MISE_A_JOUR_SCORE_RECU = 160;
    private final int MISE_A_JOUR_RECORD_RECU = 161;

    private Socket client;

    /**
     * Construit un contrôleur de jeu avec la largeur et la hauteur du plan de
     * jeu, ainsi que la classe dessinable.
     *
     * @param largeur    La largeur du plan de jeu.
     * @param hauteur    La hauteur du plan de jeu.
     * @param dessinable La classe dessinable.
     */
    public ControleurPartieMulti(double largeur, double hauteur,
                                 Dessinable dessinable, Socket client) {
        super(largeur, hauteur, dessinable);
        this.client = client;

        new Thread(() -> {

            try (PrintWriter onput = new PrintWriter(
                    client.getOutputStream(), true);
                 BufferedReader input = new BufferedReader(
                         new InputStreamReader(client.getInputStream()))) {

                while(client.isConnected()) {

                    switch (input.read()) {

                        case ATTAQUE_POISSON_RECU:
                            //TODO
                            break;

                        case MISE_A_JOUR_SCORE_RECU:
                            //TODO
                            break;

                        case MISE_A_JOUR_RECORD_RECU:
                            //TODO
                            break;

                    }

                }

            } catch (IOException ioException) {
                Alert erreurModale = new Alert(Alert.AlertType.ERROR,
                        "Une erreur de connexion s'est produite.");
                Platform.runLater(erreurModale::showAndWait);
            }
        }).start();
    }
}
