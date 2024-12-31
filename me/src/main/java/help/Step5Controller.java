package help;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Step5Controller {

    @FXML
    private Button next_button_step5;
    @FXML
    private Button back_button_step5;

    @FXML
    private void initialize() {
        // Initialization code here
    }

    @FXML
    private void handleNextButtonAction() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/final.fxml"));
        Stage stage = (Stage) next_button_step5.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true); // Ensure full screen
        stage.show();
    }

    @FXML
    private void handleBackButtonAction() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/step4.fxml"));
        Stage stage = (Stage) back_button_step5.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true); // Ensure full screen
        stage.show();
    }
}