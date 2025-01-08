package help;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import help.classes.ShoppingCart;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeTableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import help.classes.Movie;
import help.classes.SelectedSession;
import help.classes.Session;
import help.classes.Product;
import help.utilities.PriceDBO;
import help.utilities.ProductDBO;
import help.utilities.TicketsDBO;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;

import java.sql.SQLException;
import java.text.DecimalFormat;

public class Step4Controller {

    

    @FXML
    private Button next_button_step4;
    @FXML
    private Button back_button_step4;
    @FXML
    private Button AddProductToCart;
    @FXML
    private Button EmptyCartButton;
    @FXML
    private Button ApplyDiscount;

    @FXML
    private TextField DiscountEnter;
    @FXML
    private TextField NameEnter;
    @FXML
    private TextField SurnameEnter;

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
    private Label TotalPrice;
    @FXML
    private Label SeatPrices;
    @FXML
    private Label TotalTax;

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
    private PriceDBO priceDBO = new PriceDBO();
    private boolean isDiscountApplied = false;
    private int discountedSeatsCount = 0;

    // Add these constants at the class level
    private static final double TICKET_TAX_RATE = 0.20; // 20% tax for tickets
    private static final double PRODUCT_TAX_RATE = 0.10; // 10% tax for products

    // Add tax amount fields
    private double productTaxAmount = 0.0;
    private double ticketTaxAmount = 0.0;

    // Add getters/setters
    public double getProductTaxAmount() 
    {
        return productTaxAmount;
    }


    public double getTicketTaxAmount() {
        return ticketTaxAmount;
    }

    public double getTotalTaxAmount() {
        return productTaxAmount + ticketTaxAmount;
    }
    

