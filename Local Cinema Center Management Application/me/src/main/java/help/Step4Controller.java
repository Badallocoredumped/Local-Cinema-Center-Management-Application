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
import help.utilities.SeatsDBO;
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

/**
 * Controller class responsible for handling the actions of Step 4 in the cashier multi-step process.
 */
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

    
    
    /**
     * Returns the amount of tax applied to products.
     * 
     * @return The product tax amount.
     */
    public double getProductTaxAmount() 
    {
        return productTaxAmount;
    }

    /**
     * Returns the amount of tax applied to tickets.
     * 
     * @return The ticket tax amount.
     */
    public double getTicketTaxAmount() 
    {
        return ticketTaxAmount;
    }

    /**
     * Returns the total tax amount for both products and tickets.
     * 
     * This method calculates the total tax by summing up the product tax and ticket tax amounts.
     * It also prints the individual tax amounts for debugging purposes.
     * 
     * @return The total tax amount (product tax + ticket tax).
     */
    public double getTotalTaxAmount() 
    {
        System.out.println("Product Tax: " + productTaxAmount);
        System.out.println("Ticket Tax: " + ticketTaxAmount);
        return productTaxAmount + ticketTaxAmount;
    }

    /**
    * Calculates the tax amount for a seat based on the base price and a predefined tax rate.
    * 
    * @param basePrice The base price of the seat before tax.
    * @return The calculated seat tax amount.
    */
    private double calculateSeatTax(double basePrice) 
    {
        return basePrice * TICKET_TAX_RATE;
    }
    
    /**
     * Calculates the tax amount for a product based on the base price and a predefined tax rate.
     * 
     * @param basePrice The base price of the product before tax.
     * @return The calculated product tax amount.
     */
    private double calculateProductTax(double basePrice) 
    {
        return basePrice * PRODUCT_TAX_RATE;
    }

    /**
     * Calculates the savings from applying a discount to the original price.
     * 
     * @param originalPrice The original price before the discount.
     * @param discountedPrice The price after the discount has been applied.
     * @return The amount saved by applying the discount.
     */
    private double calculateDiscountSavings(double originalPrice, double discountedPrice) 
    {
        return originalPrice - discountedPrice;
    }
    
    /**
     * Updates the label showing the savings from a discount on the selected seats.
     * 
     * This method calculates the original total price, the savings, and updates the UI label to display the savings.
     * 
     * @param seatCount The number of seats selected.
     * @param seatPrice The price of a single seat.
     * @param discountedTotal The total price after discount is applied.
     */
    private void updateDiscountSavingsLabel(int seatCount, double seatPrice, double discountedTotal) 
    {
        double originalTotal = seatCount * seatPrice;
        double savings = calculateDiscountSavings(originalTotal, discountedTotal);
        SeatDiscountedPrice.setText("Savings: $" + String.format("%.2f", savings));
    }

    
    

    /**
     * Handles the action when the "Close" button is clicked. This method closes the current window.
     * 
     * @param event The action event triggered by the "Close" button.
     */
    @FXML
    private void handleCloseButtonAction(ActionEvent event) 
    {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the action when the "Minimize" button is clicked. This method minimizes the current window.
     * 
     * @param event The action event triggered by the "Minimize" button.
     */
    @FXML
    private void handleMinimizeButtonAction(ActionEvent event) 
    {
        Stage stage = (Stage) MinimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Initializes the components of the current view. This method is called when the controller is loaded.
     * It performs the following tasks:
     * - Updates the shopping cart labels.
     * - Sets up the product table.
     * - Loads the available products.
     * - Sets seat price and updates selected shopping cart.
     * 
     * @throws Exception if an error occurs while initializing the components.
     */
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
        //if (ApplyDiscount != null) ApplyDiscount.setDisable(true);
        


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

    /**
     * Sets the label displaying the total seat price. This method also updates the tax label based on the current seat price.
     * 
     * @throws Exception if an error occurs while fetching the seat price.
     */
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

    /**
     * Updates the label displaying the total tax amount based on the current product and ticket taxes.
     */
    private void updateTaxLabel() 
    {

        double totalTax = getTotalTaxAmount();
        TotalTax.setText("$ " + String.format("%.2f", totalTax));
    }

    /**
     * Calculates the total seat price, including any discounts applied to the selected seats.
     * The method also calculates and sets the ticket tax amount.
     * 
     * @return The total seat price after applying any discounts.
     * @throws Exception if an error occurs while calculating the seat price or fetching required data.
     */
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

    /**
     * Calculates the total price of the products in the shopping cart, including their quantities.
     * This method calculates the base price of the products and also calculates tax separately.
     * 
     * @return The total price of the products in the cart, excluding tax.
     * @throws Exception if an error occurs while calculating the product price.
     */
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

    /**
     * Updates the total price label by calculating the sum of the total seat price, total product price, and total tax.
     * This method also updates the tax label.
     * 
     * @throws Exception if an error occurs while calculating the prices.
     */
    private void getsetTotalPrice() throws Exception
    {
        
        updateTaxLabel();
        TotalPrice.setText("$" + (getTotalSeatPrice() + getTotalProductPrice() + getTotalTaxAmount()));
    
    }

    /**
     * Handles the action when the "Add Product to Cart" button is clicked. This method performs the following actions:
     * - Retrieves the selected products from the product table.
     * - Prompts the user for a quantity to add to the cart.
     * - Updates the product stock in the database.
     * - Adds the selected products and their quantities to the cart.
     * - Updates the total price and tax labels.
     * 
     * @param event The action event triggered by clicking the "Add Product to Cart" button.
     * @throws Exception if an error occurs while adding the product to the cart or updating the database.
     */
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
    

   

    /**
     * Prompts the user to enter a quantity for the specified product.
     * This method validates the input and ensures that the quantity is valid and available in stock.
     * 
     * @param product The product for which the quantity is being requested.
     * @return The quantity entered by the user, or -1 if the quantity is invalid or the user cancels.
     */
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

    /**
     * Handles the action when the "Apply Discount" button is clicked. This method performs the following actions:
     * - Validates the customer's name and surname.
     * - Checks if a discount has already been applied.
     * - Validates and applies the discount based on the number of seats bought.
     * - Updates the total price and tax labels.
     * 
     * @param event The action event triggered by clicking the "Apply Discount" button.
     * @throws Exception if an error occurs while applying the discount or calculating the prices.
     */
    @FXML
    private void handleApplyDiscountAction(ActionEvent event) throws Exception 
    {
        customerName = NameEnter.getText().trim();
        customerSurname = SurnameEnter.getText().trim();
        if (!validateCustomerName(customerName, customerSurname)) {
            return;
        }

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
    

    /**
     * Updates the shopping cart labels for the selected movie, session, and seats.
     * This method checks if the cart contains the selected movie, session, and seats and updates
     * the corresponding labels accordingly. If no selection is made, it sets a default message.
     */
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
    


    /**
     * Handles the action when the "Next" button is clicked. This method performs the following:
     * - Sets the ticket details based on the shopping cart's selected movie, session, seats, and products.
     * - Calculates the total costs (seats, products, tax, and discount).
     * - Saves the ticket information to the database.
     * - Loads the next scene (Step 5) for the next stage of the process.
     * 
     * @throws Exception if an error occurs during the ticket creation, database interaction, or scene loading.
     */
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
            ticket.setStatus("ACTIVE");

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

    /**
     * Handles the action when the "Back" button is clicked. This method performs the following:
     * - Returns the selected products' stock to the inventory.
     * - Unmarks the selected seats and updates the session's vacant seat count in the database.
     * - Clears the shopping cart's session and selected seats.
     * - Loads the previous step (Step 2) scene.
     * 
     * @throws SQLException if an error occurs during the database interaction.
     */
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

    /**
     * Validates a given name for required conditions. It checks if the name is not empty, 
     * has a minimum length of 2 characters, a maximum length of 50 characters, and contains 
     * only letters, spaces, and hyphens.
     * 
     * @param name The name to be validated.
     * @param fieldName The name of the field being validated (used in error messages).
     * @return true if the name is valid, false otherwise.
     */
    private boolean isValidName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", fieldName + " cannot be empty");
            return false;
        }
    
        if (name.trim().length() < 2) {
            showAlert(Alert.AlertType.ERROR, "Error", fieldName + " must be at least 2 characters long");
            return false;
        }
    
        if (name.trim().length() > 50) {
            showAlert(Alert.AlertType.ERROR, "Error", fieldName + " cannot exceed 50 characters");
            return false;
        }
    
        if (!name.matches("^[A-Za-z\\s-]+$")) {
            showAlert(Alert.AlertType.ERROR, "Error", fieldName + " can only contain letters, spaces, and hyphens");
            return false;
        }
    
        return true;
    }

    /**
     * Displays an alert with a specific type, title, and message.
     * 
     * @param alertType The type of alert (e.g., ERROR, INFORMATION).
     * @param title The title of the alert.
     * @param message The message to be displayed in the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Handles the action of signing out, including unmarking selected seats, returning products to the inventory,
     * clearing the shopping cart, and navigating to the login screen.
     * 
     * @param event The ActionEvent triggered by the sign-out button.
     */
    @FXML
    private void handleSignOutButtonAction(ActionEvent event) {
        try {
            // Get instances
            ShoppingCart cart = ShoppingCart.getInstance();
            Tickets ticket = Tickets.getInstance();
            
            // Return seats to available pool
            SeatsDBO seatsDBO = new SeatsDBO();
            Session session = cart.getSelectedDaySessionAndHall();

            List<String> seatsToUnmark = cart.getSelectedSeats();
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

            // Return products to inventory
            ProductDBO productDBO = new ProductDBO();
            for (Map.Entry<Product, Integer> entry : cart.getItemsBought().entrySet()) 
            {
                Product product = entry.getKey();
                Integer quantity = entry.getValue();
                productDBO.returnProductStock(product.getName(), quantity);
            }

            // Reset instances
            cart.clear();
            Tickets.resetInstance();

            // Navigate to login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/help/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) SignoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(false); 
            stage.show();



        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Sign Out Failed");
            alert.setHeaderText("Error During Sign Out");
            alert.setContentText("Failed to properly sign out: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    

    /**
     * Sets up the product table columns and custom cell factory for images.
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
     * Loads products from the database and populates the TreeTableView with product data.
     * 
     * @throws Exception If an error occurs while loading the products.
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

    
    /**
     * Sets the selected products in the shopping cart to be displayed in the UI.
     * 
     * @throws Exception If an error occurs while updating the shopping cart display.
     */
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

    


    /**
     * Shows an error dialog with a specified error message.
     * 
     * @param errorMessage The error message to be displayed in the dialog.
     */
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

    /**
     * Shows a confirmation dialog with a specified message.
     * 
     * @param message The message to be displayed in the confirmation dialog.
     */
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

    /**
     * Validates the customer's name and surname to ensure they are not empty, too short or too long, 
     * and contain only valid characters (including Turkish characters).
     * 
     * @param name The first name of the customer.
     * @param surname The last name of the customer.
     * @return true if the name and surname are valid, false otherwise.
     */
    private boolean validateCustomerName(String name, String surname) 
    {
        // Empty check
        if (name == null || name.trim().isEmpty() || 
            surname == null || surname.trim().isEmpty()) {
                showErrorDialog("Error Invalid Input");
            return false;
        }
    
        // Length check
        if (name.trim().length() < 2 || surname.trim().length() < 2) {
            showErrorDialog("Error Invalid Input");
            return false;
        }
    
        if (name.trim().length() > 50 || surname.trim().length() > 50) {
            showErrorDialog("Error Invalid Input");
            return false;
        }
    
        // Turkish character validation pattern
        String namePattern = "^[A-Za-zğĞıİöÖüÜşŞçÇ\\s-]+$";
        if (!name.matches(namePattern) || !surname.matches(namePattern)) {
            showErrorDialog("Name and surname can only contain letters (including Turkish characters), spaces, and hyphens");
            return false;
        }
    
        return true;
    }
}
