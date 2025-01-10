package help;



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
    public void start(Stage stage) throws IOException 
    {
        // Correctly load 'login.fxml' without duplicating the path
        Parent root = loadFXML("OrganizeMovie"); // Pass only the FXML name without path
        scene = new Scene(root, 600, 400); // Adjust size as needed
        stage.setScene(scene);
        //stage.setFullScreen(true);
        stage.setTitle("Login");
        stage.setFullScreenExitHint(""); // Turn off the "Press ESC to exit fullscreen" text
        stage.show();
    }

    static void setRoot(String fxml) throws IOException 
    {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        // Ensure the path is correct and matches the FXML location
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/help/fxml/" + fxml + ".fxml"));
        if (fxmlLoader.getLocation() == null) {
            throw new IllegalStateException("Cannot find FXML file: /help/fxml/" + fxml + ".fxml");
        }
        return fxmlLoader.load();
    }

    public static void main(String[] args) 
    {
        launch();
    }

}