    @FXML
    private void handleCloseButtonAction(ActionEvent event) 
    {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleMinimizeButtonAction(ActionEvent event) 
    {
        Stage stage = (Stage) MinimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void initialize() throws Exception 
    {
        // Get the selected movie
        ShoppingCart cart = ShoppingCart.getInstance();
        TicketsDBO ticketsDBO = new TicketsDBO();
        ticketsDBO.saveTicket(cart.getSelectedDaySessionAndHall().getSessionId(), 0, "John Doe", 25, 0.0, 0.0);
        TotalPrice.setText("$0.00");
        SeatPrices.setText("");
        TotalTax.setText("");

        if (next_button_step4 != null) next_button_step4.setDisable(true);
        if (AddProductToCart != null) AddProductToCart.setDisable(true);
        if (EmptyCartButton != null) EmptyCartButton.setDisable(true);
        if (ApplyDiscount != null) ApplyDiscount.setDisable(true);
        
        // Add listeners to text fields
        if (NameEnter != null && SurnameEnter != null) {
            NameEnter.textProperty().addListener((observable, oldValue, newValue) -> {
                validateNameSurnameFields();
            });
            
            SurnameEnter.textProperty().addListener((observable, oldValue, newValue) -> {
                validateNameSurnameFields();
            });
        }

        updateShoppingCartLabels();
        setupProductTable();
        loadProducts();

        setSeatPriceLabel();
        setselectedShoppingCart();
        /* updateSeatPrices();
        calculateTotal(); */

        getTotalSeatPrice();
        getsetTotalPrice();


        
    }
    private void setSeatPriceLabel()
    {
        try 
        {
            double totalSeatPrice = getTotalSeatPrice();
            SeatPrices.setText("Total Seat Price: $" + String.format("%.2f",totalSeatPrice));
        } 
        catch (Exception e) 
        {
            e.printStackTrace();

        }   
    }

    private void updateTaxLabel() 
    {
        double totalTax = getTotalTaxAmount();
        TotalTax.setText("$ " + String.format("%.2f", totalTax));
    }

    private double getTotalSeatPrice() throws Exception
    {
        ShoppingCart cart = ShoppingCart.getInstance();
        double totalSeatPrice = 0.0;

        List<String> seats = cart.getSelectedSeats();

        if (seats != null && !seats.isEmpty()) 
        {
            double seatPrice = priceDBO.getSeatPrice(cart.getSelectedDaySessionAndHall().getHall());
            int seatCount = seats.size();

            if(isDiscountApplied && discountedSeatsCount > 0)
            {
                int regularSeatsCount = seatCount - discountedSeatsCount;
                double regularSeatPrice = regularSeatsCount * seatPrice;
                
                double discountFactor = 1 - (priceDBO.getSeatDiscount(cart.getSelectedDaySessionAndHall().getHall()) / 100.0);                
                double discountedSeatPrice = discountedSeatsCount * seatPrice * discountFactor;

                totalSeatPrice = regularSeatPrice + discountedSeatPrice;
            }
            else
            {
                totalSeatPrice = seatPrice * seatCount;
            }
            
            // Add tax
            double basePrice = seatPrice * seatCount;
            ticketTaxAmount = basePrice * TICKET_TAX_RATE;
            totalSeatPrice = basePrice + ticketTaxAmount;
        } 
        else 
        {
            selectedSeat.setText("No seats selected");
        }
        System.out.println("Total Seat Price: " + totalSeatPrice);
        return totalSeatPrice;
    }

    private double getTotalProductPrice() throws Exception
    {
        ShoppingCart cart = ShoppingCart.getInstance();
        double totalProductPrice = 0.0;
        ObservableList<Product> cartProducts = FXCollections.observableArrayList(
            cart.getItemsBought().keySet().stream().collect(Collectors.toList())
        );
        for (Product product : cartProducts) 
        {
            int quantity = cart.getItemsBought().get(product);
            double basePrice = product.getPrice().doubleValue() * quantity;
            // Add tax
            productTaxAmount = basePrice * PRODUCT_TAX_RATE;
            totalProductPrice += basePrice + productTaxAmount;
        }
        System.out.println("Total Product Price: " + totalProductPrice);
        return totalProductPrice;
    }
    
    private void getsetTotalPrice() throws Exception
    {
        
        TotalPrice.setText("$" + (getTotalSeatPrice() + getTotalProductPrice()));
        updateTaxLabel();
    
    }

    @FXML
    private void handleAddProductToCartAction(ActionEvent event) throws Exception 
    {
        System.out.println("Add Product to Cart button clicked.");
        ObservableList<TreeItem<Product>> selectedItems = AdditionalProductTable.getSelectionModel().getSelectedItems();
    
        if (selectedItems.isEmpty()) 
        {
            showErrorDialog("No Selection");
            return;
        }
    
        ShoppingCart cart = ShoppingCart.getInstance();
    
        for (TreeItem<Product> item : selectedItems) 
        {
            Product product = item.getValue();
    
            // Get the quantity from the user using the new method
            int quantity = getQuantityFromUser(product);
    
            // If the quantity is invalid (e.g., -1), stop processing
            if (quantity == -1) 
            {
                continue; // Skip to the next product
            }
    
            // Add the product to the cart with the specified quantity
            cart.addItemBought(product, quantity);
    
            // Update stock in Product object
            product.setStockAvailability(product.getStockAvailability() - quantity);
    
            // Update the stock in the database
            try 
            {
                productDBO.updateProductStock(product.getName(), quantity);
                setupProductTable();
            } 
            catch (Exception e) 
            {
                showErrorDialog("Database Error");
                e.printStackTrace();
            }
    
            System.out.println("Added to cart: " + product.getName() + " - Quantity: " + quantity);
            showConfirmationDialog("Success");
        }

        getsetTotalPrice();
        setselectedShoppingCart();
    }
    

   

    private int getQuantityFromUser(Product product) 
    {
        // Retrieve the current stage from the main window (using the SignoutButton as an example)
        Stage stage = (Stage) SignoutButton.getScene().getWindow();
    
        // Create the TextInputDialog to capture quantity
        TextInputDialog dialog = new TextInputDialog("1"); // Default quantity is 1
        dialog.setTitle("Select Quantity");
        dialog.setHeaderText("Add " + product.getName() + " to Cart");
        dialog.setContentText("Enter the quantity:");
    
        // Set the dialog properties similar to your showConfirmationDialog method
        dialog.initOwner(stage); // Link the dialog to the current stage
        dialog.initModality(Modality.WINDOW_MODAL); // Make the dialog modal
        dialog.setResizable(true); // Allow resizing the dialog if needed
    
        // Show the dialog and get the input
        Optional<String> result = dialog.showAndWait();
    
        if (result.isPresent()) 
        {
            try 
            {
                int quantity = Integer.parseInt(result.get());
    
                // Validate the quantity
                if (quantity <= 0) {
                    showErrorDialog("Invalid Quantity");
                    return -1; // Invalid quantity
                }
    
                if (quantity > product.getStockAvailability()) {
                    showErrorDialog("Insufficient Stock");
                    return -1; // Insufficient stock
                }
    
                return quantity; // Valid quantity
            } 
            catch (NumberFormatException e) 
            {
                showErrorDialog("Invalid Input");
                return -1; // Invalid input
            }
        } 
        else 
        {
            return -1; // User canceled the dialog
        }
    }

    @FXML
    private void handleApplyDiscountAction(ActionEvent event) throws Exception 
    {
        if (isDiscountApplied) 
        {
            showErrorDialog("Discount already applied");
            return;
        }

        String discountNumber = DiscountEnter.getText();
        try 
        {
            int discount = Integer.parseInt(discountNumber);

            // Validate that the discount is a non-negative integer
            if (discount < 0) 
            {
                showErrorDialog("Please enter a non-negative integer for the discount.");
                return;
            }

            // Get the number of seats bought
            ShoppingCart cart = ShoppingCart.getInstance();
            int seatsBought = cart.getSelectedSeats().size();

            // Validate that the discount is not greater than the number of seats bought
            if (discount > seatsBought) 
            {
                showErrorDialog("Discount cannot be greater than the number of seats bought.");
                return;
            }

            discountedSeatsCount = discount;
            isDiscountApplied = true;
            getsetTotalPrice();

            NameEnter.setEditable(false);
            SurnameEnter.setEditable(false);
            DiscountEnter.setEditable(false);
            
            // Optional: Change the style to make it visually clear they're non-editable
            String nonEditableStyle = "-fx-background-color: #F0F0F0;";
            NameEnter.setStyle(nonEditableStyle);
            SurnameEnter.setStyle(nonEditableStyle);
            DiscountEnter.setStyle(nonEditableStyle);

            next_button_step4.setDisable(false);
            AddProductToCart.setDisable(false);
            EmptyCartButton.setDisable(false);
        } 
        catch (NumberFormatException e) 
        {
            showErrorDialog("Please enter a valid integer for the discount.");
        }
    }


    /* private double updateSeatPrices() throws Exception {
        ShoppingCart cart = ShoppingCart.getInstance();
        double totalSeatPrice = 0.0;
    
        // Check if there's a selected session and hall
        Session selectedSession = cart.getSelectedDaySessionAndHall();
        if (selectedSession != null) {
            // Fetch the seat price for the selected hall
            String selectedHall = selectedSession.getHall();
            PriceDBO priceDBO = new PriceDBO();
            double seatPrice = priceDBO.getSeatPrice(selectedHall); // Fetch seat price based on hall
    
            // Get the list of selected seats
            List<String> selectedSeats = cart.getSelectedSeats();
            StringBuilder shoppingCartText = new StringBuilder();
    
            if (selectedSeats != null && !selectedSeats.isEmpty()) {
                // Add seat details and calculate the number of seats
                int seatCount = selectedSeats.size(); // Number of seats
                totalSeatPrice = seatPrice * seatCount;
    
                // Format the price to two decimal places
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                shoppingCartText.append("Seats Bought: ").append(seatCount).append(" - $")
                        .append(decimalFormat.format(totalSeatPrice));
            } else {
                shoppingCartText.append("No seats selected").append("\n");
            }
    
            // Update the SeatPrices label to show the seat details
            SeatPrices.setText(shoppingCartText.toString());
        } else {
            SeatPrices.setText("No session selected");
        }
    
        return totalSeatPrice; // Return the total seat price
    } */
    

    private void updateShoppingCartLabels() 
    {
        ShoppingCart cart = ShoppingCart.getInstance();
    
        // Update Selected Movie Label
        Movie selectedMovie = cart.getSelectedMovie();
        if (selectedMovie != null) 
        {
            selectedMovieLabel.setText(selectedMovie.getTitle());
        } 
        else 
        {
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
        } 
        else 
        {
            selectedSeat.setText("No seats selected");
        }
    }
    

    @FXML
    private void handleNextButtonAction() throws IOException 
    {

        // Load the step2.fxml
        Parent step2Root = FXMLLoader.load(getClass().getResource("/help/fxml/final.fxml"));

        // Get the current scene from the Next button
        Scene scene = next_button_step4.getScene();

        // Set the new root to the current scene
        scene.setRoot(step2Root);

        // Optionally, update the stage title if needed
        Stage stage = (Stage) next_button_step4.getScene().getWindow();
        stage.setTitle("Step 5");
        
        // Ensure the stage remains in fullscreen
        stage.setFullScreen(true);
        stage.setFullScreenExitHint(""); // Hide the exit hint
    }

    @FXML
    private void handleBackButtonAction() throws Exception 
    {
        // Get the current cart and product list
        ShoppingCart cart = ShoppingCart.getInstance();
        
        // Loop through the cart and update stock for each product
        for (Product product : cart.getItemsBought().keySet()) {
            int quantityInCart = cart.getItemsBought().get(product);  // Get the quantity of each product in the cart
            
            // Update the stock in the database (we use the ProductDBO class)
            try {
                // Update product stock back to the database (increase by the quantity in the cart)
                productDBO.updateProductStock(product.getName(), -quantityInCart);
            } catch (SQLException e) {
                showErrorDialog("Database Error");
                e.printStackTrace();
            }
        }

        // Clear the shopping cart
        cart.clearSession();
        cart.clearSeats();

        // Load the previous step (step2.fxml)
        String fxmlFile = "/help/fxml/step2.fxml";
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = (Stage) back_button_step4.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true); // Ensure full screen
        stage.show();
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

    /**
     * Sets up the product table columns.
     */
    private void setupProductTable() 
    {
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
            AdditionalProductTable.setShowRoot(false); 
        } 
        catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Failed to load products from the database.");
        }
    }

    
    private void setselectedShoppingCart() throws Exception {
        ShoppingCart cart = ShoppingCart.getInstance();
        ObservableList<Product> cartProducts = FXCollections.observableArrayList(
            cart.getItemsBought().keySet().stream().collect(Collectors.toList())
        );
    
        // Update selectedShoppingCart Label
        if (cartProducts.isEmpty()) {
            selectedShoppingCart.setText("No products in cart.");
        } else {
            StringBuilder cartItems = new StringBuilder();
            for (Product product : cartProducts) {
                int quantity = cart.getItemsBought().get(product);
                cartItems.append(product.getName())
                        .append(" - $")
                        .append(String.format("%.2f", product.getPrice()))
                        .append(" x ")
                        .append(quantity)
                        .append("\n");
            }
            selectedShoppingCart.setText(cartItems.toString());
        }
    
        // Calculate and update SeatPrices Label
        System.out.println("Cart UI updated.");
    }
    

