package me.src.main.java.help;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Load FXML file from the fxml folder
        scene = new Scene(loadFXML("help/fxml/admin"), 640, 480);  // Added the fxml folder to the path
        stage.setScene(scene);
        stage.setFullScreen(true); // Ensure full screen
        stage.setFullScreenExitHint(""); // Turn off the "Press ESC to exit fullscreen" text
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        // Use getResource with the correct path to the fxml folder
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/" + fxml + ".fxml"));  // Added the "/" before the path
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
