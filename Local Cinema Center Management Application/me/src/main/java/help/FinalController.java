package help;

import java.io.IOException;

import help.classes.ShoppingCart;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for handling the final stage of the application.
 */
public class FinalController {

    @FXML
    private Button back_button_final;

    @FXML private Button CloseButton;
    @FXML private Button MinimizeButton; 

    @FXML
    private Label thankYouLabel;

    /**
     * Handles the action of clicking the close button.
     * Closes the current window.
     * 
     * @param event The action event triggered by clicking the close button.
     */
    @FXML
    private void handleCloseButtonAction(ActionEvent event) 
    {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the action of clicking the minimize button.
     * Minimizes the current window.
     * 
     * @param event The action event triggered by clicking the minimize button.
     */
    @FXML
    private void handleMinimizeButtonAction(ActionEvent event) 
    {
        Stage stage = (Stage) MinimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialization code here
    }

    /**
     * Handles the action of clicking the back button.
     * Clears the shopping cart and navigates to the step1 view.
     * 
     * @throws IOException If an error occurs while loading the FXML file.
     */
    @FXML
    private void handleBackButtonAction() throws IOException 
    {

        ShoppingCart.getInstance().clear();
        Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/step1.fxml"));
        Stage stage = (Stage) back_button_final.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true); // Ensure full screen
        stage.show();
    }
}