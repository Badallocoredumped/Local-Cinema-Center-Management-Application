package help;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;

import help.utilities.PriceDBO;
import help.utilities.ProductDBO;
import help.classes.Product;

public class ManagerPriceController 
{

    @FXML private Button Product_Inventory_Go;
    @FXML private Button Personnel_Go;
    @FXML private Button Price_Management_Go;
    @FXML private Button Revenue_Tax_Go;
    @FXML private Button CloseButton;
    @FXML private Button MinimizeButton;
    @FXML private Button SignoutButton;
    
    @FXML private Label CurrentPriceHallA;
    @FXML private Label CurrentPriceHallB;
    @FXML private Label CurrentDiscountRate;
    
    @FXML private TextField NewPriceHallA;
    @FXML private TextField NewPriceHallB;
    @FXML private TextField NewDiscountRate;

    @FXML private Button Update_Button_A;
    @FXML private Button Update_Button_B;
    @FXML private Button Update_Button_C;



    private PriceDBO priceDBO = new PriceDBO();

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
    private void initialize() 
    {
        System.out.println("ManagerPriceController initialized");
        loadCurrentPrices();

        Personnel_Go.setDisable(false);
        Product_Inventory_Go.setDisable(false);
        Revenue_Tax_Go.setDisable(false);
        Price_Management_Go.setDisable(true);
       

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


    private void loadCurrentPrices() 
    {
        System.out.println("Loading current prices");
        try {
            double hallAPrice = priceDBO.getSeatPrice("Hall_A");
            double hallBPrice = priceDBO.getSeatPrice("Hall_B");
            double discountRate = priceDBO.getSeatDiscount("Hall_A");
            
            CurrentPriceHallA.setText(String.format("$%.2f", hallAPrice));
            CurrentPriceHallB.setText(String.format("$%.2f", hallBPrice));
            CurrentDiscountRate.setText(String.format("%.0f%%", discountRate));
        } catch (Exception e) {
            showAlert("Error", "Could not load prices: " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) MinimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

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

    

    @FXML
    private void handleUpdateHallA() 
    {
        try {
            String newPriceText = NewPriceHallA.getText().trim(); // Trim any leading or trailing spaces
            if (newPriceText.isEmpty()) {
                showAlert("Error", "Please enter a new price for Hall A");
                return;
            }
    
            double newPrice = Double.parseDouble(newPriceText);
            if (newPrice <= 0) {
                showAlert("Error", "Price must be greater than 0");
                return;
            }
            if (newPrice > 125) {
                showAlert("Error", "Price cannot exceed $125");
                return;
            }
    
            priceDBO.updateTicketPricing(1, newPrice, priceDBO.getSeatDiscount("Hall_A"));
            loadCurrentPrices();
            NewPriceHallA.clear();
            showAlert("Success", "Hall A price updated successfully!");
    
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid price format. Please enter a valid number.");
        } catch (Exception e) {
            showAlert("Error", "Failed to update price: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateHallB() {
        try {
            String newPriceText = NewPriceHallB.getText().trim(); // Trim any leading or trailing spaces
            if (newPriceText.isEmpty()) {
                showAlert("Error", "Please enter a new price for Hall B");
                return;
            }

            double newPrice = Double.parseDouble(newPriceText);
            if (newPrice <= 0) {
                showAlert("Error", "Price must be greater than 0");
                return;
            }
            if (newPrice > 125) {
                showAlert("Error", "Price cannot exceed $125");
                return;
            }

            priceDBO.updateTicketPricing(2, newPrice, priceDBO.getSeatDiscount("Hall_B"));
            loadCurrentPrices();
            NewPriceHallB.clear();
            showAlert("Success", "Hall B price updated successfully!");

        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid price format. Please enter a valid number.");
        } catch (Exception e) {
            showAlert("Error", "Failed to update price: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateDiscount() {
        try {
            String newDiscountText = NewDiscountRate.getText().trim(); // Trim any leading or trailing spaces
            if (newDiscountText.isEmpty()) {
                showAlert("Error", "Please enter a new discount rate");
                return;
            }

            double discountRate = Double.parseDouble(newDiscountText); // Use the number as-is
            if (discountRate < 0 || discountRate > 100) {  // Check if the discount is between 0 and 100
                showAlert("Error", "Discount rate must be between 0 and 100");
                return;
            }

            // Update discount for both halls, directly using the entered value as percentage
            priceDBO.updateTicketPricing(1, priceDBO.getSeatPrice("Hall_A"), discountRate); // Convert to decimal for DB update
            priceDBO.updateTicketPricing(2, priceDBO.getSeatPrice("Hall_B"), discountRate); // Convert to decimal for DB update

            loadCurrentPrices();
            NewDiscountRate.clear();
            showAlert("Success", "Discount rate updated successfully!");

        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid discount rate format. Please enter a valid number.");
        } catch (Exception e) {
            showAlert("Error", "Failed to update discount rate: " + e.getMessage());
        }
    }

    



    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    
}