package help;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.ByteArrayInputStream;

import help.classes.ShoppingCart;
import help.classes.Tickets;
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
import javafx.scene.control.TableColumn;
import javafx.stage.Modality;
import javafx.stage.Stage;
import help.classes.Movie;
import help.classes.SelectedSession;
import help.classes.Session;
import help.classes.Product;
import help.utilities.DataBaseHandler;
import help.utilities.PriceDBO;
import help.utilities.ProductDBO;
import help.utilities.TicketProductsDBO;
import help.utilities.TicketsDBO;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
    private Label SeatDiscountedPrice;

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

    @FXML
    private TreeTableColumn<Product, String> CategoryColumn;



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

    private String customerName;
    private String customerSurname;

    public double getProductTaxAmount() 
    {
        return productTaxAmount;
    }


    public double getTicketTaxAmount() 
    {
        return ticketTaxAmount;
    }

    public double getTotalTaxAmount() 
    {
        System.out.println("Product Tax: " + productTaxAmount);
        System.out.println("Ticket Tax: " + ticketTaxAmount);
        return productTaxAmount + ticketTaxAmount;
    }

    private double calculateSeatTax(double basePrice) 
    {
        return basePrice * TICKET_TAX_RATE;
    }
    
    private double calculateProductTax(double basePrice) 
    {
        return basePrice * PRODUCT_TAX_RATE;
    }

    private double calculateDiscountSavings(double originalPrice, double discountedPrice) 
    {
        return originalPrice - discountedPrice;
    }
    
    private void updateDiscountSavingsLabel(int seatCount, double seatPrice, double discountedTotal) 
    {
        double originalTotal = seatCount * seatPrice;
        double savings = calculateDiscountSavings(originalTotal, discountedTotal);
        SeatDiscountedPrice.setText("Savings: $" + String.format("%.2f", savings));
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
        //ticketsDBO.saveTicket(cart.getSelectedDaySessionAndHall().getSessionId(), 0, "John Doe", 25, 0.0, 0.0);
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
            updateTaxLabel();
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
        double baseSeatPrice = 0.0;
    
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
                baseSeatPrice = regularSeatPrice + discountedSeatPrice;
                updateDiscountSavingsLabel(seatCount, seatPrice, baseSeatPrice);
            } 
            else 
            {
                baseSeatPrice = seatPrice * seatCount;
                SeatDiscountedPrice.setText("No discount applied");
            }
            
            // Calculate tax separately
            ticketTaxAmount = calculateSeatTax(baseSeatPrice);
            return baseSeatPrice;
        } 
        return 0.0;
    }

    private double getTotalProductPrice() throws Exception 
    {
        ShoppingCart cart = ShoppingCart.getInstance();
        double baseProductPrice = 0.0;
        ObservableList<Product> cartProducts = FXCollections.observableArrayList(
            cart.getItemsBought().keySet().stream().collect(Collectors.toList())
        );
        
        for (Product product : cartProducts) 
        {
            int quantity = cart.getItemsBought().get(product);
            baseProductPrice += product.getPrice().doubleValue() * quantity;
        }
        
        // Calculate tax separately
        productTaxAmount = calculateProductTax(baseProductPrice);
        return baseProductPrice;
    }
    private void getsetTotalPrice() throws Exception
    {
        
        updateTaxLabel();
        TotalPrice.setText("$" + (getTotalSeatPrice() + getTotalProductPrice() + getTotalTaxAmount()));
    
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
            int newStock = product.getStockAvailability() - quantity;
            product.setStockAvailability(newStock);
    
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
        updateTaxLabel();
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
        customerName = NameEnter.getText().trim();
        customerSurname = SurnameEnter.getText().trim();

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
            updateTaxLabel();

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
            
            calculateSeatTax(seatsBought);
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
    private void handleNextButtonAction() throws Exception {
        
            // Get existing instances
            ShoppingCart cart = ShoppingCart.getInstance();
            Tickets ticket = Tickets.getInstance();
            
            // Set ticket data
            ticket.setSessionId(cart.getSelectedDaySessionAndHall().getSessionId());
            ticket.setSeatNumbers(new ArrayList<>(cart.getSelectedSeats()));
            ticket.setCustomerName(customerName + " " + customerSurname);
            ticket.setTotalSeatCost(getTotalSeatPrice());
            ticket.setTotalProductCost(getTotalProductPrice());
            ticket.setTotalTax(getTotalTaxAmount());
            ticket.setTotalCost(getTotalSeatPrice() + getTotalProductPrice() + getTotalTaxAmount());
            ticket.setDiscountedSeatNumber(discountedSeatsCount);

            // Debug prints
            System.out.println("Step4 - Before Scene Change:");
            System.out.println("Customer Name: " + ticket.getCustomerName());
            System.out.println("Seat Numbers: " + ticket.getSeatNumbers());
            System.out.println("Total Cost: " + ticket.getTotalCost());

            // Save to database
            TicketsDBO ticketsDBO = new TicketsDBO();
            int ticketId = ticketsDBO.createTicket(ticket);
            ticket.setTicketId(ticketId);
            if (!cart.getItemsBought().isEmpty()) 
            {
                TicketProductsDBO ticketProductsDBO = new TicketProductsDBO();
                ticketProductsDBO.saveTicketProducts(ticketId, cart.getItemsBought());
            }

            // Load next scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/help/fxml/step5.fxml"));
            Parent step5Root = loader.load();
            Scene scene = next_button_step4.getScene();

            if (scene == null) 
            {
                scene = next_button_step4.getParent().getScene(); // This will fetch the scene from the parent of the button
            }
            if (scene != null) 
            {
                // Set the new root for the next scene
                scene.setRoot(step5Root);
                Stage stage = (Stage) scene.getWindow();
                stage.setTitle("Step 5");

                // Ensure the stage remains in fullscreen or add any other stage settings
                stage.setFullScreen(true);
                stage.setFullScreenExitHint(""); // Hide the exit hint if needed
            } 
            else 
            {
                // Handle the case when the scene is still null (very rare but might happen during initialization)
                System.err.println("Error: Scene not found. Could not change scene.");
            }
        
    }
    @FXML
    private void handleBackButtonAction() throws Exception 
    {
        ShoppingCart cart = ShoppingCart.getInstance();
        ProductDBO productDBO = new ProductDBO();
        
        // Get the list of seats to unmark
        List<String> seatsToUnmark = cart.getSelectedSeats();
        
        // Return products to inventory
        for (Map.Entry<Product, Integer> entry : cart.getItemsBought().entrySet()) 
        {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            productDBO.returnProductStock(product.getName(), quantity);
        }
        // Unmark seats in database if there are any selected seats
        if (!seatsToUnmark.isEmpty()) 
        {
            try 
            {
                // Create SQL queries
                String unmarkSeatQuery = "UPDATE Seats SET is_occupied = FALSE WHERE seat_label = ? AND session_id = ?";
                String updateVacantSeatsQuery = "UPDATE Sessions SET vacant_seats = vacant_seats + ? WHERE session_id = ?";
    
                try (Connection conn = DataBaseHandler.getConnection();
                     PreparedStatement unmarkSeatStmt = conn.prepareStatement(unmarkSeatQuery);
                     PreparedStatement updateVacantSeatsStmt = conn.prepareStatement(updateVacantSeatsQuery)) {
                    
                    int sessionId = cart.getSelectedDaySessionAndHall().getSessionId();
    
                    // Unmark each seat as available
                    for (String seatLabel : seatsToUnmark) 
                    {
                        unmarkSeatStmt.setString(1, seatLabel);
                        unmarkSeatStmt.setInt(2, sessionId);
                        unmarkSeatStmt.addBatch();
                    }
    
                    // Execute batch to update all seats
                    unmarkSeatStmt.executeBatch();
    
                    // Update vacant seats count
                    updateVacantSeatsStmt.setInt(1, seatsToUnmark.size());
                    updateVacantSeatsStmt.setInt(2, sessionId);
                    updateVacantSeatsStmt.executeUpdate();
                }
            } 
            catch (SQLException e) 
            {
                e.printStackTrace();
                throw e;
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

    private Image byteArrayToImage(byte[] imageData) 
    {
        if (imageData == null || imageData.length == 0) return null;
        try {
            return new Image(new ByteArrayInputStream(imageData));
        } catch (Exception e) {
            System.err.println("Error converting image data: " + e.getMessage());
            return null;
        }
    }

    /**
     * Sets up the product table columns.
     */
    private void setupProductTable() 
    {
        //imageColumn.setCellValueFactory(param -> param.getValue().getValue().imageProperty());
        nameColumn.setCellValueFactory(param -> param.getValue().getValue().nameProperty());
        priceColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getPrice().toString()));  // Convert BigDecimal to String
        stockAvailabilityColumn.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().getValue().getStockAvailability())));  // Convert int to String
        CategoryColumn.setCellValueFactory(param -> param.getValue().getValue().categoryProperty());

        // Custom cell factory to display images
        // Custom cell factory for image column
        imageColumn.setCellFactory(column -> new TreeTableCell<Product, String>() {
        private final ImageView imageView = new ImageView();

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
            } else {
                Product product = getTreeTableRow().getItem();
                if (product != null && product.getImageData() != null) {
                    try {
                        // Convert BLOB data to Image
                        ByteArrayInputStream bis = new ByteArrayInputStream(product.getImageData());
                        Image image = new Image(bis);
                        
                        // Configure ImageView
                        imageView.setImage(image);
                        imageView.setFitHeight(250);  // Increased from 50 to 100
                        imageView.setFitWidth(250);   // Increased from 50 to 100
                        imageView.setPreserveRatio(true);
                        
                        setGraphic(imageView);
                    } catch (Exception e) {
                        System.err.println("Error loading image: " + e.getMessage());
                        setGraphic(null);
                    }
                } else {
                    setGraphic(null);
                }
            }
        }
    });

    // Set cell value factory for image column
    imageColumn.setCellValueFactory(param -> new SimpleStringProperty(""));  // Dummy value, actual image is handled in cell factory

    
 
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
