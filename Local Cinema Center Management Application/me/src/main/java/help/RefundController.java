package help;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.math.BigDecimal;

import help.classes.TicketProduct;

/**
 * Controller class responsible for handling the logic related to processing refunds.
 * This includes initiating refund actions and updating the necessary records.
 */
public class RefundController {

    @FXML
    private Button signOutButton;
    
    @FXML
    private Button refundButton;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private ComboBox<String> typeOfRefundComboBox;
    
    @FXML
    private ComboBox<String> productNameComboBox;
    
    @FXML
    private TextField usernameTextField;
    
    @FXML
    private TextField transactionNumberTextField;
    
    @FXML
    private TextField ticketIdTextField;
    
    @FXML
    private Label refundLabel;
    
    @FXML
    private Label ticketIdLabel;
    
    @FXML
    private Label productNameLabel;
    
    @FXML
    private TableView<TicketProduct> refundTableView;
    
    @FXML
    private TableColumn<TicketProduct, Integer> idColumn;
    
    @FXML
    private TableColumn<TicketProduct, String> seatCostColumn;
    
    @FXML
    private TableColumn<TicketProduct, String> productColumn;
    
    @FXML
    private TableColumn<TicketProduct, Integer> quantityColumn;
    
    @FXML
    private TableColumn<TicketProduct, BigDecimal> productPriceColumn;
    
    @FXML
    private TableColumn<TicketProduct, BigDecimal> totalCostColumn;

    // Observable list for the table
    private ObservableList<TicketProduct> ticketProducts;

    /**
     * Handles the click event for the Refund button.
     * This method processes the refund logic.
     * 
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    private void handleRefundButtonClick(ActionEvent event) {
        // Logic for processing the refund
        System.out.println("Refund button clicked");
    }

    /**
     * Handles the click event for the Search button.
     * This method handles the logic for searching refund records.
     * 
     * @param event The ActionEvent triggered by the button click.
     */ 
   @FXML
    private void handleSearchButtonClick(ActionEvent event) {
        // Logic for searching refund records
        System.out.println("Search button clicked");

        // Example of adding a TicketProduct to the table
        TicketProduct product = new TicketProduct(1, "Product A", 2, new BigDecimal("15.00"));
        ticketProducts.add(product);
    }
    
    /**
     * Handles the click event for the Sign Out button.
     * This method handles the logic for signing out the user.
     * 
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    private void handleSignOutButtonClick(ActionEvent event) {
        // Logic for signing out
        System.out.println("Sign out button clicked");
    }

    /**
     * Initializes the controller. This method is called after the FXML file is loaded.
     * It initializes the ComboBoxes, TableView, and sets up the columns.
     */
    @FXML
    public void initialize() {
        // Initialize ComboBoxes or other UI elements
        typeOfRefundComboBox.getItems().addAll("Partial Refund", "Full Refund");
        productNameComboBox.getItems().addAll("Product 1", "Product 2", "Product 3");

        // Initialize TableView
        ticketProducts = FXCollections.observableArrayList();
        refundTableView.setItems(ticketProducts);

        // Set up the columns for the table
        idColumn.setCellValueFactory(cellData -> cellData.getValue().ticketIdProperty().asObject());
        productColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        productPriceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
    }
}
