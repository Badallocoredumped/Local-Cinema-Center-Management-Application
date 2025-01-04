package help;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class UntitledController {

    @FXML
    private Button closeButton;

    @FXML
    private Label welcomeLabel;

    @FXML
    private void initialize() {
        // Initialization code here
    }

    @FXML
    private void handleCloseButtonAction() {
        // Handle close button action
        System.out.println("Close button clicked");
    }
}