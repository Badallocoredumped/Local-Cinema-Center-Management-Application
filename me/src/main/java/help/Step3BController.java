package help;

import java.io.IOException;

import help.classes.SelectedSession;
import help.classes.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Step3BController 
{
    @FXML
    private Button next_button_step3b;
    @FXML
    private Button back_button_step3b;

    @FXML
    private void initialize() 
    {
        // Access the selected session
        Session selectedSession = SelectedSession.getInstance().getSession();
        if (selectedSession != null) 
        {
            System.out.println("Selected Session: " + selectedSession.getDay() + " " + selectedSession.getSession() + " " + selectedSession.getHall());
        }
    }

    @FXML
    private void handleNextButtonAction() throws IOException 
    {
        Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/step4.fxml"));
        Stage stage = (Stage) next_button_step3b.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true); // Ensure full screen
        stage.show();
    }

    @FXML
    private void handleBackButtonAction() throws IOException 
    {
        Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/step2.fxml"));
        Stage stage = (Stage) back_button_step3b.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true); // Ensure full screen
        stage.show();
    }
}