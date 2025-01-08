package help;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import help.classes.Movie;
import help.utilities.AdminDBH;

public class OMoviesController {

    

    @FXML
    private TextField txtMovieTitle;
    @FXML
    private TextField txtDuration;
    @FXML
    private TextField txtSummary;
    @FXML
    private ComboBox<String> cmbGenre;
    @FXML
    private Button btnAdd, btnUpdate, btnClear, btnDelete, btnImport;
    @FXML
    private TableView<Movie> tblMovies;
    @FXML
    private TableColumn<Movie, String> colTitle, colGenre, colDuration, colSummary;
    @FXML
    private ImageView moviePosterImageView;

    private ObservableList<Movie> movieList = FXCollections.observableArrayList();
    private Movie selectedMovie = null;
    private String posterPath = null;
    private byte [] posterData = null;
    private AdminDBH dbHandler = new AdminDBH();

    @FXML
    public void initialize() 
    {
        cmbGenre.setItems(FXCollections.observableArrayList("Action", "Drama", "Comedy", "Horror", "Romance"));

        colTitle.setCellValueFactory(data -> data.getValue().titleProperty());
        colGenre.setCellValueFactory(data -> data.getValue().genreProperty());
        colDuration.setCellValueFactory(data -> data.getValue().durationProperty());
        colSummary.setCellValueFactory(data -> data.getValue().summaryProperty());

        tblMovies.setItems(movieList);
        tblMovies.setOnMouseClicked(this::onRowSelect);

        loadMoviesFromDatabase();
        tblMovies.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> 
        {
            if (newSelection != null) 
            {
                try 
                {
                    displayPoster(newSelection);
                }
                finally
                {
                    moviePosterImageView.setImage(null);
                }
            }
        });
    }

    @FXML
    private void onAdd(ActionEvent event) {
        String title = txtMovieTitle.getText();
        String genre = cmbGenre.getValue();
        String duration = txtDuration.getText();
        String summary = txtSummary.getText();

        if (posterPath != null) {
            File posterFile = new File(posterPath);
            if (posterFile.exists()) {
                try {
                    posterData = Files.readAllBytes(posterFile.toPath());
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to read the poster file.");
                    e.printStackTrace();
                    return;
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Invalid Poster", "The specified poster file does not exist.");
                return;
            }
        } else 
        {
            posterData = selectedMovie.getPosterData();
            posterPath = selectedMovie.getPosterUrl();
        }
    
        if (title.isEmpty() || genre == null || duration.isEmpty() || summary.isEmpty() || posterPath == null || posterData == null) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Data", "Please fill in all fields and import a poster.");
            return;
        }
    
        AdminDBH.AddMovie(title, posterPath, posterData, genre, summary, duration);
        loadMoviesFromDatabase();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Movie added successfully!");
        clearForm();
    }

    @FXML
private void onUpdate(ActionEvent event) {
    if (selectedMovie == null) {
        showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a movie to update.");
        return;
    }

    String newTitle = txtMovieTitle.getText();
    String newGenre = cmbGenre.getValue();
    String newDuration = txtDuration.getText();
    String newSummary = txtSummary.getText();

    // Check if a new poster has been imported, otherwise use the existing data
    if (posterPath != null) {
        File posterFile = new File(posterPath);
        if (posterFile.exists()) {
            try {
                posterData = Files.readAllBytes(posterFile.toPath());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to read the poster file.");
                e.printStackTrace();
                return;
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Invalid Poster", "The specified poster file does not exist.");
            return;
        }
    } else {
        posterData = selectedMovie.getPosterData();
        posterPath = selectedMovie.getPosterUrl();
    }

    if (newTitle.isEmpty() || newGenre == null || newDuration.isEmpty() || newSummary.isEmpty() || posterPath == null || posterData == null) {
        showAlert(Alert.AlertType.WARNING, "Incomplete Data", "Please fill in all fields.");
        return;
    }

    int movieId = dbHandler.getMovieIdFromTitle(selectedMovie.getTitle());
    dbHandler.FullUpdateMovie(movieId, newTitle, posterPath, posterData, newGenre, newSummary, newDuration);
    loadMoviesFromDatabase();
    showAlert(Alert.AlertType.INFORMATION, "Success", "Movie updated successfully!");
    clearForm();
}


    @FXML
    private void onClear(ActionEvent event) 
    {
        clearForm();
    }

    @FXML
    private void onImport(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Movie Poster");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            posterPath = selectedFile.getAbsolutePath(); // Get absolute path for database storage
            byte[] posterData = Files.readAllBytes(selectedFile.toPath());
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
            txtDuration.setText(selectedMovie.getDuration());
            txtSummary.setText(selectedMovie.getSummary());
            posterPath = selectedMovie.getPosterUrl();
            displayPoster(selectedMovie);
        }
    }

    @FXML
private void onDelete(ActionEvent event) {
    if (selectedMovie == null) {
        showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a movie to delete.");
        return;
    }

    // Confirm deletion
    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmAlert.setTitle("Confirm Deletion");
    confirmAlert.setHeaderText("Are you sure you want to delete this movie?");
    confirmAlert.setContentText("Movie: " + selectedMovie.getTitle());

    confirmAlert.showAndWait().ifPresent(response -> {
        if (response == javafx.scene.control.ButtonType.OK) {
            int movieId = dbHandler.getMovieIdFromTitle(selectedMovie.getTitle());
            dbHandler.deleteMovie(movieId);
            loadMoviesFromDatabase();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Movie deleted successfully!");
            clearForm();
        }
    });
}

    private void loadMoviesFromDatabase() {
        movieList.clear();
        movieList.addAll(dbHandler.GetAllMovies()); // Assuming GetAllMovies returns a List<Movie>
    }

    private void clearForm() {
        txtMovieTitle.clear();
        cmbGenre.setValue(null);
        txtDuration.clear();
        txtSummary.clear();
        cmbGenre.setValue(null);
        posterPath = null;
        selectedMovie = null;
        moviePosterImageView.setImage(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void displayPoster(Movie movie) 
    {
        if (movie == null) {
            moviePosterImageView.setImage(null);
            return;
        }
        byte[] posterData = movie.getPosterData();
        if (posterData != null && posterData.length > 0) {
            moviePosterImageView.setImage(new Image(new ByteArrayInputStream(posterData)));
        } else {
            moviePosterImageView.setImage(null); // Clear the image if no data is provided
        }
    }
}