package help;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main Application class for the Cinema Center Management System.
 * This class serves as the entry point for the JavaFX application and handles
 * the primary stage initialization.
 * 
 * Key Features:
 * <ul>
 *   <li>Product Inventory Management</li>
 *   <li>Personnel Management</li>
 *   <li>Price Management</li>
 *   <li>Revenue and Tax Management</li>
 * </ul>
 * 
 * Navigation Controls:
 * <ul>
 *   <li>SignoutButton - Returns to login screen</li>
 *   <li>MinimizeButton - Minimizes application window</li>
 *   <li>CloseButton - Exits application</li>
 * </ul>
 * 
 * 
 * @version 1.0
 */
public class App extends Application 
{

    private static Scene scene;

    /**
     * Starts the application by loading the initial FXML file and setting up the primary stage.
     * 
     * @param stage the primary stage for this application
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException 
    {
        // Correctly load 'login.fxml' without duplicating the path
        Parent root = loadFXML("login"); // Pass only the FXML name without path
        scene = new Scene(root, 600, 400); // Adjust size as needed
        stage.setScene(scene);
        //stage.setFullScreen(true);
        stage.setTitle("Login");
        stage.setFullScreenExitHint(""); // Turn off the "Press ESC to exit fullscreen" text
        stage.show();
    }

    /**
     * Changes the root of the current scene to a new FXML layout.
     * 
     * @param fxml the name of the FXML file to be loaded as the new root
     * @throws IOException if the FXML file cannot be loaded
     */
    static void setRoot(String fxml) throws IOException 
    {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Loads an FXML file from the specified path.
     * 
     * @param fxml the name of the FXML file (without extension) located in the /help/fxml/ directory
     * @return the loaded Parent node for the specified FXML file
     * @throws IOException if the FXML file cannot be loaded
     * @throws IllegalStateException if the FXML file cannot be found
     */
    private static Parent loadFXML(String fxml) throws IOException {
        // Ensure the path is correct and matches the FXML location
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/help/fxml/" + fxml + ".fxml"));
        if (fxmlLoader.getLocation() == null) {
            throw new IllegalStateException("Cannot find FXML file: /help/fxml/" + fxml + ".fxml");
        }
        return fxmlLoader.load();
    }

    /**
     * The main entry point for the JavaFX application.
     * 
     * @param args the command-line arguments
     */
    public static void main(String[] args) 
    {
        launch();
    }

}
