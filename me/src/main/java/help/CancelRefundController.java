package help;


import java.sql.Statement;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import help.classes.Movie;
import help.classes.MovieService;
import help.classes.SelectedMovie;
import help.classes.ShoppingCart;
import help.classes.TicketProduct;
import help.classes.Tickets;
import help.classes.User;
import help.utilities.BankDBO;
import help.utilities.DataBaseHandler;
import help.utilities.ProductDBO;
import help.utilities.SeatsDBO;
import help.utilities.TicketProductsDBO;
import help.utilities.TicketsDBO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.ComboBoxTreeTableCell;

public class CancelRefundController 
{
    @FXML private Button MinimizeButton, CloseButton, SignoutButton, RefundButton;
    @FXML private TableView<Tickets> TicketTable;
    @FXML private TableColumn<Tickets, Integer> IdColumn;
    @FXML private TableColumn<Tickets, Double> SeatCostColumn;
    @FXML private TableColumn<Tickets, String> ProductColumns;
    @FXML private TableColumn<Tickets, Integer> QuantityColumn;
    @FXML private TableColumn<Tickets, Double> ProductPriceColumn;
    @FXML private TableColumn<Tickets, Double> TotalCostColumn;
    @FXML private TableColumn<Tickets, String> StatusColumn;

    
    @FXML private ComboBox<String> TypeOfRefund;
    @FXML private TextField TicketIdText;
    @FXML private TextField ProductNameText;

    private TicketsDBO ticketService = new TicketsDBO();  // Assuming TicketService is your helper class.
    private TicketProductsDBO ticketProductsDBO = new TicketProductsDBO();


    @FXML
    public void initialize() throws Exception 
    {
        setupTableColumns();
        setupComboBox();
        setupTableSelection();
        loadTicketData();
    }

