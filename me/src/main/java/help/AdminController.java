package help;


import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
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
    private TextField txtMovieTitle;
    @FXML
    private TextField txtMoviePoster;
    @FXML
    private TextField txtMovieGenre;
    @FXML
    private TextArea txtMovieSummary;
    @FXML
    private Button btnSaveMovie;

    private AdminDBH dbHandler;

    public void initialize() {
        dbHandler = new AdminDBH(); // Initialize DataBaseHandler
        paneDashboard = new AnchorPane();
        paneDashboard.setVisible(false);
        paneAddMovie = new AnchorPane();
        paneAddMovie.setVisible(true); // Initially hide the add movie pane
    }

    @FXML
    public void handleSignOut(ActionEvent event) {
        // Add logic to handle sign out functionality
        System.out.println("Sign Out button clicked");
        // Example: Close the current window
        // ((Stage) btnSignOut.getScene().getWindow()).close();
    }

    @FXML
    public void handleDashboard(ActionEvent event) {
        showPane(paneDashboard);
        System.out.println("Dashboard button clicked");
    }

    @FXML
    public void handleAddMovies(ActionEvent event) {
        showPane(paneAddMovie);
        System.out.println("Add Movies button clicked");
    }

    @FXML
    public void handleSaveMovie(ActionEvent event) {
        String title = txtMovieTitle.getText();
        String poster = txtMoviePoster.getText();
        String genre = txtMovieGenre.getText();
        String summary = txtMovieSummary.getText();

        if (title.isEmpty() || poster.isEmpty() || genre.isEmpty() || summary.isEmpty()) {
            System.out.println("Please fill in all movie details.");
            return; // Stop if any field is empty
        }

        dbHandler.AddMovie(title, poster, genre, summary);

        // Clear the input fields after saving
        txtMovieTitle.clear();
        txtMoviePoster.clear();
        txtMovieGenre.clear();
        txtMovieSummary.clear();
        showPane(paneDashboard); // Return to dashboard
    }



    @FXML
    public void handleAvailableMovies(ActionEvent event) {
        // Add logic to handle available movies functionality using dbHandler
        System.out.println("Available Movies button clicked");
        dbHandler.GetAllMovies(); // Example : calls the get all movies method from the database handler.
    }

    @FXML
    public void handleEditScreening(ActionEvent event) {
        // Add logic to handle edit screening functionality using dbHandler
        System.out.println("Edit Screening button clicked");
    }

    @FXML
    public void handleMonthlyDisplayPlan(ActionEvent event) {
        // Add logic to handle monthly display plan functionality using dbHandler
        System.out.println("Monthly Display Plan button clicked");
    }

    @FXML
    public void handleCancellationsAndRefunds(ActionEvent event) {
        // Add logic to handle cancellations and refunds functionality using dbHandler
        System.out.println("Cancellations and Refunds button clicked");
    }

    private void showPane(AnchorPane paneToShow) {
        paneDashboard.setVisible(false);
        paneAddMovie.setVisible(false);
        // Add other panes here as needed
        paneToShow.setVisible(true);
    }
}