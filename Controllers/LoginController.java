package Controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import utilities.DataBaseHandler;

public class LoginController 
{

    @FXML
    private TextField UsernameEnter;

    @FXML
    private TextField PasswordEnter;

    @FXML
    private Button LoginButton;

    @FXML
    private void initialize() 
    {
        LoginButton.setOnAction(event -> handleLogin());
        System.out.println("LoginController initialized");
    }

    private void handleLogin() 
    {
        System.out.println("Login button clicked");
        String username = UsernameEnter.getText();
        String password = PasswordEnter.getText();

        String role = DataBaseHandler.authenticate(username, password);
        if (role != null) {
            System.out.println("Login successful. Role: " + role);
            // Proceed to the next scene or functionality based on the role
        } else {
            System.out.println("Invalid username or password");
        }
    }
}