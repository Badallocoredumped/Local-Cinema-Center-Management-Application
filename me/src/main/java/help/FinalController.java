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

public class FinalController {

    @FXML
    private Button back_button_final;

    @FXML private Button CloseButton;
    @FXML private Button MinimizeButton; 

    @FXML
    private Label thankYouLabel;

    @FXML
    private void handleCloseButtonAction(ActionEvent event) 
    {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleMinimizeButtonAction(ActionEvent event) 
    {
        Stage stage = (Stage) MinimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void initialize() {
        // Initialization code here
    }

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