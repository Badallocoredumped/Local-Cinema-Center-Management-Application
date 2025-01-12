package help;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import help.classes.Movie;
import help.classes.MovieService;
import help.classes.SelectedMovie;
import help.classes.ShoppingCart;
import help.classes.User;
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
import javafx.event.ActionEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.ComboBoxTreeTableCell;

import help.utilities.UserDBO;

public class Manager_PersonnelController 
{
    private UserDBO userDBO = new UserDBO();

    @FXML
    private Button CloseButton, MinimizeButton, SignoutButton,
    Product_Inventory_Go, Revenue_Tax_Go,
    Price_Management_Go, Personnel_Go, Add_New_Staff_Butto,Fire_Staff_Button;

    @FXML
    private Label Total_Staff_Count;

    @FXML
    private TreeTableView<User> Personnel_Table;

    @FXML
    private TreeTableColumn<User, String> First_Name_Column;
    @FXML
    private TreeTableColumn<User, String> Last_Name_Column;
    @FXML
    private TreeTableColumn<User, String> Username_Column;
    @FXML
    private TreeTableColumn<User, String> Role_Column;
    @FXML
    private TreeTableColumn<User, String> Password_Column;
    
    private String currentUsername; // Add this field


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

    public void setCurrentUsername(String username) 
    {
        this.currentUsername = username;
    }


