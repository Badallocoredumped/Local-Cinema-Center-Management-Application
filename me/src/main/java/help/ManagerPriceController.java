package help;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import help.utilities.PriceDBO;
import help.utilities.ProductDBO;
import help.classes.Product;

/**
 * Controller class for managing the prices of products within the system.
 * This class handles the functionality for updating and displaying the prices of various items.
 */
public class ManagerPriceController 
{

    @FXML private Button Product_Inventory_Go;
    @FXML private Button Personnel_Go;
    @FXML private Button Price_Management_Go;
    @FXML private Button Revenue_Tax_Go;
    @FXML private Button CloseButton;
    @FXML private Button MinimizeButton;
    @FXML private Button SignoutButton;
    @FXML private ImageView Image1;
    @FXML private ImageView Image2;

    
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

    /**
     * Handles the action for the "Close" button. This will close the current stage.
     * 
     * @param event The ActionEvent triggered by the "Close" button click.
     */
    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the action for the "Minimize" button. This will minimize the current stage to the taskbar.
     * 
     * @param event The ActionEvent triggered by the "Minimize" button click.
     */
    @FXML
    private void handleMinimizeButtonAction(ActionEvent event) {
        Stage stage = (Stage) MinimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Initializes the controller and sets up the UI elements. This includes loading images for the UI and
     * setting initial states for buttons. It also loads the current prices from the PriceDBO.
     */
    @FXML
    private void initialize() 
    {
        Image image1 = new Image(getClass().getResourceAsStream("/help/images/cinema-hall.png"));
        ImageView imageView1 = new ImageView(image1);

        // Load the second image
        Image image2 = new Image(getClass().getResourceAsStream("/help/images/discount.png"));
        ImageView imageView2 = new ImageView(image2);





        System.out.println("ManagerPriceController initialized");
        loadCurrentPrices();

        Personnel_Go.setDisable(false);
        Product_Inventory_Go.setDisable(false);
        Revenue_Tax_Go.setDisable(false);
        Price_Management_Go.setDisable(true);
       

    }

    /**
     * Handles the action for the "Sign Out" button. This will load the login screen and sign the user out.
     * 
     * @param event The ActionEvent triggered by the "Sign Out" button click.
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
     * Loads and displays the current prices and discount rate for the cinema halls from the PriceDBO.
     * The prices are displayed in the respective UI elements.
     */
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
            showAlert(null, "Error");
        }
    }

    /**
     * Handles the action for the "Close" button. This will close the current stage.
     * 
     * @param event The ActionEvent triggered by the "Close" button click.
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the action for the "Minimize" button. This will minimize the current stage to the taskbar.
     * 
     * @param event The ActionEvent triggered by the "Minimize" button click.
     */
    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) MinimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Handles the action for the "Product Management" button. This will load the product management page
     * in the current window and update the stage to fullscreen.
     * 
     * @param event The ActionEvent triggered by the "Product Management" button click.
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
            showAlert(null, "Error");
        }
    }

    /**
     * Handles the action for the "Personnel Management" button. This will load the personnel management page
     * in the current window and set the stage to fullscreen.
     * 
     * @param event The ActionEvent triggered by the "Personnel Management" button click.
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
            showAlert(null, "Error");
        }
    }

    /**
     * Handles the action for the "Price Management" button. This will load the price management page
     * in the current window and set the stage to fullscreen.
     * 
     * @param event The ActionEvent triggered by the "Price Management" button click.
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
            showAlert(null, "Error");
        }
    }

    /**
     * Handles the action for the "Revenue and Tax" button. This will load the revenue and tax page
     * in the current window and set the stage to fullscreen.
     * 
     * @param event The ActionEvent triggered by the "Revenue and Tax" button click.
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
            showAlert(null, "Error");
        }
    }

    

    /**
     * Handles the action for updating the price of Hall A. Validates the input price and updates the price in the database.
     * Displays an alert with success or failure message.
     */
    @FXML
    private void handleUpdateHallA() 
    {
        try {
            String newPriceText = NewPriceHallA.getText().trim(); // Trim any leading or trailing spaces
            if (newPriceText.isEmpty()) {
                showAlert(null, "Error");
                return;
            }
    
            double newPrice = Double.parseDouble(newPriceText);
            if (newPrice <= 0) {
                showAlert(null, "Error");
                return;
            }
            if (newPrice > 125) {
                showAlert(null, "Error");
                return;
            }
    
            priceDBO.updateTicketPricing(1, newPrice, priceDBO.getSeatDiscount("Hall_A"));
            loadCurrentPrices();
            NewPriceHallA.clear();
            showAlert(null, "Succesfully Updated Hall A Price");
    
        } catch (NumberFormatException e) {
            showAlert(null, "Error in Hall A");
        } catch (Exception e) {
            showAlert(null, "Error in Hall A");
        }
    }

    /**
     * Handles the action for updating the price of Hall B. Validates the input price and updates the price in the database.
     * Displays an alert with success or failure message.
     */
    @FXML
    private void handleUpdateHallB() {
        try {
            String newPriceText = NewPriceHallB.getText().trim(); // Trim any leading or trailing spaces
            if (newPriceText.isEmpty()) {
                showAlert(null, "Error in Hall B");
                return;
            }

            double newPrice = Double.parseDouble(newPriceText);
            if (newPrice <= 0) {
                showAlert(null, "Error in Hall B");
                return;
            }
            if (newPrice > 125) {
                showAlert(null, "Error in Hall B");
                return;
            }

            priceDBO.updateTicketPricing(2, newPrice, priceDBO.getSeatDiscount("Hall_B"));
            loadCurrentPrices();
            NewPriceHallB.clear();
            showAlert(null, "Succesfully Updated Hall B Price");

        } catch (NumberFormatException e) {
            showAlert(null, "Error");
        } catch (Exception e) {
            showAlert(null, "Error");
        }
    }

    /**
     * Handles the action for updating the discount rate. Validates the input discount and updates the discount in the database.
     * Displays an alert with success or failure message.
     */
    @FXML
    private void handleUpdateDiscount() {
        try {
            String newDiscountText = NewDiscountRate.getText().trim(); // Trim any leading or trailing spaces
            if (newDiscountText.isEmpty()) {
                showAlert(null, "Error");
                return;
            }

            double discountRate = Double.parseDouble(newDiscountText); // Use the number as-is
            if (discountRate < 0 || discountRate > 100) {  // Check if the discount is between 0 and 100
                showAlert(null, "Error");
                return;
            }

            // Update discount for both halls, directly using the entered value as percentage
            priceDBO.updateTicketPricing(1, priceDBO.getSeatPrice("Hall_A"), discountRate); // Convert to decimal for DB update
            priceDBO.updateTicketPricing(2, priceDBO.getSeatPrice("Hall_B"), discountRate); // Convert to decimal for DB update

            loadCurrentPrices();
            NewDiscountRate.clear();
            showAlert(null, "Successfully Updated Discount Rate");

        } catch (NumberFormatException e) {
            showAlert(null, "Error");
        } catch (Exception e) {
            showAlert(null, "Error");
        }
    }

    



    /**
     * Displays an alert with a given title and message.
     * 
     * @param title The title of the alert (can be null).
     * @param message The message to display in the alert.
     */
    private void showAlert(String title, String message) 
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    }

    
}