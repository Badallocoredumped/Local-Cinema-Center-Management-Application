package help;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import help.classes.ShoppingCart;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import help.classes.Movie;
import help.classes.SelectedSession;
import help.classes.Session;
import help.classes.Product;
import help.utilities.ProductDBO;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.Alert;

import java.sql.SQLException;

public class Step4Controller {

    @FXML
    private Button next_button_step4;
    @FXML
    private Button back_button_step4;
    

    @FXML
    private Button SignoutButton;
    @FXML
    private Button CloseButton;
    @FXML
    private Button MinimizeButton; 


    @FXML
    private Label selectedShoppingCart;
    @FXML
    private Label selectedMovieLabel;
    @FXML
    private Label selectedSession;
    @FXML
    private Label selectedSeat;

    @FXML
    private TreeTableView<Product> AdditionalProductTable;

    @FXML
    private TreeTableColumn<Product, String> imageColumn;

    @FXML
    private TreeTableColumn<Product, String> nameColumn;

    @FXML
    private TreeTableColumn<Product, String> priceColumn;

    @FXML
    private TreeTableColumn<Product, String> stockAvailabilityColumn;

    private final ProductDBO productDBO = new ProductDBO();

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
    private void initialize() throws Exception 
    {
        // Get the selected movie
        ShoppingCart cart = ShoppingCart.getInstance();
        System.out.println("HELP" + cart.getSelectedSeats());
        
        updateShoppingCartLabels();
        setupProductTable();
        loadProducts();
    }

    private void updateShoppingCartLabels() {
        ShoppingCart cart = ShoppingCart.getInstance();
    
        // Update Selected Movie Label
        Movie selectedMovie = cart.getSelectedMovie();
        if (selectedMovie != null) {
            selectedMovieLabel.setText(selectedMovie.getTitle());
        } else {
            selectedMovieLabel.setText("No movie selected");
        }
    
        // Update Selected Session Label
        Session session = cart.getSelectedDaySessionAndHall();
        if (session != null) 
        {
            selectedSession.setText(session.getSession() + "\n" + session.getDay() + "\n" + session.getHall());
        } 
        else 
        {
            selectedSession.setText("No session selected");
        }
    
        // Update Selected Seats Label
        List<String> seats = cart.getSelectedSeats();
        if (seats != null && !seats.isEmpty()) 
        {
            StringBuilder seatsText = new StringBuilder();
            for (String seat : seats) 
            {
                if (seatsText.length() > 0) 
                {
                    seatsText.append(", ");
                }
                seatsText.append(seat);
            }
            selectedSeat.setText(seatsText.toString());
        } else {
            selectedSeat.setText("No seats selected");
        }
    }
    

    @FXML
    private void handleNextButtonAction() throws IOException 
    {
        Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/final.fxml"));
        Stage stage = (Stage) next_button_step4.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true); // Ensure full screen
        stage.show();
    }

    @FXML
    private void handleBackButtonAction() throws IOException 
    {
        // Clear the shopping cart
        ShoppingCart.getInstance().clear();

        String fxmlFile = "/help/fxml/step2.fxml";
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = (Stage) back_button_step4.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true); // Ensure full screen
        stage.show();
    }
    @FXML
    private void handleSignOutButtonAction(ActionEvent event) throws IOException 
    {
        // Load the login.fxml
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/help/fxml/login.fxml"));
        ShoppingCart cart = ShoppingCart.getInstance();
        cart.clear();

        // Get the current stage
        Stage stage = (Stage) SignoutButton.getScene().getWindow();

        // Set the new root to the current scene
        Scene scene = SignoutButton.getScene();
        scene.setRoot(loginRoot);

        // Update the stage title if needed
        stage.setTitle("Login");

        // Exit fullscreen mode
        stage.setFullScreen(false);
        stage.setFullScreenExitHint(""); // Hide the exit hint
    }

    /**
     * Sets up the product table columns.
     */
    private void setupProductTable() {
        imageColumn.setCellValueFactory(param -> param.getValue().getValue().imageProperty());
        nameColumn.setCellValueFactory(param -> param.getValue().getValue().nameProperty());
        priceColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getPrice().toString()));  // Convert BigDecimal to String
        stockAvailabilityColumn.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().getValue().getStockAvailability())));  // Convert int to String
    
        // Custom cell factory to display images
        imageColumn.setCellFactory(column -> new TreeTableCell<Product, String>() {
            private final ImageView imageView = new ImageView();
    
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    try {
                        // Convert the image path to a valid file URL
                        String imageUrl = "file:///" + item.replace("\\", "/");
    
                        // Load the image
                        Image image = new Image(imageUrl);
                        imageView.setImage(image);
                        imageView.setFitHeight(100); // Adjust image size as needed
                        imageView.setFitWidth(100);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(null);  // In case the image URL is invalid
                        System.err.println("Error loading image: " + e.getMessage());
                    }
                }
            }
        });
    }
    

    /**
     * Loads products from the database and populates the TreeTableView.
        * @throws Exception 
        */
    private void loadProducts() throws Exception 
    {
        try 
        {
            List<Product> products = productDBO.loadProducts();
            TreeItem<Product> root = new TreeItem<Product>(new Product());
            for (Product product : products) {
                root.getChildren().add(new TreeItem<>(product));
            }
            AdditionalProductTable.setRoot(root);
            AdditionalProductTable.setShowRoot(false); // Hide the root item
        } 
        catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Failed to load products from the database.");
        }
    }

    /**
     * Displays an error dialog with the specified message.
     *
     * @param message The error message to display.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
