package help;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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
    private void initialize() {
        // Optional: Add any initialization logic here
    }

    /**
     * Handles the login button action.
     *
     * @param event The action event triggered by clicking the login button.
     */
    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Simple validation
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Form Error!", "Please enter username and password");
            return;
        }

        // TODO: Implement actual authentication logic
        boolean isAuthenticated = authenticate(username, password);

        if (isAuthenticated) {
            showAlert(AlertType.INFORMATION, "Login Successful", "Welcome, " + username + "!");
            // TODO: Navigate to the next scene or perform other actions
        } else {
            showAlert(AlertType.ERROR, "Login Failed", "Invalid username or password");
        }
    }

    /**
     * Authenticates the user based on username and password.
     * This is a placeholder method and should be replaced with real authentication logic.
     *
     * @param username The entered username.
     * @param password The entered password.
     * @return true if authentication is successful, false otherwise.
     */
    private boolean authenticate(String username, String password) {
        // Placeholder authentication logic
        // Replace with real authentication (e.g., database query)
        return "admin".equals(username) && "password".equals(password);
    }

    /**
     * Displays an alert dialog.
     *
     * @param alertType The type of alert.
     * @param title     The title of the alert dialog.
     * @param message   The content message of the alert dialog.
     */
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}