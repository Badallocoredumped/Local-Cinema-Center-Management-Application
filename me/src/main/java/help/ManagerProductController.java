package help;

import help.classes.Product;
import help.classes.ShoppingCart;
import help.utilities.ProductDBO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.nio.file.Files;
import java.io.IOException;

/**
 * Controller class for managing products in the system.
 * This class provides the functionality for adding, updating, and removing products.
 */
public class ManagerProductController 
{
    
    @FXML private Button CloseButton, MinimizeButton, SignoutButton,
                        Product_Inventory_Go, Revenue_Tax_Go,
                        Price_Management_Go, Personnel_Go,
                        AddButton, UpdateButton, ImportButton;
    
    @FXML private TableView<Product> ProductTable;
    @FXML private TableColumn<Product, String> ProductName, ProductType;
    @FXML private TableColumn<Product, Integer> ProductStock;
    @FXML private TableColumn<Product, BigDecimal> ProductPrice;
    
    @FXML private TextField ProductNameText, 
                           ProductStockText, ProductPriceText;
    @FXML private ComboBox<String> ProductTypeComboBox;
    @FXML private ImageView ProductImage;
    @FXML private Button importImageButton;

    /**
     * Handles the close button action. Closes the current window when the close button is clicked.
     * 
     * @param event The ActionEvent triggered by the Close button click.
     */
    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }
    /**
     * Handles the minimize button action. Minimizes the current window when the minimize button is clicked.
     * 
     * @param event The ActionEvent triggered by the Minimize button click.
     */
    @FXML
    private void handleMinimizeButtonAction(ActionEvent event) {
        Stage stage = (Stage) MinimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Handles the sign out button action. Clears the shopping cart, loads the login page, and sets it as the 
     * current scene. 
     * 
     * @param event The ActionEvent triggered by the Sign Out button click.
     */
    @FXML
    private void handleSignOutButtonAction(ActionEvent event) 
    {
        try 
        {
            ShoppingCart cart = ShoppingCart.getInstance();
            cart.clear();
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
    
    private ProductDBO productDBO = new ProductDBO();
    private byte[] selectedImageData;
    
    /**
     * Initializes the controller, setting up the table, combo boxes, buttons, and loading the product data.
     */
    @FXML
    public void initialize() 
    {
        ProductTable.getStyleClass().add("table-view");
        AddButton.getStyleClass().add("insert-button");
        UpdateButton.getStyleClass().add("insert-button-1");
        ImportButton.getStyleClass().add("insert-button-2");
        ProductImage.getStyleClass().add("anchor-pane1");
        MinimizeButton.getStyleClass().add("minus-button");
        ProductImage.setFitWidth(200);
        ProductImage.setFitHeight(200);

        setupTableColumns();
        setupComboBox();
        setupButtons();
        loadProducts();
        
        // Add selection listener
        ProductTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showProductDetails(newSelection);
            }
        });

        Personnel_Go.setDisable(false);
        Product_Inventory_Go.setDisable(true);
        Revenue_Tax_Go.setDisable(false);
        Price_Management_Go.setDisable(false);
    }
    
    /**
     * Sets up the table columns for the product list, including cell value factories, column widths, and formatting.
     */
    private void setupTableColumns() {
        // Setup column cell factories
        ProductName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        ProductType.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        ProductStock.setCellValueFactory(cellData -> cellData.getValue().stockAvailabilityProperty().asObject());
        ProductPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        
        // Set column widths
        ProductName.prefWidthProperty().bind(ProductTable.widthProperty().multiply(0.25));
        ProductType.prefWidthProperty().bind(ProductTable.widthProperty().multiply(0.20));
        ProductStock.prefWidthProperty().bind(ProductTable.widthProperty().multiply(0.15));
        ProductPrice.prefWidthProperty().bind(ProductTable.widthProperty().multiply(0.20));

        // Format price column to show currency
        ProductPrice.setCellFactory(tc -> new TableCell<Product, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
            }
        });
        
        // Right-align numeric columns
        ProductStock.setStyle("-fx-alignment: CENTER-RIGHT;");
        ProductPrice.setStyle("-fx-alignment: CENTER-RIGHT;");
    }
    
    /**
     * Sets up the combo box for product type selection with predefined values.
     */
    private void setupComboBox() {
        ProductTypeComboBox.setItems(FXCollections.observableArrayList(
            "Drinks", "Snacks", "Toys"
        ));
    }

    /**
     * Sets up button actions for the UI components, including close, minimize, import, and menu navigation.
     */
    private void setupButtons() {
        CloseButton.setOnAction(e -> handleClose());
        MinimizeButton.setOnAction(e -> handleMinimize());
        ImportButton.setOnAction(e -> {
            try {
                handleImportImage();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
       /*  AddButton.setOnAction(e -> handleAddProduct());
        UpdateButton.setOnAction(e -> handleUpdateProduct()); */
    
        // Add menu navigation handlers
        Product_Inventory_Go.setOnAction(e -> handleProductManagement());
        Personnel_Go.setOnAction(e -> handlePersonnelManagement());
        Price_Management_Go.setOnAction(e -> handlePriceManagement());
        Revenue_Tax_Go.setOnAction(e -> handleRevenueTax());
    }
    
    /**
     * Closes the current window.
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Minimizes the current window.
     */
    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) MinimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }
    
    /**
     * Handles the import image action. Allows the user to select an image file and updates the selected product's image.
     * 
     * @throws Exception If an error occurs while handling the image import.
     */
    @FXML
    private void handleImportImage() throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Read image file
                byte[] imageData = Files.readAllBytes(selectedFile.toPath());
                
                // Update image preview
                Image image = new Image(new ByteArrayInputStream(imageData));
                ProductImage.setImage(image);
                ProductImage.setFitHeight(190);
                ProductImage.setFitWidth(182);
                
                // Get selected product and update
                Product selectedProduct = ProductTable.getSelectionModel().getSelectedItem();
                if (selectedProduct != null) {
                    selectedProduct.setImageData(imageData);
                    if (productDBO.updateProduct(selectedProduct.getName(), selectedProduct)) {
                        showAlert("Success", "Image updated successfully");
                        loadProducts(); // Refresh table
                    }
                } else {
                    showAlert("Error", "Please select a product first");
                }
                
            } catch (IOException e) {
                showAlert("Error", "Failed to load image: " + e.getMessage());
            }
        }
    }

    /**
     * Loads products from the database and populates the ProductTable with them.
     * It also logs the details of each product for debugging purposes.
     */
    private void loadProducts() {
        try {
            System.out.println("Loading products...");
            List<Product> products = productDBO.loadProducts();
            System.out.println("Found " + products.size() + " products");
            
            ProductTable.getItems().clear();
            ProductTable.getItems().addAll(products);
            
            // Debug information
            for (Product p : products) {
                System.out.println("Loaded: " + p.getName() + " - " + p.getCategory());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load products: " + e.getMessage());
        }
    }
    
    /**
     * Displays the details of the selected product in the relevant UI fields.
     *
     * @param product The product whose details need to be displayed.
     */
    private void showProductDetails(Product product) {
        ProductNameText.setText(product.getName());
        ProductStockText.setText(String.valueOf(product.getStockAvailability()));
        ProductPriceText.setText(product.getPrice().toString());
        ProductTypeComboBox.setValue(product.getCategory());
        
        if (product.getImageData() != null && product.getImageData().length > 0) {
            Image image = new Image(new ByteArrayInputStream(product.getImageData()));
            ProductImage.setImage(image);
            ProductImage.setFitHeight(360);
            ProductImage.setFitWidth(360);
        } else {
            ProductImage.setImage(null);
        }
    }


    /**
     * Handles the action of adding a new product to the database. 
     * Validates inputs, creates a new product object, and attempts to insert it into the database.
     * If successful, the product list is refreshed, and input fields are cleared.
     */
    @FXML
    private void handleAddProduct() 
    {
        if (!validateInputs()) return;
        
        try {
            Product newProduct = new Product(
                selectedImageData,
                ProductNameText.getText(),
                new BigDecimal(ProductPriceText.getText()),
                Integer.parseInt(ProductStockText.getText()),
                BigDecimal.ZERO, // Default tax rate
                ProductTypeComboBox.getValue()
            );
            
            if (productDBO.insertProduct(newProduct)) {
                showSuccessMessage("added");
                clearFields();
                loadProducts();
            } else {
                showAlert("Error", "Failed to add product");
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid input: " + e.getMessage());
        }
    }

    /**
     * Handles the action of updating an existing product in the database.
     * Validates inputs, retrieves the selected product, creates an updated product object, 
     * and attempts to update the product in the database.
     * If successful, the product list is refreshed, and input fields are cleared.
     */
    @FXML
    private void handleUpdateProduct() {
        Product selectedProduct = ProductTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Error", "Please select a product to update");
            return;
        }
        
        if (!validateInputs()) return;
        
        try {
            Product updatedProduct = new Product(
                selectedImageData != null ? selectedImageData : selectedProduct.getImageData(),
                ProductNameText.getText(),
                new BigDecimal(ProductPriceText.getText()),
                Integer.parseInt(ProductStockText.getText()),
                selectedProduct.getTaxRate(),
                ProductTypeComboBox.getValue()
            );
            
            if (productDBO.updateProduct(selectedProduct.getName(), updatedProduct)) {
                showSuccessMessage("updated");
                clearFields();
                loadProducts();
            } else {
                showAlert("Error", "Failed to update product");
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid input: " + e.getMessage());
        }
    }


    /**
     * Handles the action of deleting a selected product from the database.
     * Displays a confirmation dialog before proceeding with the deletion.
     * If the deletion is successful, the product list is refreshed.
     */
    @FXML
    private void handleDeleteProduct() {
        Product selectedProduct = ProductTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Error", "Please select a product to delete");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete Product");
        confirmation.setContentText("Are you sure you want to delete " + selectedProduct.getName() + "?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                if (productDBO.deleteProduct(selectedProduct.getName())) {
                    showSuccessMessage("deleted");
                    clearFields();
                    loadProducts();
                } else {
                    showAlert("Error", "Failed to delete product");
                }
            } catch (Exception e) {
                showAlert("Error", "Could not delete product: " + e.getMessage());
            }
        }
    }

    /**
     * Validates the inputs for adding or updating a product. 
     * Checks if the product name, stock, price, and product type are valid.
     * 
     * @return true if all inputs are valid, false otherwise.
     */
    private boolean validateInputs() 
    {
    // Validate Product Name
        if (ProductNameText == null || ProductNameText.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Product name cannot be empty");
            return false;
        } else if (!ProductNameText.getText().trim().matches("[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]+")) {
            showAlert("Validation Error", "Product name can only contain letters and Turkish characters");
            return false;
        }

        // Validate Stock
        try {
            int stock = Integer.parseInt(ProductStockText.getText().trim());
            if (stock < 0) {
                showAlert("Validation Error", "Stock cannot be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter a valid number for stock");
            return false;
        }

        // Validate Price
        try {
            double price = Double.parseDouble(ProductPriceText.getText().trim());
            if (price < 0) {
                showAlert("Validation Error", "Price cannot be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter a valid number for price");
            return false;
        }

        // Validate Product Type
        if (ProductTypeComboBox == null || ProductTypeComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select a product type");
            return false;
        }

        // All validations passed
        return true;
    }


    /**
     * Clears all input fields for product management.
     * This includes clearing the product name, stock, price, type, 
     * and image data fields.
     */
    private void clearFields() {
        ProductNameText.clear();
        ProductStockText.clear();
        ProductPriceText.clear();
        ProductTypeComboBox.setValue(null);
        ProductImage.setImage(null);
        selectedImageData = null;
    }
    
    /**
     * Displays an informational alert with a specified title and message.
     * The alert appears in the same window as the main application and retains
     * the fullscreen state if it was active before the alert was shown.
     *
     * @param title   The title of the alert.
     * @param message The message content of the alert.
     */
    private void showAlert(String title, String message) {
        // Store the main window state
        Stage mainStage = (Stage) SignoutButton.getScene().getWindow();
        boolean wasFullScreen = mainStage.isFullScreen();
        
        // Create the alert
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Show the alert and wait for user interaction
        alert.showAndWait();
        
        // Ensure the fullscreen state is retained
        if (wasFullScreen) {
            mainStage.setFullScreen(true);
        }
    }

    /**
     * Displays a success message indicating that a product has been successfully added, updated, or deleted.
     * The alert appears in the same window as the main application and retains
     * the fullscreen state if it was active before the alert was shown.
     *
     * @param action The action performed, such as "added", "updated", or "deleted".
     */
    private void showSuccessMessage(String action) {
        // Store the main window state
        Stage mainStage = (Stage) ProductTable.getScene().getWindow();
        boolean wasFullScreen = mainStage.isFullScreen();
        
        // Create the success message alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Product successfully " + action + "!");
        
        // Show the alert and wait for user interaction
        alert.showAndWait();
        
        // Ensure the fullscreen state is retained
        if (wasFullScreen) {
            mainStage.setFullScreen(true);
        }
    }
    
    /**
     * Handles the action of navigating to the product management page.
     * Loads the corresponding FXML file and sets it as the root of the current scene.
     * Ensures the stage remains in fullscreen mode.
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
     * Handles the action of navigating to the personnel management page.
     * Loads the corresponding FXML file and sets it as the root of the current scene.
     * Ensures the stage remains in fullscreen mode.
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
     * Handles the action of navigating to the price management page.
     * Loads the corresponding FXML file and sets it as the root of the current scene.
     * Ensures the stage remains in fullscreen mode.
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
     * Handles the action of navigating to the revenue and tax management page.
     * Loads the corresponding FXML file and sets it as the root of the current scene.
     * Ensures the stage remains in fullscreen mode.
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
}