    @FXML
    public void initialize() 
    {

        First_Name_Column.prefWidthProperty().bind(Personnel_Table.widthProperty().multiply(0.2));
        Last_Name_Column.prefWidthProperty().bind(Personnel_Table.widthProperty().multiply(0.2));
        Username_Column.prefWidthProperty().bind(Personnel_Table.widthProperty().multiply(0.2));
        Role_Column.prefWidthProperty().bind(Personnel_Table.widthProperty().multiply(0.2));
        Password_Column.prefWidthProperty().bind(Personnel_Table.widthProperty().multiply(0.2));
        // Setup columns
        System.out.println("initialize");
        First_Name_Column.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, String> param) -> new SimpleStringProperty(param.getValue().getValue().getFirstName()));
        Last_Name_Column.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, String> param) -> new SimpleStringProperty(param.getValue().getValue().getLastName()));
        Username_Column.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, String> param) -> new SimpleStringProperty(param.getValue().getValue().getUsername()));
        Role_Column.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, String> param) -> new SimpleStringProperty(param.getValue().getValue().getRole()));
        Password_Column.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, String> param) -> new SimpleStringProperty(param.getValue().getValue().getPassword()));


        // Load users
        loadUsers();
        
        // Set initial button states
        Personnel_Go.setDisable(true);
        Product_Inventory_Go.setDisable(false);
        Revenue_Tax_Go.setDisable(false);
        Price_Management_Go.setDisable(false);

        Personnel_Table.setEditable(true);
        setupEditableColumns();
    }

    private void loadUsers() 
    {
        List<User> users = userDBO.getAllUsers();
        
        TreeItem<User> root = new TreeItem<>();
        root.setExpanded(true);
        
        for (User user : users) 
        {
            root.getChildren().add(new TreeItem<>(user));
        }
        
        Personnel_Table.setShowRoot(false);
        Personnel_Table.setRoot(root);
        
        Total_Staff_Count.setText(String.valueOf(users.size()));
    }

    @FXML
    private void handleAddNewStaffButton(ActionEvent event) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/help/fxml/add_staff.fxml"));
            Parent root = loader.load();
            
            // Create dialog
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(Personnel_Table.getScene().getWindow());
            dialogStage.setTitle("Add New Staff");
            
            // Set fixed size for dialog
            Scene scene = new Scene(root, 275, 300);
            dialogStage.setScene(scene);
            
            // Center the dialog
            dialogStage.centerOnScreen();
            
            // Get controller and pass reference
            AddStaffController controller = loader.getController();
            controller.setParentController(this);
            
            dialogStage.showAndWait();
            
            // Refresh table after dialog closes
            loadUsers();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open add staff dialog");
        }
    }
    @FXML
    private void handleFireStaffButton(ActionEvent event) 
    {
        TreeItem<User> selectedItem = Personnel_Table.getSelectionModel().getSelectedItem();
        
        if (selectedItem == null || selectedItem.getValue() == null) {
            showAlert("Error", "Please select a staff member to remove");
            return;
        }

        User selectedUser = selectedItem.getValue();
        
        // Prevent self-firing
        if (selectedUser.getUsername().equals(currentUsername)) {
            showAlert("Error", "You cannot remove yourself from the system");
            return;
        }

        // Show confirmation dialog
        // Show confirmation dialog
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Are you sure you want to remove " + 
                        selectedUser.getFirstName() + " " + 
                        selectedUser.getLastName() + "?");
        alert.setContentText("This action cannot be undone.");

        // Set owner and modality
        Stage currentStage = (Stage) Personnel_Table.getScene().getWindow();
        alert.initOwner(currentStage);
        alert.initModality(Modality.WINDOW_MODAL);

        // Center alert

        if (alert.showAndWait().get() == ButtonType.OK) 
        {
            UserDBO userDBO = new UserDBO();
            if (userDBO.deleteUser(selectedUser.getUsername())) 
            {
                loadUsers(); // Refresh table
            } 
            else 
            {
                showAlert("Error", "Could not remove staff member");
            }
        }
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

    private void showAlert(String title, String content) 
    {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.initModality(Modality.WINDOW_MODAL);
        
        // Get the current stage (add staff dialog)
        Stage currentStage = (Stage) Personnel_Table.getScene().getWindow();
        alert.initOwner(currentStage);
        
        // Position alert relative to owner
        
        alert.showAndWait();
    }

    

    // Method to be called from AddStaffController
    public void refreshTable() {
        loadUsers();
    }

    @FXML
    private void handlePersonnelButtonAction(ActionEvent event) {
        // Refresh the current view since we're already on personnel
        loadUsers();
        
        // Visual feedback - disable this button and enable others
        Personnel_Go.setDisable(true);
        Product_Inventory_Go.setDisable(false);
        Revenue_Tax_Go.setDisable(false);
        Price_Management_Go.setDisable(false);
    }

    @FXML
    private void handleProductManagement() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/manager_product.fxml"));
            Scene scene = Product_Inventory_Go.getScene();
            scene.setRoot(root);
            
            Stage stage = (Stage) Product_Inventory_Go.getScene().getWindow();
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            
        } catch (IOException e) {
            showAlert("Error", "Could not load product management page");
        }
    }

    @FXML
    private void handlePersonnelManagement() {
        try {
            URL resourceUrl = getClass().getResource("/help/fxml/manager_personel.fxml");
            if (resourceUrl == null) {
                throw new IOException("FXML file not found");
            }
            
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            Stage stage = (Stage) Personnel_Go.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.setResizable(false);
            stage.show();
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

    private void setupEditableColumns() 
    {
            First_Name_Column.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        First_Name_Column.setOnEditCommit(event -> {
            User user = event.getRowValue().getValue();
            user.setFirstName(event.getNewValue());
            updateUser(user,"");
        });

        Last_Name_Column.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        Last_Name_Column.setOnEditCommit(event -> {
            User user = event.getRowValue().getValue();
            user.setLastName(event.getNewValue());
            updateUser(user,"");
        });

        Username_Column.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        Username_Column.setOnEditCommit(event -> {
            User user = event.getRowValue().getValue();
            if (validateUsername(event.getNewValue())) 
            {
                String oldUsername = event.getNewValue();
                updateUser(user,oldUsername);
            }
        });

        Password_Column.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        Password_Column.setOnEditCommit(event -> {
            User user = event.getRowValue().getValue();
            if (validatePassword(event.getNewValue())) {
                user.setPassword(event.getNewValue());
                updateUser(user,"");
            }
        });

        Role_Column.setCellFactory(ComboBoxTreeTableCell.forTreeTableColumn(
            FXCollections.observableArrayList("manager", "admin", "cashier")
        ));
        Role_Column.setOnEditCommit(event -> {
            User user = event.getRowValue().getValue();
            user.setRole(event.getNewValue());
            updateUser(user,"");
        });

        // Make columns editable
        First_Name_Column.setEditable(true);
        Last_Name_Column.setEditable(true);
        Username_Column.setEditable(true);
        Password_Column.setEditable(true);
        Role_Column.setEditable(true);
    }

    private void updateUser(User user,String oldUsername) {
        try {
            if (userDBO.updateUser(user,oldUsername)) 
            {
                showAlert(AlertType.INFORMATION, "Success", "User information updated successfully!");
                setCurrentUsername(user.getUsername());
                loadUsers(); // Refresh table
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to update user information");
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Error updating user: " + e.getMessage());
        }
    }

    private boolean validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            showAlert(AlertType.ERROR, "Invalid Input", "Username cannot be empty");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        if (password == null || password.length() < 6) {
            showAlert(AlertType.ERROR, "Invalid Input", "Password must be at least 6 characters");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) 
    {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        // Get the primary stage that contains this controller's scene
        Stage primaryStage = (Stage) Personnel_Table.getScene().getWindow();
        
        // Set the alert's owner to the primary stage
        alert.initOwner(primaryStage);
        
        // Keep alert within fullscreen window
        alert.initModality(Modality.WINDOW_MODAL);
        
        // Show and wait for user response
        alert.showAndWait();
    }
}
