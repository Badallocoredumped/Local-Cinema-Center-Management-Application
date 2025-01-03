package me.src.main.java.help;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.src.main.java.help.classes.SelectedSession;
import me.src.main.java.help.classes.Session;
import me.src.main.java.help.classes.ShoppingCart;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Step3AController 
{
    @FXML
    private Button next_button_step3a;
    @FXML
    private Button back_button_step3a;
    @FXML
    private Button add_to_cart_button;
    @FXML
    private Label shoppingCartLabel;

    @FXML
    private Button seat_A1, seat_A2, seat_A3, seat_A4;
    @FXML
    private Button seat_B1, seat_B2, seat_B3, seat_B4;
    @FXML
    private Button seat_C1, seat_C2, seat_C3, seat_C4;
    @FXML
    private Button seat_D1, seat_D2, seat_D3, seat_D4;

    private List<String> selectedSeats = new ArrayList<>();

    @FXML
    private void initialize() 
    {

        // Access the selected session
        Session selectedSession = SelectedSession.getInstance().getSession();
        if (selectedSession != null) 
        {
            System.out.println("Selected Session: " + selectedSession.getDay() + " " + selectedSession.getSession() + " " + selectedSession.getHall());
        }

        // Initialize seat buttons
        initializeSeatButtons();
    }

    private void initializeSeatButtons() 
    {
        Button[] seats = {
            seat_A1, seat_A2, seat_A3, seat_A4,
            seat_B1, seat_B2, seat_B3, seat_B4,
            seat_C1, seat_C2, seat_C3, seat_C4,
            seat_D1, seat_D2, seat_D3, seat_D4
        };

        for (Button seat : seats) 
        {
            seat.setOnAction(event -> handleSeatSelection(seat));
            seat.setStyle("-fx-background-color: GREEN;"); // Default to available
        }
    }

    private void handleSeatSelection(Button seatButton) 
    {
        String seatId = seatButton.getId();
        if (selectedSeats.contains(seatId)) 
        {
            selectedSeats.remove(seatId);
            seatButton.setStyle("-fx-background-color: GREEN;");
        } 
        else 
        {
            selectedSeats.add(seatId);
            seatButton.setStyle("-fx-background-color: YELLOW;");
        }
        updateShoppingCart();
        
    }

    private void updateShoppingCart() 
    {
        StringBuilder cartContent = new StringBuilder("Selected Seats:\n");
        for (String seat : selectedSeats) 
        {
            cartContent.append(seat).append("\n");
        }
        shoppingCartLabel.setText(cartContent.toString());
    }

    

    @FXML
    private void handleAddToCartAction() 
    {
        List<String> cartSeats = ShoppingCart.getInstance().getSelectedSeats();
        for (String seat : selectedSeats) {
            if (cartSeats.contains(seat)) {
                showAlert("Seat already in cart", "The seat " + seat + " is already in the cart.");
                return;
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/help/fxml/confirmation_dialog.fxml"));
            Parent root = loader.load();

            NewConfirmationDialogController controller = loader.getController();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Confirmation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(add_to_cart_button.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            if (controller.isConfirmed()) {
                // Add selected seats to the shopping cart
                ShoppingCart.getInstance().addSeats(selectedSeats);
                System.out.println("Seats added to cart: " + selectedSeats);
                // Optionally, clear the selected seats after adding to cart
                selectedSeats.clear();
                updateShoppingCart();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleNextButtonAction() throws IOException 
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/help/fxml/step4.fxml"));
        Parent root = loader.load();

        Step4Controller controller = loader.getController();

        Stage stage = (Stage) next_button_step3a.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true); // Ensure full screen
    }

    @FXML
    private void handleBackButtonAction() throws IOException 
    {
        Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/step2.fxml"));
        Stage stage = (Stage) back_button_step3a.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true); // Ensure full screen
        stage.show();
    }
}