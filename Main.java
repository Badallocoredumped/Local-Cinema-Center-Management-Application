import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.geometry.Pos;
import utilities.DataBaseHandler; 

public class Main extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        primaryStage.setTitle("Local Cinema Center Management Application");

        // Login Interface
        VBox loginLayout = new VBox(10);
        loginLayout.setAlignment(Pos.CENTER);

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");

        loginLayout.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton);

        Scene loginScene = new Scene(loginLayout, 300, 200);
        primaryStage.setScene(loginScene);
        primaryStage.show();

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = authenticateUser(username, password);

            if (role != null)
            {
                primaryStage.close();
                showRoleBasedInterface(role);
            }
            else
            {
                // Show authentication failure message
                Label failureLabel = new Label("Authentication Failed. Please try again.");
                loginLayout.getChildren().add(failureLabel);
            }
        });
    }

    private String authenticateUser(String username, String password)
    {
        // Implement authentication logic here
        // For simplicity, let's assume we have a method in DataBaseHandler to authenticate users
        return DataBaseHandler.authenticate(username, password);
    }

    private void showRoleBasedInterface(String role)
    {
        Stage stage = new Stage();
        stage.setTitle("GroupXX CinemaCenter");

        if (role.equals("cashier"))
        {
            // Show cashier interface
            Label cashierLabel = new Label("Welcome, Cashier!");
            VBox cashierLayout = new VBox(10);
            cashierLayout.setAlignment(Pos.CENTER);
            cashierLayout.getChildren().add(cashierLabel);
            Scene cashierScene = new Scene(cashierLayout, 400, 300);
            stage.setScene(cashierScene);
        }
        else if (role.equals("admin"))
        {
            // Show admin interface
            Label adminLabel = new Label("Welcome, Admin!HEEEEEEEEEEEEEELP");
            VBox adminLayout = new VBox(10);
            adminLayout.setAlignment(Pos.CENTER);
            adminLayout.getChildren().add(adminLabel);
            Scene adminScene = new Scene(adminLayout, 400, 300);
            stage.setScene(adminScene);
        }
        else if (role.equals("manager"))
        {
            // Show manager interface
            Label managerLabel = new Label("Welcome, Manager!");
            VBox managerLayout = new VBox(10);
            managerLayout.setAlignment(Pos.CENTER);
            managerLayout.getChildren().add(managerLabel);
            Scene managerScene = new Scene(managerLayout, 400, 300);
            stage.setScene(managerScene);
        }

        stage.show();
    }
}