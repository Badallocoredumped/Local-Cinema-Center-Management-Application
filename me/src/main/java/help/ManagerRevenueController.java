package help;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.chart.XYChart;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import help.utilities.BankDBO;
import help.utilities.DataBaseHandler;

/**
 * Controller class responsible for managing revenue-related operations in the system.
 * This class provides the functionality for viewing and displaying revenue details.
 */
public class ManagerRevenueController {

    @FXML private Button Product_Inventory_Go;
    @FXML private Button Personnel_Go;
    @FXML private Button Price_Management_Go;
    @FXML private Button Revenue_Tax_Go;
    @FXML private Button CloseButton;
    @FXML private Button MinimizeButton;
    @FXML private Button SignoutButton;

    
    @FXML private Label TotalRevenue;
    @FXML private Label TaxAmount;
    
    @FXML private AreaChart<String, Number> RevenueChart;
    @FXML private AreaChart<String, Number> TaxChart;

    

    private BankDBO bankDBO = new BankDBO();

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
    

    /**
     * Initializes the manager revenue controller by setting up initial labels, 
     * enabling/disabling relevant buttons, and preparing the view.
     */
    @FXML
    public void initialize() {
        System.out.println("ManagerRevenueController initialized");
        updateRevenueAndTaxLabels();
        Personnel_Go.setDisable(false);
        Product_Inventory_Go.setDisable(false);
        Revenue_Tax_Go.setDisable(true);
        Price_Management_Go.setDisable(false);
        //setupChartData();
    }

    /**
     * Handles the sign-out button action, loading the 'login.fxml' page.
     * This method loads the login page, sets it as the current scene, 
     * and resets the stage with a new scene for login. It also disables fullscreen mode.
     *
     * @param event The action event triggered by the button press.
     */
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
     * Updates the revenue and tax labels with the latest data fetched from the bankDBO.
     * Displays the total revenue and tax amount.
     */
    private void updateRevenueAndTaxLabels() 
    {
        try {
            double[] totals = bankDBO.getBankTotals();
            double totalRevenue = totals[0];
            double totalTax = totals[1];
            
            TotalRevenue.setText(String.format("$%.2f", totalRevenue));
            TaxAmount.setText(String.format("$%.2f", totalTax));
        } catch (Exception e) {
            System.err.println("Error updating revenue and tax labels: " + e.getMessage());
            showAlert("Error", "Failed to load bank totals: " + e.getMessage());
        }
    }
  



    
    /**
     * Closes the current stage when called, typically when clicking the close button.
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Minimizes the current stage when called, typically when clicking the minimize button.
     */
    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) MinimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Navigates to the product management page by loading the corresponding FXML file.
     * Ensures the stage remains in fullscreen mode after the page is loaded.
     */
    @FXML
    private void handleProductManagement() {
        try {
            //manager_product
            Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/manager_product.fxml"));
            Scene scene = Product_Inventory_Go.getScene();

            scene.setRoot(root);

            // Optionally, update the stage title if needed
            Stage stage = (Stage) Product_Inventory_Go.getScene().getWindow();
            stage.setTitle("Step 4");
            
            // Ensure the stage remains in fullscreen
            stage.setFullScreen(true);
            stage.setFullScreenExitHint(""); // Hide the exit hint

        } catch (IOException e) {
            showAlert("Error", "Could not load product management page");
        }
    }

    /**
     * Navigates to the personnel management page by loading the corresponding FXML file.
     * Ensures the stage remains in fullscreen mode after the page is loaded.
     */
    @FXML
    private void handlePersonnelManagement() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/manager_personel.fxml"));
            Scene scene = Personnel_Go.getScene();
            scene.setRoot(root);
            
            Stage stage = (Stage) Personnel_Go.getScene().getWindow();
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            
        } catch (IOException e) {
            showAlert("Error", "Could not load personnel management page");
        }
    }

    /**
     * Navigates to the price management page by loading the corresponding FXML file.
     * Ensures the stage remains in fullscreen mode after the page is loaded.
     */
    @FXML
    private void handlePriceManagement() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/manager_price.fxml"));
            Scene scene = Price_Management_Go.getScene();
            scene.setRoot(root);
            
            Stage stage = (Stage) Price_Management_Go.getScene().getWindow();
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            
        } catch (IOException e) {
            showAlert("Error", "Could not load price management page");
        }
    }

    /**
     * Navigates to the revenue and tax management page by loading the corresponding FXML file.
     * Ensures the stage remains in fullscreen mode after the page is loaded.
     */
    @FXML
    private void handleRevenueTax() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/manager_revenue.fxml"));
            Scene scene = Revenue_Tax_Go.getScene();
            scene.setRoot(root);
            
            Stage stage = (Stage) Revenue_Tax_Go.getScene().getWindow();
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            
        } catch (IOException e) {
            showAlert("Error", "Could not load revenue management page");
        }
    }

    /**
     * Displays an alert with a specified title and message. It also ensures that the dialog 
     * retains fullscreen mode if the main window was in fullscreen.
     *
     * @param title   The title of the alert.
     * @param message The content of the alert.
     */
    private void showAlert(String title, String message) {
        // Store the main window state
        Stage mainStage = (Stage) RevenueChart.getScene().getWindow();
        boolean wasFullScreen = mainStage.isFullScreen();
    
        // Create the alert
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
    
        // Get the dialog stage (the underlying stage of the alert)
        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
    
        // Set the dialog to fullscreen mode
        dialogStage.setFullScreen(true);
    
        // Show the alert and wait for user interaction
        alert.showAndWait();
    
        // Restore fullscreen if needed
        if (wasFullScreen) {
            mainStage.setFullScreen(true);
        }
    }
    
}