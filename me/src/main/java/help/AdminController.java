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
    private TextField movieTitleField;
    @FXML
    private TextField movieGenreField;
    @FXML
    private TextArea movieSummaryArea;
    @FXML
    private ImageView moviePosterImageView;
    @FXML
    private Label imagePathLabel;
    @FXML
    private TableView<AdminDBH.Movie> movieTable;
    @FXML
    private TableColumn<AdminDBH.Movie, String> titleColumn;
    @FXML
    private TableColumn<AdminDBH.Movie, String> genreColumn;
    @FXML
    private TableColumn<AdminDBH.Movie, String> summaryColumn;
    @FXML
    private File selectedFile;
    @FXML
    private Button btnSignOut;
    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnAddMovies;
    @FXML
    private Button btnAvailableMovies;
    @FXML
    private Button btnEditScreening;
    @FXML
    private Button btnMonthlyDisplayPlan;
    @FXML
    private Button btnCancellationsAndRefunds;
    @FXML
    private AnchorPane paneDashboard;
    @FXML
    private AnchorPane paneAddMovie; // Pane for adding movies
    @FXML
    private TextField txtMoviePoster;
    @FXML
    private TextField txtMovieGenre;
    @FXML
    private TextArea txtMovieSummary;
    @FXML
    private Button btnSaveMovie;

    private AdminDBH dbHandler;

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
    private ComboBox<String> cmbType;
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

    @FXML
    public void initialize() {
        // Initialize the ComboBox with movie types
        cmbType.setItems(FXCollections.observableArrayList("Action", "Drama", "Comedy", "Horror"));

        // Set up table columns
        colTitle.setCellValueFactory(data -> data.getValue().titleProperty());
        colGenre.setCellValueFactory(data -> data.getValue().genreProperty());
        colDuration.setCellValueFactory(data -> data.getValue().durationProperty());
        colSummary.setCellValueFactory(data -> data.getValue().summaryProperty());

        // Set the table's items
        tblMovies.setItems(movieList);

        // Add listener for table row selection
        tblMovies.setOnMouseClicked(this::onRowSelect);

    }

    @FXML
    private void onAdd(ActionEvent event) {
        String title = txtMovieTitle.getText();
        String genre = txtGenre.getText();
        String duration = txtDuration.getText();
        String summary = txtSummary.getText();
        String type = cmbType.getValue();

        if (title.isEmpty() || genre.isEmpty() || duration.isEmpty() || summary.isEmpty() || type == null || posterPath == null) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Data", "Please fill in all fields.");
            return;
        }

        Movie newMovie = new Movie(0, title, posterPath, genre, summary, duration, null);
        movieList.add(newMovie);
        clearForm();
    }

    @FXML
    private void onUpdate(ActionEvent event) 
    {
        if (selectedMovie == null) 
        {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a movie to update.");
            return;
        }

        if (!txtMovieTitle.getText().isEmpty()) selectedMovie.setTitle(txtMovieTitle.getText());
        if (!txtGenre.getText().isEmpty()) selectedMovie.setGenre(txtGenre.getText());
        if (!txtDuration.getText().isEmpty()) selectedMovie.setDuration(txtDuration.getText());
        if (!txtSummary.getText().isEmpty()) selectedMovie.setSummary(txtSummary.getText());
        if (posterPath != null) selectedMovie.setPosterUrl(posterPath);

        tblMovies.refresh();
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
            posterPath = selectedFile.toURI().toString(); // Convert to URI for ImageView
            imgPoster.setImage(new Image(posterPath));
        }
    }

    private void onRowSelect(MouseEvent event) {
        selectedMovie = tblMovies.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            txtMovieTitle.setText(selectedMovie.getTitle());
            txtGenre.setText(selectedMovie.getGenre());
            txtDuration.setText(selectedMovie.getDuration());
            txtSummary.setText(selectedMovie.getSummary());
            imgPoster.setImage(new Image(selectedMovie.getPosterUrl()));
            posterPath = selectedMovie.getPosterUrl();
        }
    }

    private void clearForm() {
        txtMovieTitle.clear();
        txtGenre.clear();
        txtDuration.clear();
        txtSummary.clear();
        cmbType.setValue(null);
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