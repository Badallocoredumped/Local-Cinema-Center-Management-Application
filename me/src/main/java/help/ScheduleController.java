package help;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import help.classes.Movie;
import help.classes.Schedule;
import help.classes.Session;
import help.utilities.AdminDBH;

public class ScheduleController {

    @FXML
    private TextField movieTitleField;

    @FXML
    private ComboBox<String> hallComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> timeField;

    @FXML
    private TableView<Schedule> sessionTable;

    @FXML
    private TableColumn<Schedule, Integer> idColumn;

    @FXML
    private TableColumn<Schedule, String> titleColumn; // This column might be referencing the wrong class (Movie)

    @FXML
    private TableColumn<Schedule, LocalDate> dateColumn;

    @FXML
    private TableColumn<Schedule, Time> timeColumn;

    @FXML
    private TableColumn<Schedule, String> hallColumn;

    private ObservableList<Schedule> sessionList = FXCollections.observableArrayList();

    private AdminDBH dbhandler = new AdminDBH();
    private Schedule selectedSession = null;

    public void initialize() 
    {

            timeField.getItems().addAll(
            "14:00",
            "16:00",
            "18:00",
            "20:00"
        );
        timeField.setValue("14:00");
        idColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getSessionId()));

        titleColumn.setCellValueFactory(cellData -> 
        {
            Schedule schedule = cellData.getValue();
            if (schedule != null) 
            {
                try {
                    String title = dbhandler.getMovieTitleFromId(schedule.getMovieId());
                    return new ReadOnlyObjectWrapper<>(title != null ? title : "Unknown Movie"); // Handle null case
                } catch (SQLException e) {
                    e.printStackTrace(); // Handle the exception appropriately (e.g., show an alert)
                    return new ReadOnlyObjectWrapper<>("Error"); // Or some other error message
                }
            } 
            else 
            {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        hallColumn.setCellValueFactory(cellData -> cellData.getValue().hallNameProperty());

        dateColumn.setCellValueFactory(cellData -> cellData.getValue().sessionDateProperty());

        timeColumn.setCellValueFactory(cellData -> cellData.getValue().startTimeProperty());


        // Populate ComboBox with hall names
        hallComboBox.setItems(FXCollections.observableArrayList("Hall_A", "Hall_B"));

        // Initialize schedule list and bind it to the TableView
        sessionTable.setItems(sessionList);
        sessionTable.setOnMouseClicked(this::onRowSelect);

        loadSessionsFromDatabase();

    }

    @FXML
    void OnAdd(ActionEvent event) 
    {
        String title = movieTitleField.getText();
        String hall = hallComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String timeF = timeField.getValue();

        if (title.isEmpty() || hall == null || date == null || timeF.isEmpty()) 
        {
            showAlert(Alert.AlertType.WARNING, "Missing information", "Please fill in all fields before updating the schedule.");
            return;
        }
        String formattedTime = timeF + ":00";
        Time time = Time.valueOf(formattedTime);

        int movieId = AdminDBH.getMovieIdFromTitle(title);

        try {
            dbhandler.AddSession(movieId, hall, date, time);
        } catch (SQLException e) 
        {
            e.printStackTrace();
        }
        loadSessionsFromDatabase();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Session added successfully!");
        clearFields();
    }

    @FXML
    void onUpdate(ActionEvent event) {
        selectedSession = sessionTable.getSelectionModel().getSelectedItem();
        if (selectedSession == null) 
        {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a movie to delete.");
            return;
        }

        String newTitle = movieTitleField.getText();
        String newHall = hallComboBox.getValue();
        LocalDate newDate = datePicker.getValue();
        String time = timeField.getValue();

        

        if (newTitle.isEmpty() || newHall == null || newDate == null || time.isEmpty()) 
        {
            showAlert(Alert.AlertType.WARNING, "Missing information", "Please fill in all fields before updating the schedule.");
            return;
        }

        String formattedTime = time + ":00";
        Time newTime = Time.valueOf(formattedTime);
        int sessionId = selectedSession.getSessionId();
        int movieId = AdminDBH.getMovieIdFromTitle(newTitle);

        try {
            dbhandler.UpdateSession(sessionId, movieId, newHall, newDate, newTime);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadSessionsFromDatabase();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Session updated successfully!");
        clearFields();
    }

    @FXML
    void OnDelete(ActionEvent event) 
    {
        selectedSession = sessionTable.getSelectionModel().getSelectedItem();
        if (selectedSession == null) 
        {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a movie to delete.");
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Are you sure you want to delete this movie?");
        confirmAlert.setContentText("Session: " + selectedSession.getSessionId());

        confirmAlert.showAndWait().ifPresent(response -> 
        {
            if (response == javafx.scene.control.ButtonType.OK) 
            {
                int sessionId = selectedSession.getSessionId();
                try {
                    dbhandler.DeleteSession(sessionId);
                } catch (SQLException e) 
                {
                    e.printStackTrace();
                }
                loadSessionsFromDatabase();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Session deleted successfully!");
                clearFields();
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) 
    {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        // Get the primary stage that contains this controller's scene
        Stage primaryStage = (Stage) sessionTable.getScene().getWindow();
        
        // Set the alert's owner to the primary stage
        alert.initOwner(primaryStage);
        
        // Keep alert within fullscreen window
        alert.initModality(Modality.WINDOW_MODAL);
        
        // Show and wait for user response
        alert.showAndWait();
    }

    private void clearFields() 
    {
        movieTitleField.clear();
        hallComboBox.getSelectionModel().clearSelection();
        datePicker.setValue(null);
        timeField.setValue(null);
    }

    private void loadSessionsFromDatabase() {
        sessionList.clear();
        try {
            sessionList.addAll(dbhandler.GetAllSessions());
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    private void onRowSelect(MouseEvent event) {
        selectedSession = sessionTable.getSelectionModel().getSelectedItem();
        if (selectedSession != null) {
            try {
                movieTitleField.setText(dbhandler.getMovieTitleFromId(selectedSession.getMovieId()));
            } catch (SQLException e) {
                movieTitleField.setText("Error");
                e.printStackTrace();
            }
            hallComboBox.setValue(selectedSession.getHallName());
            datePicker.setValue(selectedSession.getSessionDate());
            LocalTime startTime = selectedSession.getStartTime().toLocalTime();
            String formattedTime = startTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            timeField.setValue(formattedTime);
        }
    }

    public void onOrganizeMovie(ActionEvent event) throws IOException 
    {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        if (stage.getTitle().equals("Organize Movies")) 
        {
            return;
        }
        Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/OrganizeMovie.fxml"));
        changeScene(stage, root, "Organize Movies");
    }

    // Function to load Cancellations and Refunds screen
    public void onCancellationsRefunds(ActionEvent event) throws IOException 
    {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        if (stage.getTitle().equals("Cancellations and Refunds")) 
        {
            return;
        }
        Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/CancelRefund.fxml"));
        changeScene(stage, root, "Cancellations and Refunds");
    }

    // Function to sign out
    public void onsignOut(ActionEvent event) throws IOException
    {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/login.fxml"));
        changeScene(stage, root, "Login");
    }

    // Helper function to change the scene
    private void changeScene(Stage stage, Parent root, String newSceneTitle) {
        Scene scene = stage.getScene();
        scene.setRoot(root);
        stage.setTitle(newSceneTitle);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
    }
}

/* Todo:
    Add more Alerts for appropriate error
*/
