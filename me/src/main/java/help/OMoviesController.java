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

/**
 * Controller class for managing movie-related actions in the application.
 * Handles interactions with the user interface related to movie management, 
 * such as deleting, adding, or editing movie details.
 */
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

    /**
     * Handles the close button action. This method closes the current stage (window) when the user clicks the close button.
     * 
     * @param event The event triggered by the close button click.
     */
    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the minimize button action. This method minimizes the current stage (window) when the user clicks the minimize button.
     * 
     * @param event The event triggered by the minimize button click.
     */
    @FXML
    private void handleMinimizeButtonAction(ActionEvent event) {
        Stage stage = (Stage) MinimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Initializes the controller. Sets up the combo box with genre options, binds the table columns to the movie properties,
     * and loads the movie data from the database.
     */
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


    /**
     * Handles the add movie button action. This method validates the inputs, checks if the movie already exists, 
     * and adds the new movie to the database.
     * 
     * @param event The event triggered by the add button click.
     * @throws SQLException If there is an issue with the database operation.
     */
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

    /**
     * Handles the update movie button action. This method validates the inputs, checks if the movie exists, 
     * and updates the selected movie in the database.
     * 
     * @param event The event triggered by the update button click.
     * @throws SQLException If there is an issue with the database operation.
     */
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

    /**
     * Handles the clear button action. This method clears all the fields in the form.
     * 
     * @param event The event triggered by the clear button click.
     */
    @FXML
    private void onClear(ActionEvent event) {
        clearForm();
    }

    /**
     * Handles the import movie poster button action. This method opens a file chooser to select an image file and 
     * imports the selected poster for the movie.
     * 
     * @param event The event triggered by the import button click.
     * @throws IOException If there is an issue with reading the file.
     */
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

    /**
     * Handles the row selection event in the movie table. This method sets the selected movie's information 
     * into the form for editing.
     * 
     * @param event The mouse event triggered by selecting a row in the table.
     */
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

    /**
     * Handles the delete movie button action. This method checks if a movie is selected, 
     * shows a confirmation dialog, and deletes the selected movie from the database upon confirmation.
     * 
     * @param event The event triggered by the delete button click.
     */
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

        // Get the window owner
        Stage stage = (Stage) cmbGenre.getScene().getWindow();
                
        // Configure alert
        confirmAlert.initOwner(stage);
        confirmAlert.initModality(Modality.APPLICATION_MODAL);

        // Position alert
        Stage alertStage = (Stage) confirmAlert.getDialogPane().getScene().getWindow();
        alertStage.setAlwaysOnTop(true);

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

    /**
     * Loads the movies from the database into the movie list.
     */
    private void loadMoviesFromDatabase() {
        movieList.clear();
        movieList.addAll(dbHandler.GetAllMovies());
    }

    /**
     * Clears all the input fields and the movie poster.
     */
    private void clearForm() {
        txtMovieTitle.clear();
        cmbGenre.setValue(null);
        txtSummary.clear();
        posterData = null;
        selectedMovie = null;
        moviePosterImageView.setImage(null);
    }

    /**
     * Shows an alert with a specified type, title, and content message.
     * 
     * @param alertType The type of the alert (e.g., WARNING, INFORMATION, ERROR).
     * @param title The title of the alert.
     * @param content The content message of the alert.
     */
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

    /**
     * Displays the movie poster for the selected movie. If the movie has no poster, the image view is cleared.
     * 
     * @param movie The movie whose poster needs to be displayed.
     */
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

    /**
     * Loads the Monthly Schedule screen into the current window.
     * 
     * @param event The event triggered by the "Schedule" button click.
     * @throws IOException If there is an issue with loading the FXML file.
     */
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

    /**
     * Loads the Organize Movies screen into the current window.
     * 
     * @param event The event triggered by the "Organize Movies" button click.
     * @throws IOException If there is an issue with loading the FXML file.
     */
    @FXML
    public void onOrganizeMovies(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        if (stage.getTitle().equals("Organize Movies")) {
            return;
        }
        Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/OrganizeMovie.fxml"));
        changeScene(stage, root, "Organize Movies");
    }

    /**
     * Loads the Cancellations and Refunds screen into the current window.
     * 
     * @param event The event triggered by the "Cancellations and Refunds" button click.
     * @throws IOException If there is an issue with loading the FXML file.
     */
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

    /**
     * Handles the sign-out button action. This method loads the login screen and switches the current window to the login scene.
     * 
     * @param event The event triggered by the sign-out button click.
     */
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

    /**
     * Helper function to change the scene of the current stage.
     * 
     * @param stage The current stage.
     * @param root The new root of the scene.
     * @param newSceneTitle The title for the new scene.
     */
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
