package help;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import help.utilities.DataBaseHandler;
import help.classes.Movie;
import help.classes.SelectedMovie;
import help.classes.SelectedSession;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import help.classes.Session;

public class Step2Controller 
{
    @FXML
    private Button next_button_step2;
    @FXML
    private Button back_button_step2;
    @FXML
    private TreeTableView<Session> sessionsTableView;
    @FXML
    private TreeTableColumn<Session, String> dayColumn;
    @FXML
    private TreeTableColumn<Session, String> sessionColumn;
    @FXML
    private TreeTableColumn<Session, String> hallColumn;
    @FXML
    private TreeTableColumn<Session, String> vacantSeatsColumn;
    @FXML
    private ComboBox<String> dayComboBox;
    @FXML
    private ComboBox<String> sessionComboBox;
    @FXML
    private ComboBox<String> hallComboBox;

    @FXML
    private void initialize() 
    {
        // Initialize the TreeTableView columns
        dayColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().dayProperty());
        sessionColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().sessionProperty());
        hallColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().hallProperty());
        vacantSeatsColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().vacantSeatsProperty());

        // Load session data
        loadSessionData();

        next_button_step2.setDisable(true); // Disable the Next button initially

        /*         // Add listeners to the ComboBoxes to enable the Next button when all selections are made
        dayComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkSelections());
        sessionComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkSelections());
        hallComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkSelections()); */
    }

    private void loadSessionData() 
    {
        Movie selectedMovie = SelectedMovie.getInstance().getMovie();
        if (selectedMovie == null) 
        {
            System.out.println("No movie selected");
            return;
        }

        List<Session> sessions = new ArrayList<>();
        try (Connection conn = DataBaseHandler.getConnection()) 
        {
            String query = "SELECT session_date, start_time, hall_name, vacant_seats FROM Sessions WHERE movie_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selectedMovie.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) 
            {
                sessions.add(new Session(
                    rs.getString("session_date"),
                    rs.getString("start_time"),
                    rs.getString("hall_name"),
                    rs.getString("vacant_seats")
                ));
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        TreeItem<Session> root = new TreeItem<>(new Session("Day", "Session", "Hall", "Vacant Seats"));
        for (Session session : sessions) 
        {
            root.getChildren().add(new TreeItem<>(session));
        }
        sessionsTableView.setRoot(root);
        sessionsTableView.setShowRoot(false);
    }

    /* private void checkSelections() {
        if (dayComboBox.getValue() != null && sessionComboBox.getValue() != null && hallComboBox.getValue() != null) {
            next_button_step2.setDisable(false);
        } else {
            next_button_step2.setDisable(true);
        }
    } */

    @FXML
    private void handleNextButtonAction() throws IOException 
    {
        Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/step3_salonA.fxml"));
        Stage stage = (Stage) next_button_step2.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true); // Ensure full screen
        stage.setFullScreenExitHint(""); // Turn off the "Press ESC to exit fullscreen" text
        stage.show();
    }

    @FXML
    private void handleBackButtonAction() throws IOException 
    {
        Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/step1.fxml"));
        Stage stage = (Stage) back_button_step2.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true); // Ensure full screen
        stage.setFullScreenExitHint(""); // Turn off the "Press ESC to exit fullscreen" text
        stage.show();
    }

    @FXML
    private void handleConfirmSelection() throws IOException 
    {
        // Handle the confirmation of the selected session
        TreeItem<Session> selectedSession = sessionsTableView.getSelectionModel().getSelectedItem();
        if (selectedSession != null) 
        {
            // Save the selected session
            SelectedSession.getInstance().setSession(selectedSession.getValue());

            // Show confirmation dialog
            showConfirmationDialog();

            // Proceed to the next stage with the selected session
            navigateToNextStep();
        } 
        else 
        {
            // Show an error dialog or message indicating that no session was selected
            System.out.println("No session selected");
        }
    }

    private void showConfirmationDialog() 
    {
        try 
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/help/fxml/SessionConfirmationDialog.fxml"));
            Parent root = loader.load();

            SessionConfirmationDialogController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Confirmation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(next_button_step2.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private void navigateToNextStep() throws IOException 
    {
        // Access the selected session
        Session selectedSession = SelectedSession.getInstance().getSession();
        if (selectedSession != null) 
        {
            String nextStepFXML = getNextStepFXML(selectedSession);
            System.out.println("Navigating to: " + nextStepFXML); // Log the FXML path
            Parent root = FXMLLoader.load(getClass().getResource(nextStepFXML));
            Stage stage = (Stage) back_button_step2.getScene().getWindow(); // Use the appropriate button for the current stage
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true); // Ensure full screen
            stage.setFullScreenExitHint(""); // Turn off the "Press ESC to exit fullscreen" text
            stage.show();
        }
    }

    private String getNextStepFXML(Session selectedSession) 
    {
        String hall = selectedSession.getHall();
        System.out.println("Selected Hall: " + hall); // Log the hall value
        return hall.contains("A") ? "/help/fxml/step3_salonA.fxml" : "/help/fxml/step3_salonB.fxml";
    }
}