    /* private void calculateTotal() throws Exception 
    {
        ShoppingCart cart = ShoppingCart.getInstance();

        // Calculate total product cost
        double totalProductPrice = 0.0;
        ObservableList<Product> cartProducts = FXCollections.observableArrayList(
            cart.getItemsBought().keySet().stream().collect(Collectors.toList())
        );
        for (Product product : cartProducts) 
        {
            int quantity = cart.getItemsBought().get(product);
            totalProductPrice += product.getPrice().doubleValue() * quantity;
        }

        // Calculate total seat cost
        double totalSeatPrice = updateSeatPrices(); // Call the method to get seat price

        // Return the combined total price (products + seats)
        TotalPrice.setText("$" + totalProductPrice + totalSeatPrice); 
    } */

    


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

    private void showConfirmationDialog(String message) 
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
            alert.setContentText(message);
            alert.showAndWait();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    private void validateNameSurnameFields() 
    {
        // Check if both name and surname fields have valid input
        boolean hasValidInput = !NameEnter.getText().trim().isEmpty() 
                              && !SurnameEnter.getText().trim().isEmpty();
        
        // Enable/disable buttons based on input validity
        ApplyDiscount.setDisable(!hasValidInput);
        
        // Keep other buttons disabled until discount is applied
        next_button_step4.setDisable(true);
        AddProductToCart.setDisable(true);
        EmptyCartButton.setDisable(true);
    }
}