    private void setupTableColumns() throws Exception 
    {
        RefundButton.setDisable(true);

        // Add selection listener to table
        TicketTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Enable refund button only if ticket is not cancelled
                boolean isCancelled = "CANCELLED".equals(newSelection.getStatus());
                RefundButton.setDisable(isCancelled);
                TypeOfRefund.setDisable(isCancelled);
                
                // Auto-populate ticket ID field
                TicketIdText.setText(String.valueOf(newSelection.getTicketId()));
                
                if (isCancelled) {
                    showAlert(Alert.AlertType.WARNING, "This ticket has already been cancelled", "Warning");
                }
            } else {
                RefundButton.setDisable(true);
                TypeOfRefund.setDisable(true);
                TicketIdText.clear();
            }
        });

        // Clear existing factory
        IdColumn.setCellValueFactory(null);
        SeatCostColumn.setCellValueFactory(null);
        ProductColumns.setCellValueFactory(null);
        QuantityColumn.setCellValueFactory(null);
        ProductPriceColumn.setCellValueFactory(null);
        TotalCostColumn.setCellValueFactory(null);
    
        ObservableList<TicketProduct> ticketProducts = fetchTicketProducts();

        // Set new factory
        IdColumn.setCellValueFactory(new PropertyValueFactory<>("ticketId"));
        SeatCostColumn.setCellValueFactory(new PropertyValueFactory<>("totalSeatCost"));
        ProductColumns.setCellValueFactory(cellData -> {
            try {
                ObservableList<TicketProduct> products = ticketProductsDBO.getTicketProducts(cellData.getValue().getTicketId());
                String names = products.stream()
                    .map(TicketProduct::getProductName)
                    .collect(Collectors.joining(", "));
                return new SimpleStringProperty(names);
            } catch (SQLException e) {
                return new SimpleStringProperty("");
            } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                                    return null;
        });
        QuantityColumn.setCellValueFactory(cellData -> {
            try {
                ObservableList<TicketProduct> products = ticketProductsDBO.getTicketProducts(cellData.getValue().getTicketId());
                
                // Create a string with quantities joined by commas
                String quantities = products.stream()
                    .map(p -> String.valueOf(p.getQuantity()))
                    .collect(Collectors.joining(", "));
        
                // Return a StringProperty containing the quantities
                int totalQuantity = products.stream()
                    .mapToInt(TicketProduct::getQuantity)
                    .sum();
                return new SimpleIntegerProperty(totalQuantity).asObject();
            } catch (Exception e) {
                return new SimpleIntegerProperty(0).asObject(); // In case of an error, return 0
            }
        });

        StatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    
        // Optional: Custom cell factory to style different statuses
        StatusColumn.setCellFactory(column -> new TableCell<Tickets, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.equals("CANCELLED")) {
                        setStyle("-fx-text-fill: red;");
                    } else if (status.equals("ACTIVE")) {
                        setStyle("-fx-text-fill: green;");
                    }
                }
            }
        });
        
        ProductPriceColumn.setCellValueFactory(cellData -> {
            try {
                ObservableList<TicketProduct> products = ticketProductsDBO.getTicketProducts(cellData.getValue().getTicketId());
                Double totalPrice = products.stream()
                    .mapToDouble(p -> p.getPrice().doubleValue() * p.getQuantity())
                    .sum();
                return new SimpleDoubleProperty(totalPrice).asObject();
            } catch (SQLException e) {
                return new SimpleDoubleProperty(0.0).asObject();
            } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                                    return null;
        });
        TotalCostColumn.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        // TicketTable.setItems(ticketProducts);

        //loadTicketData();
    }

    private void setupComboBox() 
    {
        TypeOfRefund.setItems(FXCollections.observableArrayList(
            "Full Refund",
            "Product Refund",
            "Seat Refund"
        ));
    }

    public ObservableList<TicketProduct> fetchTicketProducts() throws Exception 
    {
        ObservableList<TicketProduct> ticketProducts = FXCollections.observableArrayList();

        // SQL query to join Tickets and Ticket_Products tables
        String query = "SELECT t.ticket_id, t.session_id, t.total_seat_cost, t.total_product_cost, " +
                    "t.total_tax, t.total_cost, tp.product_name, tp.quantity, tp.price " +
                    "FROM Tickets t " +
                    "JOIN Ticket_Products tp ON t.ticket_id = tp.ticket_id";

        try (Connection conn = DataBaseHandler.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Extract ticket and product information from result set
                int ticketId = rs.getInt("ticket_id");
                int sessionId = rs.getInt("session_id");
                BigDecimal totalSeatCost = rs.getBigDecimal("total_seat_cost");
                BigDecimal totalProductCost = rs.getBigDecimal("total_product_cost");
                BigDecimal totalTax = rs.getBigDecimal("total_tax");
                BigDecimal totalCost = rs.getBigDecimal("total_cost");

                // Product details
                String productName = rs.getString("product_name");
                int quantity = rs.getInt("quantity");
                BigDecimal price = rs.getBigDecimal("price");

                // Create new TicketProduct object (representing a product for a specific ticket)
                TicketProduct ticketProduct = new TicketProduct(ticketId, productName, quantity, price);

                // Set additional ticket information (optional)
/*                 ticketProduct.setSessionId(sessionId);
                ticketProduct.setTotalSeatCost(totalSeatCost);
                ticketProduct.setTotalProductCost(totalProductCost);
                ticketProduct.setTotalTax(totalTax);
                ticketProduct.setTotalCost(totalCost); */

                // Add to ObservableList
                ticketProducts.add(ticketProduct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ticketProducts;
    }
    private void setupTableSelection() {
        // Disable refund button initially
        RefundButton.setDisable(true);
    
        // Add selection listener to table
        TicketTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Enable refund button when ticket is selected
                RefundButton.setDisable(false);
                // Auto-populate ticket ID field
                TicketIdText.setText(String.valueOf(newSelection.getTicketId()));
            } else {
                RefundButton.setDisable(true);
                TicketIdText.clear();
            }
        });
    }

    
    private void loadTicketData() {
        try {
            TicketsDBO ticketsDBO = new TicketsDBO();
            ObservableList<Tickets> tickets = ticketsDBO.getTickets();
            TicketTable.setItems(tickets);  // Populate the TableView with the tickets
        } catch (Exception e) {
            e.printStackTrace();  // Handle any exceptions properly in real code
        }
    }
    

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



























    

    @FXML
    private void handleSearch() {
        String ticketId = TicketIdText.getText().trim();
        if (!ticketId.isEmpty()) {
            TicketTable.getItems().stream()
                .filter(ticket -> String.valueOf(ticket.getTicketId()).equals(ticketId))
                .findFirst()
                .ifPresent(ticket -> TicketTable.getSelectionModel().select(ticket));
        }
    }

    
    @FXML
    private void handleSearchButton() {
        String ticketId = TicketIdText.getText();
        // Implement search logic
    }

    @FXML
    private void handleRefundSelection(ActionEvent event) 
    {
        ProductDBO productDBO = new ProductDBO();
        SeatsDBO seatDBO = new SeatsDBO();

        Tickets selectedTicket = TicketTable.getSelectionModel().getSelectedItem();
        if (selectedTicket != null && !"CANCELLED".equals(selectedTicket.getStatus())) {
            String refundType = TypeOfRefund.getValue();
            try {
                BankDBO bankDBO = new BankDBO();
                boolean success = false;
                
                switch(refundType) {
                    case "Full Refund":
                        success = bankDBO.processFullRefund(
                            selectedTicket.getTotalSeatCost(), 
                            selectedTicket.getTotalProductCost()
                        );
                        if (success) {
                            productDBO.returnProductsToInventory(selectedTicket.getTicketId());
                            seatDBO.updateSeatOccupancy(selectedTicket.getTicketId(), false);
                        }
                        break;
                        
                    case "Product Refund":
                        success = bankDBO.processProductRefund(
                            selectedTicket.getTotalProductCost()
                        );
                        if (success) {
                            productDBO.returnProductsToInventory(selectedTicket.getTicketId());
                        }
                        break;
                        
                        
                    case "Seat Refund":
                        success = bankDBO.processSeatRefund(
                            selectedTicket.getTotalSeatCost()
                        );
                        if (success) {
                            seatDBO.updateSeatOccupancy(selectedTicket.getTicketId(), false);
                        }
                        break;
                        
                }
                
                if (success) {
                    ticketService.cancelTicket(selectedTicket.getTicketId());
                    loadTicketData();
                    showAlert(Alert.AlertType.INFORMATION, "Refund processed successfully", "Success");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Failed to process refund", "Error");
                e.printStackTrace();
            }
        }
    }

    

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
    private void handleSignOutButtonAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/login.fxml"));
            Scene scene = new Scene(root, 600, 400);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSchedule(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/Schedule.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onOrganizeMovie(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/OrganizeMovie.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}