package help;

import help.utilities.DataBaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() 
    {

    }

    /**
     * Handles the login button action.
     *
     * @param event The action event triggered by clicking the login button.
     */
    @FXML
    private void handleLoginButtonAction(ActionEvent event) 
    {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Debugging: Print entered credentials
        System.out.println("Username entered: " + username);
        System.out.println("Password entered: " + password);

        // Simple validation
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Form Error!", "Please enter username and password");
            return;
        }

        // Authenticate using DataBaseHandler
        String role = DataBaseHandler.authenticate(username, password); // Get the role of the user

        if (role != null) 
        {
            showAlert(AlertType.INFORMATION, "Login Successful", "Welcome, " + username + "!");
            loadUIForRole(role);
        } 
        else 
        {
            showAlert(AlertType.ERROR, "Login Failed", "Invalid username or password");
        }
    }

    /**
     * Loads the appropriate UI based on the user's role.
     *
     * @param role The role of the authenticated user.
     */
    private void loadUIForRole(String role) 
    {
        String fxmlFile = "";

        switch (role.toLowerCase()) 
        {
            case "cashier":
                fxmlFile = "/help/fxml/step1.fxml";
                break;
            case "admin":
                fxmlFile = "/help/fxml/admin.fxml";
                break;
            case "manager":
                fxmlFile = "/help/fxml/manager.fxml";
                break;
            default:
                showAlert(AlertType.ERROR, "Role Error", "Unrecognized user role.");
                return;
        }

        try 
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } 
        catch (IOException e) 
        {
            showAlert(AlertType.ERROR, "Error", "Cannot load the appropriate UI for the role.");
            e.printStackTrace();
        }
    }

    /**
     * Displays an alert dialog.
     *
     * @param alertType The type of alert.
     * @param title     The title of the alert dialog.
     * @param message   The content message of the alert dialog.
     */
    private void showAlert(AlertType alertType, String title, String message) 
    {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}