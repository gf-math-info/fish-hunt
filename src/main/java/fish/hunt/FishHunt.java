package fish.hunt;

import fish.hunt.vue.VueAccueil;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FishHunt extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setWidth(640);
        stage.setHeight(480);
        stage.setResizable(false);
        stage.setTitle("Fish Hunt");
        stage.getIcons().add(new Image("/images/star.png"));

        Scene scene = new Scene(new VueAccueil(stage));
        stage.setScene(scene);
        stage.show();
    }
}
