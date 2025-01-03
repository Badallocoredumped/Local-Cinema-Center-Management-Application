package help;

import java.io.IOException;
import java.util.List;
import help.classes.Movie;
import help.classes.MovieService;
import help.classes.SelectedMovie;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.ByteArrayInputStream;

public class Step1Controller 
{
    @FXML
    private Button next_button_step1;
    @FXML
    private ComboBox<String> searchComboBox;
    @FXML
    private TextField searchTextField;
    @FXML
    private TreeTableView<Movie> resultsTableView;
    @FXML
    private TreeTableColumn<Movie, String> movies;  // TreeTableColumn for movie names
    @FXML
    private Label movieTitleLabel;
    @FXML
    private Label movieGenreLabel;
    @FXML
    private Label movieSummaryLabel;
    @FXML
    private Label movieDurationLabel;
    @FXML
    private ImageView moviePosterImageView;

    private MovieService movieService = new MovieService();

    @FXML
    private void initialize() 
    {
        searchComboBox.getItems().addAll("Genre", "Partial Title", "Full Title");
        searchComboBox.setValue("Genre"); // Set default value to "Genre"

        // Bind the TreeTableColumn to the movie title property
        movies.setCellValueFactory(cellData -> cellData.getValue().getValue().titleProperty());

        // Add listener for row selection
        resultsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> handleMovieSelection());
    }

    @FXML
    private void handleSearchButtonAction() 
    {
        System.out.println("Search button clicked");

        String searchType = searchComboBox.getValue();
        String searchText = searchTextField.getText();

        // Validate input: only letters are allowed
        if (!searchText.matches("[a-zA-Z]+")) 
        {
            showErrorDialog("Invalid input. Please enter only letters.");
            searchTextField.clear(); // Clear the input field
            return;
        }

        List<Movie> moviesList = null;

        // Perform the search based on the selected search type
        if (searchType.equals("Genre")) 
        {
            moviesList = movieService.searchByGenre(searchText);
        } 
        else if (searchType.equals("Partial Title")) 
        {
            moviesList = movieService.searchByPartialName(searchText);
        } 
        else if (searchType.equals("Full Title")) 
        {
            moviesList = movieService.searchByFullName(searchText);
        }

        // Ensure the TreeTableView has a root item
        if (resultsTableView.getRoot() == null) 
        {
            resultsTableView.setRoot(new TreeItem<>(new Movie(0, "Movies Found", "", "", "", "", null)));
        }

        // Update the TreeTableView with the search results
        resultsTableView.getRoot().getChildren().clear();
        if (moviesList.isEmpty()) 
        {
            resultsTableView.getRoot().setValue(new Movie(0, "No Movies Found", "", "", "", "", null));
        } 
        else 
        {
            resultsTableView.getRoot().setValue(new Movie(0, "Movies Found", "", "", "", "", null));
            for (Movie movie : moviesList) 
            {
                resultsTableView.getRoot().getChildren().add(new TreeItem<>(movie));
            }
        }
    }

    @FXML
    private void handleMovieSelection() 
    {
        // Handle row selection and display movie details
        Movie selectedMovie = resultsTableView.getSelectionModel().getSelectedItem().getValue();
        if (selectedMovie != null) 
        {
            movieTitleLabel.setText(selectedMovie.getTitle());
            movieGenreLabel.setText(selectedMovie.getGenre());
            movieSummaryLabel.setText(selectedMovie.getSummary());
            movieDurationLabel.setText(selectedMovie.getDuration() + " minutes");

            // Assuming the Movie class has a method getPosterData() that returns the image data as a byte array
            byte[] posterData = selectedMovie.getPosterData();
            if (posterData != null && posterData.length > 0) 
            {
                moviePosterImageView.setImage(new Image(new ByteArrayInputStream(posterData)));
            } 
            else 
            {
                moviePosterImageView.setImage(null); // Clear the image if no data is provided
            }
        }
    }

    @FXML
    private void handleConfirmSelection() throws IOException 
    {
        // Save the selected movie
        Movie selectedMovie = resultsTableView.getSelectionModel().getSelectedItem().getValue();
        if (selectedMovie != null) 
        {
            SelectedMovie.getInstance().setMovie(selectedMovie);

            // Show confirmation dialog
            showConfirmationDialog();

            // Proceed to the next stage
            /* Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/step2.fxml"));
            Stage stage = (Stage) next_button_step1.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true); // Ensure full screen
            stage.setFullScreenExitHint(""); // Turn off the "Press ESC to exit fullscreen" text
            stage.show(); */
        } 
        else 
        {
            // Show an error dialog or message indicating that no movie was selected
            System.out.println("No movie selected");
        }
    }

    @FXML
    private void handleNextButtonAction() throws IOException 
    {
        Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/step2.fxml"));
        Stage stage = (Stage) next_button_step1.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        /* stage.setFullScreen(true); // Ensure full screen
        stage.setFullScreenExitHint(""); // Turn off the "Press ESC to exit fullscreen" text
        stage.show(); */
    }

    private void showErrorDialog(String errorMessage) 
    {
        try 
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/help/fxml/ErrorDialog.fxml"));
            Parent root = loader.load();

            ErrorDialogController controller = loader.getController();
            controller.setErrorMessage(errorMessage);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Error");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(next_button_step1.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private void showConfirmationDialog() 
    {
        try 
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/help/fxml/ConfirmationDialog.fxml"));
            Parent root = loader.load();

            ConfirmationDialogController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Confirmation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(next_button_step1.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}
