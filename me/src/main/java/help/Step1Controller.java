package help;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import help.classes.Movie;
import help.classes.MovieService;
import help.classes.SelectedMovie;
import help.classes.ShoppingCart;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import javafx.application.Platform;
import javafx.event.ActionEvent;

public class Step1Controller 
{
    @FXML
    private Button next_button_step1;
    @FXML
    private Button CloseButton;
    @FXML
    private Button MinimizeButton; 
    @FXML
    private Button SignoutButton; // Ensure fx:id matches 'SignoutButton'
    @FXML
    private Button confirmButton;
    
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
    private Label selectedMovieLabel;

    @FXML
    private ImageView moviePosterImageView;

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
    private MovieService movieService = new MovieService();


    private void updateMoviePoster(Movie movie) 
    {
        if (movie != null && movie.getPosterImageView() != null) {
            // Ensure the update happens on the JavaFX thread
            Platform.runLater(() -> {
                try {
                    Image posterImage = movie.getPosterImageView();
                    if (posterImage != null) {
                        moviePosterImageView.setImage(posterImage);
                        moviePosterImageView.setFitWidth(200);
                        moviePosterImageView.setFitHeight(300);
                        moviePosterImageView.setPreserveRatio(true);
                    } else {
                        moviePosterImageView.setImage(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            // Handle case where the poster image is null or movie is null
            moviePosterImageView.setImage(null);
        }
    }
    
    @FXML
    private void initialize() 
    {
        
        searchComboBox.getItems().addAll("Genre", "Partial Title", "Full Title");
        searchComboBox.setValue("Genre"); // Set default value to "Genre"

        // Bind the TreeTableColumn to the movie title property
        movies.setCellValueFactory(cellData -> cellData.getValue().getValue().titleProperty());

        // Set up the root node and hide it
        resultsTableView.setRoot(new TreeItem<Movie>(new Movie(0, "Hidden Root", null, "", "", "")));
        resultsTableView.setShowRoot(false);
        // Disable selection for the TreeTableView itself
        resultsTableView.getSelectionModel().setCellSelectionEnabled(false);

        // Add listener for row selection
        resultsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> handleMovieSelection());
        Platform.runLater(() -> {
        Stage stage = (Stage) MinimizeButton.getScene().getWindow();
        stage.setFullScreen(true);
        });
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
    private void handleSearchButtonAction() throws Exception 
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

        // Clear existing items
        TreeItem<Movie> root = resultsTableView.getRoot();
        root.getChildren().clear();

        // Add search results to the tree table
        if (moviesList.isEmpty()) 
        {
            showErrorDialog("No movies found.");
        } 
        else 
        {
            for (Movie movie : moviesList) 
            {
                TreeItem<Movie> treeItem = new TreeItem<>(movie);
                root.getChildren().add(treeItem);
            }
        }
    }


    @FXML
    private void handleMovieSelection() {
        Movie selectedMovie = resultsTableView.getSelectionModel().getSelectedItem().getValue();
        if (selectedMovie != null) {
            System.out.println("Selected movie: " + selectedMovie.getTitle()); // Debugging log
            movieTitleLabel.setText(selectedMovie.getTitle());
            movieGenreLabel.setText(selectedMovie.getGenre());
            movieSummaryLabel.setText(selectedMovie.getSummary());
            movieDurationLabel.setText(selectedMovie.getDuration() + " minutes");
            selectedMovieLabel.setText(selectedMovie.getTitle());
            updateMoviePoster(selectedMovie);  // This should update the poster image
        } else {
            System.out.println("No movie selected");
        }
    }

    

    @FXML
    private void handleConfirmSelection() throws IOException 
    {
        // Save the selected movie
        ShoppingCart cart = ShoppingCart.getInstance();
    
        // Get the selected TreeItem safely
        TreeItem<Movie> selectedTreeItem = resultsTableView.getSelectionModel().getSelectedItem();
    
        // Check if any item is selected
        if (selectedTreeItem != null && selectedTreeItem.getValue() != null) 
        {
            Movie selectedMovie = selectedTreeItem.getValue();
            cart.setSelectedMovie(selectedMovie);
    
            try 
            {
                // Retrieve the current stage from confirmButton
                Stage stage = (Stage) confirmButton.getScene().getWindow();
    
                // Create and configure the alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(stage);
                alert.initModality(Modality.WINDOW_MODAL);
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("Movie selected successfully!");
                alert.showAndWait();
            } catch (Exception e) 
            {
                e.printStackTrace();
            }
        } 
        else 
        {
            // Show an error dialog when no movie is selected
            showErrorDialog("No movie selected.");
        }
    }
    

    @FXML
    private void handleNextButtonAction() throws IOException 
    {
        ShoppingCart cart = ShoppingCart.getInstance();
        Movie selectedMovie = cart.getSelectedMovie();
        if(selectedMovie == null)
        {
            showErrorDialog("No movie selected.");
            return;
        }
        
        // Load the step2.fxml
        Parent step2Root = FXMLLoader.load(getClass().getResource("/help/fxml/step2.fxml"));

        // Get the current scene from the Next button
        Scene scene = next_button_step1.getScene();

        // Set the new root to the current scene
        scene.setRoot(step2Root);

        // Optionally, update the stage title if needed
        Stage stage = (Stage) next_button_step1.getScene().getWindow();
        stage.setTitle("Step 2");
        
        // Ensure the stage remains in fullscreen
        stage.setFullScreen(true);
        stage.setFullScreenExitHint(""); // Hide the exit hint
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

    
}
