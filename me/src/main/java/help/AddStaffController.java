package help;

import help.classes.Movie;
import help.classes.MovieService;
import help.classes.SelectedMovie;
import help.classes.ShoppingCart;
import help.classes.User;
import help.utilities.UserDBO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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


public class AddStaffController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    
    private UserDBO userDBO = new UserDBO();
    private Manager_PersonnelController parentController;
    
    @FXML
    public void initialize() 
    {
        roleComboBox.getItems().addAll("Admin", "Manager", "Cashier");
        
    }

    private void closeDialog() 
    {
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close();
    }
        @FXML
    private void handleCancel(ActionEvent event) {
        closeDialog();
    }
    
    public void setParentController(Manager_PersonnelController controller) 
    {
        this.parentController = controller;
    }
    
    @FXML
    private void handleSave() {
        if (!validateInputs()) {
            return;
        }
    
        User newUser = new User(
            firstNameField.getText().trim(),
            lastNameField.getText().trim(),
            usernameField.getText().trim(),
            roleComboBox.getValue(),
            passwordField.getText()
        );
        
        if (userDBO.insertUser(newUser, passwordField.getText())) {
            closeDialog();
            parentController.refreshTable();
        } else {
            showAlert("Error", "Could not save new staff member");
        }
    }
    
    private boolean validateInputs() {
        // Check empty fields
        if (firstNameField.getText().trim().isEmpty() ||
            lastNameField.getText().trim().isEmpty() ||
            usernameField.getText().trim().isEmpty() ||
            passwordField.getText().isEmpty()) {
            showAlert("Validation Error", "All fields must be filled");
            return false;
        }
    
        // Validate names (letters only including Turkish characters)
        String turkishLetterPattern = "[a-zA-ZğĞıİöÖşŞüÜçÇ]+";
        if (!firstNameField.getText().trim().matches(turkishLetterPattern) ||
            !lastNameField.getText().trim().matches(turkishLetterPattern)) {
            showAlert("Validation Error", "Names must contain only letters");
            return false;
        }
    
        // Username validation (alphanumeric, min 4 chars)
        if (!usernameField.getText().trim().matches("[a-zA-Z0-9ğĞıİöÖşŞüÜçÇ]{4,}")) {
            showAlert("Validation Error", "Username must be at least 4 characters and can contain letters, numbers, and Turkish characters");
            return false;
        }
    
        // Password validation (min 8 chars, 1 number)
        if (!passwordField.getText().matches("^(?=.*\\d).{8,}$")) {
            showAlert("Validation Error", "Password must be at least 8 characters and contain at least 1 number");
            return false;
        }
    
        // Role selection check
        if (roleComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select a role");
            return false;
        }
    
        return true;
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        
        // Get current stage (Add Staff dialog)
        Stage currentStage = (Stage) firstNameField.getScene().getWindow();
        
        // Set owner and modality
        alert.initOwner(currentStage);
        alert.initModality(Modality.WINDOW_MODAL);
        
        // Center alert over dialog
        
        alert.showAndWait();
    }
}