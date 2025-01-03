package me.src.main.java.help;

import java.io.IOException;
import java.util.List;

import me.src.main.java.help.classes.ShoppingCart;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import me.src.main.java.help.classes.SelectedSession;
import me.src.main.java.help.classes.Session;

public class Step4Controller {

    @FXML
    private Button next_button_step4;
    @FXML
    private Button back_button_step4;
    @FXML
    private Label shoppingCartLabel;

    private String previousSalon;

    @FXML
    private void initialize() {
        // Initialization code here
        // Determine the previous salon from the shopping cart or session data
        List<String> selectedSeats = ShoppingCart.getInstance().getSelectedSeats();
        updateShoppingCartLabel(selectedSeats);

        Session selectedSession = SelectedSession.getInstance().getSession();
        if (selectedSession != null) {
            previousSalon = selectedSession.getHall();
        }
    }

    private void updateShoppingCartLabel(List<String> selectedSeats) {
        StringBuilder cartContent = new StringBuilder("Selected Seats:\n");
        for (String seat : selectedSeats) {
            cartContent.append(seat).append("\n");
        }
        shoppingCartLabel.setText(cartContent.toString());
    }

    @FXML
    private void handleNextButtonAction() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/final.fxml"));
        Stage stage = (Stage) next_button_step4.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true); // Ensure full screen
        stage.show();
    }

    @FXML
    private void handleBackButtonAction() throws IOException {
        // Clear the shopping cart
        ShoppingCart.getInstance().clear();
        System.out.println("Previous Salon: " + previousSalon);

        String fxmlFile = "/help/fxml/step2.fxml";
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = (Stage) back_button_step4.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true); // Ensure full screen
        stage.show();
    }
}
