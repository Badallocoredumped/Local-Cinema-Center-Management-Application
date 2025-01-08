package help;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;

import help.classes.Movie;
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
    private TextField timeField;

    @FXML
    private TableView<Session> sessionTable;

    @FXML
    private TableColumn<Session, Integer> idColumn;

    @FXML
    private TableColumn<Session, String> titleColumn; // This column might be referencing the wrong class (Movie)

    @FXML
    private TableColumn<Session, LocalDate> dateColumn;

    @FXML
    private TableColumn<Session, Time> timeColumn;

    @FXML
    private TableColumn<Session, String> hallColumn;

    private ObservableList<Session> sessionList = FXCollections.observableArrayList();

    private AdminDBH dbhandler = new AdminDBH();
    private Session selectedSession = null;

    public void initialize() 
    {
        idColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getSessionId()));

        titleColumn.setCellValueFactory(cellData -> 
        {
            Session session = cellData.getValue();
            if (session != null) 
            {
                try {
                    String title = dbhandler.getMovieTitleFromId(session.getMovieId());
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
        String timeF = timeField.getText();

        if (title.isEmpty() || hall == null || date == null || timeF.isEmpty()) 
        {
            showAlert(Alert.AlertType.WARNING, "Missing information", "Please fill in all fields before updating the schedule.");
            return;
        }
        Time time = Time.valueOf(timeF);

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
    void OnUpdate(ActionEvent event) {
        selectedSession = sessionTable.getSelectionModel().getSelectedItem();
        if (selectedSession == null) 
        {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a movie to delete.");
            return;
        }

        String newTitle = movieTitleField.getText();
        String newHall = hallComboBox.getValue();
        LocalDate newDate = datePicker.getValue();
        String time = timeField.getText();

        if (newTitle.isEmpty() || newHall == null || newDate == null || time.isEmpty()) 
        {
            showAlert(Alert.AlertType.WARNING, "Missing information", "Please fill in all fields before updating the schedule.");
            return;
        }

        
        Time newTime = Time.valueOf(time);
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

    private void showAlert(Alert.AlertType type, String title, String message) 
    {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() 
    {
        movieTitleField.clear();
        hallComboBox.getSelectionModel().clearSelection();
        datePicker.setValue(null);
        timeField.clear();
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
            timeField.setText(selectedSession.getStartTime().toString());
        }
    }
}

/* Todo:
    Add more Alerts for appropriate error
    hamdle the vacant seats issue, I'm putting everything to 0 for now
*/
