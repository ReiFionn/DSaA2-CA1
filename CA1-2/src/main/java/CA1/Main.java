package CA1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    public static Stage mainStage;
    public static Scene mainScene, twoToneScene;

    public static Scene newScene(String file) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(file));
        Parent root = fxmlLoader.load();
        return new Scene(root, 1000, 600);
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
        Parent root = fxmlLoader.load();

        mainScene = new Scene(root, 1000, 600);
        twoToneScene = newScene("twoTone.fxml");

        Image icon = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("pill.png")));

        mainStage.setScene(mainScene);
        mainStage.getIcons().add(icon);
        mainStage.setResizable(false);
        mainStage.setTitle("Pill and Capsule Analyzer");
        mainStage.initStyle(StageStyle.UNDECORATED);
        mainStage.show();
    }
}

