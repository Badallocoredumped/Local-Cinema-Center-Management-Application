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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import help.classes.Session;
import help.classes.ShoppingCart;

public class Step2Controller 
{
    @FXML
    private Button next_button_step2;
    @FXML
    private Button back_button_step2;
    @FXML
    private Button SignoutButton;
    @FXML
    private Button CloseButton;
    @FXML
    private Button MinimizeButton; 
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
    private Label selectedMovieLabel;
    @FXML
    private Label selectedSession;

    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleMinimizeButtonAction(ActionEvent event) {
        Stage stage = (Stage) MinimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }
 
    @FXML
    private void initialize() 
    {
        ShoppingCart cart = ShoppingCart.getInstance();
        Movie selectedMovie = cart.getSelectedMovie();
        


        if (selectedMovie != null) 
        {
            selectedMovieLabel.setText(selectedMovie.getTitle());
            loadSessionData(selectedMovie);
        } 
        else 
        {
            selectedMovieLabel.setText("No movie selected.");
        }

        // Add listener to update selectedSession label when a session is selected
        // Add listener to update selectedSession label when a session is selected
        sessionsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> 
        {
            if (newValue != null && newValue.getValue() != null) 
            {
                Session selected = newValue.getValue();
                selectedSession.setText(
                            selected.getSession() + "\n" +
                            selected.getDay() + "\n" +
                            selected.getHall());

                
                //cart.setSelectedDaySessionAndHall(selected);
            } 
            else 
            {
                selectedSession.setText("No session selected.");
            }
        });

    }

    private void loadSessionData(Movie selectedMovie) 
    {
        if (selectedMovie == null) 
        {
            System.out.println("No movie selected");
            return;
        }

        List<Session> sessions = new ArrayList<>();
        try (Connection conn = DataBaseHandler.getConnection()) {
            // Updated query to fetch session data for a selected movie
            String query = "SELECT session_id, session_date, start_time, hall_name, vacant_seats " +
                           "FROM Sessions WHERE movie_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selectedMovie.getId());  // Use the selected movie's ID
            System.out.println("Movie ID: " + selectedMovie.getId());
    
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                // Add new session details to the sessions list, including session_id
                sessions.add(new Session(
                rs.getString("session_date"),  // session_date (as day)
                rs.getString("start_time"),    // start_time (as session)
                rs.getString("hall_name"),     // hall_name
                rs.getString("vacant_seats"),  // vacant_seats
                rs.getInt("session_id")        // session_id
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Fetched Sessions: " + sessions.size());
        for (Session session : sessions) 
        {
            System.out.println(session);
        }

        // Set up table columns for displaying sessions
        dayColumn.setCellValueFactory(param -> param.getValue().getValue().dayProperty());
        sessionColumn.setCellValueFactory(param -> param.getValue().getValue().sessionProperty());
        hallColumn.setCellValueFactory(param -> param.getValue().getValue().hallProperty());
        vacantSeatsColumn.setCellValueFactory(param -> param.getValue().getValue().vacantSeatsProperty());

        // Create the root TreeItem and add children (sessions)
        TreeItem<Session> root = new TreeItem<>(new Session("Day", "Session", "Hall", "Vacant Seats", 0));
        for (Session session : sessions) 
        {
            root.getChildren().add(new TreeItem<>(session));
        }

        sessionsTableView.setRoot(root);
        sessionsTableView.setShowRoot(false);  // Hide the root item, as it is just a placeholder
    }


    @FXML
    private void handleNextButtonAction(ActionEvent event) throws IOException 
    {
        ShoppingCart cart = ShoppingCart.getInstance();
        Session selectedSession= cart.getSelectedDaySessionAndHall();

        if(selectedSession == null)
        {
            showErrorDialog("No session selected.");
            return;
        }
        else
        {
            System.out.println("Selected session: " + selectedSession.getSession());
            //To be decided
            Parent nextRoot = FXMLLoader.load(getClass().getResource(getNextStepFXML(SelectedSession.getInstance().getSession())));
    
            // Get the current stage
            Scene scene = next_button_step2.getScene();
    
            // Set the new root to the current scene
            scene.setRoot(nextRoot);
    
            // Optionally, update the stage title if needed
            Stage stage = (Stage) next_button_step2.getScene().getWindow();
            stage.setTitle("Step 3");
            
            // Ensure the stage remains in fullscreen
            stage.setFullScreen(true);
            stage.setFullScreenExitHint(""); // Hide the exit hint
            System.out.println("Navigating to next step");
        }

    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) throws IOException 
    {
        // Load the Step1 FXML file
        Parent backRoot = FXMLLoader.load(getClass().getResource("/help/fxml/step1.fxml"));
        
        // Get the controller for Step 1 (this is required to pass data)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/help/fxml/step1.fxml"));
        backRoot = loader.load();
        
        Step1Controller step1Controller = loader.getController();
        
        // Pass the selected movie to Step 1
        ShoppingCart cart = ShoppingCart.getInstance();
        step1Controller.updateSelectedMovie(cart.getSelectedMovie());
        cart.clearSession();
        cart.clearSelectedMovie();

        // Get the current scene
        Scene scene = back_button_step2.getScene();
        
        // Set the new root to the current scene
        scene.setRoot(backRoot);

        // Optionally, update the stage title if needed
        Stage stage = (Stage) back_button_step2.getScene().getWindow();
        stage.setTitle("Step 1");
        
        // Ensure the stage remains in fullscreen
        stage.setFullScreen(true);
        stage.setFullScreenExitHint(""); // Hide the exit hint
    
    }
    

    public void updateSelectedMovie(Movie movie) 
    {
        if (movie != null) 
        {
            selectedMovieLabel.setText(movie.getTitle());
        } 
        else 
        {
            selectedMovieLabel.setText("No movie selected");
        }
    }


    @FXML
    private void handleConfirmSelection() throws IOException 
    {
        // Handle the confirmation of the selected session
        ShoppingCart cart = ShoppingCart.getInstance();
        TreeItem<Session> selectedSession = sessionsTableView.getSelectionModel().getSelectedItem();
        if (selectedSession != null) 
        {
            // Save the selected session
            Session session = selectedSession.getValue();

            SelectedSession.getInstance().setSession(session);
            cart.setSelectedDaySessionAndHall(session);

            showConfirmationDialog();

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
            // Retrieve the current stage from the Next button
            Stage stage = (Stage) SignoutButton.getScene().getWindow();

            // Create and configure the alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initOwner(stage);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Session selected successfully!");
            alert.showAndWait();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    private void showErrorDialog(String errorMessage) 
    {
        try 
        {
            // Retrieve the current stage from SignoutButton
            Stage stage = (Stage) SignoutButton.getScene().getWindow();

            // Create and configure the alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(stage);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred");
            alert.setContentText(errorMessage);
            alert.showAndWait();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    

    private String getNextStepFXML(Session selectedSession) 
    {
        
        String hall = selectedSession.getHall();
        System.out.println("Selected Hall: " + hall); // Log the hall value
        return hall.contains("A") ? "/help/fxml/step3_salonA.fxml" : "/help/fxml/step3_salonB.fxml";
        
    }
        @FXML
    private void handleSignOutButtonAction(ActionEvent event) 
    {
        try 
        {
            // Load 'login.fxml'
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/help/fxml/login.fxml"));
            Parent root = loader.load();

            // Get the current stage from the SignoutButton
            Stage stage = (Stage) SignoutButton.getScene().getWindow();

            // Create a new scene with specified size
            Scene scene = new Scene(root, 600, 400);

            // Set the new scene to the stage
            stage.setScene(scene);

            // Center the stage on the screen
            stage.centerOnScreen();

            // Optionally, disable fullscreen if it was enabled
            stage.setFullScreen(false);

            // Show the stage
            stage.show();
        } 
        catch (IOException e) 
        {
            // Display an error alert if loading fails
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Sign Out Failed");
            alert.setHeaderText("Unable to Sign Out");
            alert.setContentText("There was an error signing out. Please try again.");
            alert.showAndWait();

            e.printStackTrace();
        }
    }
}
