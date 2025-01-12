package help;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.function.Function;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import help.classes.Movie;
import help.utilities.AdminDBH;

public class OMoviesController {

    @FXML
    private TextField txtMovieTitle;
    @FXML
    private TextField txtSummary;
    @FXML
    private ComboBox<String> cmbGenre;
    @FXML
    private Button btnAdd, btnUpdate, btnClear, btnDelete, btnImport , SignoutButton,MinimizeButton,CloseButton;
    @FXML
    private TableView<Movie> tblMovies;
    @FXML
    private TableColumn<Movie, String> colTitle, colGenre, colDuration, colSummary;
    @FXML
    private ImageView moviePosterImageView;

    private ObservableList<Movie> movieList = FXCollections.observableArrayList();
    private Movie selectedMovie = null;
    private byte[] posterData = null;
    private AdminDBH dbHandler = new AdminDBH();
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
    public void initialize() {
        cmbGenre.setItems(FXCollections.observableArrayList("Action", "Drama", "Comedy", "Horror", "Romance", "Documentary", "Adventure", "Sci-Fi"));

        colTitle.setCellValueFactory(data -> data.getValue().titleProperty());
        colGenre.setCellValueFactory(data -> data.getValue().genreProperty());
        colDuration.setCellValueFactory(data -> data.getValue().durationProperty());
        colSummary.setCellValueFactory(data -> data.getValue().summaryProperty());

        tblMovies.setItems(movieList);
        tblMovies.setOnMouseClicked(this::onRowSelect);

        loadMoviesFromDatabase();
        tblMovies.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayPoster(newSelection);
            }
        });
    }

    //ADD UPDATE FUNCTIONALITY, IF A SESSION HAS BOUGHT TICKETS, THEN THE SESSION CANNOT BE DELETED/MODIFIED



    
    @FXML
    private void onAdd(ActionEvent event) throws SQLException 
    {
        String title = txtMovieTitle.getText();
        if (title.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Title", "Please enter a movie title.");
            return;
        }

        try {
            if (dbHandler.movieExists(title)) {
                showAlert(Alert.AlertType.WARNING, "Duplicate Movie", "A movie with this title already exists.");
                return;
            }
            String genre = cmbGenre.getValue();
            String summary = txtSummary.getText();

            if (posterData == null) {
                showAlert(Alert.AlertType.WARNING, "Incomplete Data", "Please import a poster.");
                return;
            }

            if (title.isEmpty() || genre == null || summary.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Incomplete Data", "Please fill in all fields.");
                return;
            }

            AdminDBH.AddMovie(title, posterData, genre, summary);

            loadMoviesFromDatabase();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Movie added successfully!");
            clearForm();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add movie: " + e.getMessage());
        }
    }

    @FXML
    private void onUpdate(ActionEvent event) throws SQLException {
        if (selectedMovie == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a movie to update.");
            return;
        }

        String newTitle = txtMovieTitle.getText();
        if (newTitle.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Title", "Please enter a movie title.");
            return;
        }

        try {
            if (!newTitle.equals(selectedMovie.getTitle()) && dbHandler.movieExists(newTitle)) {
                showAlert(Alert.AlertType.WARNING, "Duplicate Movie", "A movie with this title already exists.");
                return;
            }
            String newGenre = cmbGenre.getValue();
            String newSummary = txtSummary.getText();

            if (posterData == null) {
                posterData = selectedMovie.getPosterImage();
            }

            if (newTitle.isEmpty() || newGenre == null || newSummary.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Incomplete Data", "Please fill in all fields.");
                return;
            }

            int movieId = AdminDBH.getMovieIdFromTitle(selectedMovie.getTitle());
            dbHandler.FullUpdateMovie(movieId, newTitle, posterData, newGenre, newSummary);
            loadMoviesFromDatabase();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Movie updated successfully!");
            clearForm();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update movie: " + e.getMessage());
        }
    }

    @FXML
    private void onClear(ActionEvent event) {
        clearForm();
    }

    @FXML
    private void onImport(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Movie Poster");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            posterData = Files.readAllBytes(selectedFile.toPath());
            if (posterData.length > 0) {
                moviePosterImageView.setImage(new Image(new ByteArrayInputStream(posterData)));
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Error", "Selected file is empty.");
                moviePosterImageView.setImage(null); // Clear if empty
            }
        }
    }

    private void onRowSelect(MouseEvent event) {
        selectedMovie = tblMovies.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            txtMovieTitle.setText(selectedMovie.getTitle());
            cmbGenre.setValue(selectedMovie.getGenre());
            txtSummary.setText(selectedMovie.getSummary());
            posterData = selectedMovie.getPosterImage();
            displayPoster(selectedMovie);
        }
    }

    @FXML
    private void onDelete(ActionEvent event) {
        if (selectedMovie == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a movie to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Are you sure you want to delete this movie?");
        confirmAlert.setContentText("Movie: " + selectedMovie.getTitle());

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                int movieId = AdminDBH.getMovieIdFromTitle(selectedMovie.getTitle());
                try {
                    dbHandler.deleteMovie(movieId);
                } catch (SQLException e) 
                {
                    // Handle alert
                    e.printStackTrace();
                }
                loadMoviesFromDatabase();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Movie deleted successfully!");
                clearForm();
            }
        });
    }

    private void loadMoviesFromDatabase() {
        movieList.clear();
        movieList.addAll(dbHandler.GetAllMovies());
    }

    private void clearForm() {
        txtMovieTitle.clear();
        cmbGenre.setValue(null);
        txtSummary.clear();
        posterData = null;
        selectedMovie = null;
        moviePosterImageView.setImage(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) 
    {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        // Get the primary stage
        Stage primaryStage = (Stage) tblMovies.getScene().getWindow();
        
        // Set the alert's owner to the primary stage
        alert.initOwner(primaryStage);
        
        // Set modality to keep alert within the window
        alert.initModality(Modality.WINDOW_MODAL);
        
        alert.showAndWait();
    }

    private void displayPoster(Movie movie) {
        if (movie == null) {
            moviePosterImageView.setImage(null);
            return;
        }
        byte[] posterData = movie.getPosterImage();
        if (posterData != null && posterData.length > 0) {
            moviePosterImageView.setImage(new Image(new ByteArrayInputStream(posterData)));
        } else {
            moviePosterImageView.setImage(null);
        }
    }

    // Function to load Monthly Schedule screen
    public void onSchedule(ActionEvent event) throws IOException 
    {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        if (stage.getTitle().equals("Monthly Schedule")) 
        {
            return;
        }
        Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/MovieSchedule.fxml"));
        changeScene(stage, root, "Monthly Schedule");
    }

    @FXML
    public void onOrganizeMovies(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        if (stage.getTitle().equals("Organize Movies")) {
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

    // Helper function to change the scene
    private void changeScene(Stage stage, Parent root, String newSceneTitle) {
        Scene scene = stage.getScene();
        scene.setRoot(root);
        stage.setTitle(newSceneTitle);
        if(newSceneTitle.equals("Login"))
        {
            stage.setFullScreen(false);
            stage.setFullScreenExitHint("");
        }
        else
        {
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");

        }
    }
    
}
