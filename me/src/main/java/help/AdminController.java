package help;


import java.io.ByteArrayInputStream;
import java.io.File;

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
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import help.classes.Movie;
import help.utilities.AdminDBH;

public class AdminController {

    

    @FXML
    private TreeTableView<Movie> resultsTableView;
    @FXML
    private TextField txtMovieTitle;
    @FXML
    private TextField txtGenre;
    @FXML
    private TextField txtDuration;
    @FXML
    private TextField txtSummary;
    @FXML
    private ComboBox<String> cmbGenre;
    @FXML
    private Button btnAdd, btnUpdate, btnClear, btnDelete;
    @FXML
    private TableView<Movie> tblMovies;
    @FXML
    private TableColumn<Movie, String> colTitle, colGenre, colDuration, colSummary;
    @FXML
    private ImageView imgPoster;

    private ObservableList<Movie> movieList = FXCollections.observableArrayList();
    private Movie selectedMovie = null;
    private String posterPath = null;
    private AdminDBH dbHandler = new AdminDBH();

    @FXML
    public void initialize() {
        cmbGenre.setItems(FXCollections.observableArrayList("Action", "Drama", "Comedy", "Horror"));

        colTitle.setCellValueFactory(data -> data.getValue().titleProperty());
        colGenre.setCellValueFactory(data -> data.getValue().genreProperty());
        colDuration.setCellValueFactory(data -> data.getValue().durationProperty());
        colSummary.setCellValueFactory(data -> data.getValue().summaryProperty());

        tblMovies.setItems(movieList);
        tblMovies.setOnMouseClicked(this::onRowSelect);

        loadMoviesFromDatabase();
    }

    @FXML
    private void onAdd(ActionEvent event) {
        String title = txtMovieTitle.getText();
        String genre = cmbGenre.getValue();
        String duration = txtDuration.getText();
        String summary = txtSummary.getText();

        if (title.isEmpty() || genre == null || duration.isEmpty() || summary.isEmpty() || posterPath == null) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Data", "Please fill in all fields.");
            return;
        }

        AdminDBH.AddMovie(title, posterPath, genre, summary, duration);
        loadMoviesFromDatabase();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Movie added successfully!");
        clearForm();
    }

    @FXML
    private void onUpdate(ActionEvent event) 
    {
        if (selectedMovie == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a movie to update.");
            return;
        }

        if (!txtMovieTitle.getText().isEmpty()) selectedMovie.setTitle(txtMovieTitle.getText());
        if (!txtGenre.getText().isEmpty()) selectedMovie.setGenre(txtGenre.getText());
        if (!txtDuration.getText().isEmpty()) selectedMovie.setDuration(txtDuration.getText());
        if (!txtSummary.getText().isEmpty()) selectedMovie.setSummary(txtSummary.getText());
        if (posterPath != null) selectedMovie.setPosterUrl(posterPath);
        String newTitle = txtMovieTitle.getText();
        String newGenre = cmbGenre.getValue();
        String newDuration = txtDuration.getText();
        String newSummary = txtSummary.getText();
        tblMovies.refresh();

        int movieId = dbHandler.getMovieIdFromTitle(selectedMovie.getTitle());
        dbHandler.FullUpdateMovie(movieId, newTitle, posterPath, newGenre, newSummary, newDuration);
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
    private void onImport(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Movie Poster");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            posterPath = selectedFile.toURI().toString();
            imgPoster.setImage(new Image(posterPath));
        }
    }

    private void onRowSelect(MouseEvent event) {
        selectedMovie = tblMovies.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            txtMovieTitle.setText(selectedMovie.getTitle());
            cmbGenre.setValue(selectedMovie.getGenre());
            txtDuration.setText(selectedMovie.getDuration());
            txtSummary.setText(selectedMovie.getSummary());
            imgPoster.setImage(new Image(selectedMovie.getPosterUrl()));
            posterPath = selectedMovie.getPosterUrl();
        }
    }

    private void loadMoviesFromDatabase() {
        movieList.clear();
        dbHandler.GetAllMovies(); // Assuming GetAllMovies populates movieList
    }

    private void clearForm() {
        txtMovieTitle.clear();
        txtGenre.clear();
        txtDuration.clear();
        txtSummary.clear();
        cmbGenre.setValue(null);
        imgPoster.setImage(null);
        posterPath = null;
        selectedMovie = null;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}