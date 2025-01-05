package help;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import help.classes.Movie;
import help.classes.SelectedSession;
import help.classes.Session;
import help.classes.ShoppingCart;
import help.utilities.DataBaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;

public class Step3AController 
{
    @FXML
    private Button next_button_step3a;
    @FXML
    private Button back_button_step3a;
    @FXML
    private Button add_to_cart_button;
    @FXML
    private Label selectedSeat;
    @FXML
    private Label selectedMovieLabel;
    @FXML
    private Label selectedSession;


    @FXML
    private Button SignoutButton;
    @FXML
    private Button CloseButton;
    @FXML
    private Button MinimizeButton; 

    @FXML
    private Button A1, A2, A3, A4;
    @FXML
    private GridPane seatsGridPane;
    @FXML
    private Button B1, B2, B3, B4;
    @FXML
    private Button C1, C2, C3, C4;
    @FXML
    private Button D1, D2, D3, D4;

    private List<String> selectedSeats = new ArrayList<>();
    private List<String> confirmedSeats = new ArrayList<>();

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

    @FXML
    private void initialize() throws Exception
    {
        ShoppingCart cart = ShoppingCart.getInstance();
        Session session = cart.getSelectedDaySessionAndHall();

        selectedMovieLabel.setText(cart.getSelectedMovie().getTitle());
        selectedSession.setText((
            session.getSession() + "\n" +
            session.getDay() + "\n" +
            session.getHall()));

        // Access the selected session
        // Initialize seat buttons
        initializeSeatButtons();
    }

    private List<String> fetchSoldSeats() throws Exception 
    {
        List<String> soldSeats = new ArrayList<>();
        ShoppingCart cart = ShoppingCart.getInstance();
        Session session = cart.getSelectedDaySessionAndHall();
        
        try (Connection conn = DataBaseHandler.getConnection()) 
        {
            // Assuming "purchases" table has a column "seat_id" that tracks sold seats
            String query = "SELECT seat_label FROM Seats WHERE session_id = ? AND is_occupied = TRUE";
            PreparedStatement stmt = conn.prepareStatement(query);
            
            // Pass in the selected session_id from the ShoppingCart or your current session
            stmt.setInt(1, session.getSessionId());
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) 
            {
                soldSeats.add(rs.getString("seat_label"));
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        System.out.println("Sold seats: " + soldSeats.size());
        return soldSeats;
    }


    private void initializeSeatButtons() throws Exception 
    {
        Button[] seats = 
        {
            A1, A2, A3, A4,
            B1, B2, B3, B4,
            C1, C2, C3, C4,
            D1, D2, D3, D4
        };
    
        // Fetch sold seats from database
        List<String> soldSeats = fetchSoldSeats();
        System.out.println("Before selection: " + soldSeats); // Log current selectedSeats

        for (Button seat : seats) 
        {
            System.out.println("Button ID: " + seat.getId());

            // Check if the seat is sold
            if (soldSeats.contains(seat.getId())) 
            {
                System.out.println("Seat is sold: " + seat.getId());
                seat.setStyle("-fx-background-color: RED;"); // Sold seat
            } 
            else 
            {
                System.out.println("Seat is available: " + seat.getId());
                seat.setStyle("-fx-background-color: GREEN;"); // Available seat
                seat.setOnAction(event -> handleSeatSelection(seat)); // Set action for available seats
            }
        }
    }
    

    private void handleSeatSelection(Button seatButton) 
    {
        String seatId = seatButton.getId();
        
        if (selectedSeats.contains(seatId)) 
        {
            selectedSeats.remove(seatId);
            seatButton.setStyle("-fx-background-color: GREEN;"); // Seat deselected
        } 
        else 
        {
            selectedSeats.add(seatId);
            seatButton.setStyle("-fx-background-color: YELLOW;"); // Seat selected
        }
        //updateSelectedSeatsLabel();
        updateShoppingCart();
    }
    
    /* private void updateSelectedSeatsLabel() 
    {
        if (selectedSeats.isEmpty()) {
            selectedSeat.setText("No seats selected.");
        } 
        else 
        {
            // Join the selected seats with commas and update the label
            String selectedSeatsText = String.join(", ", selectedSeats);
            selectedSeat.setText("Selected Seats: " + selectedSeatsText);
        }
    } */

    private void updateShoppingCart() 
    {
        // Get the current selected seats
        List<String> cartSeats = ShoppingCart.getInstance().getSelectedSeats();
    
        // Update the shopping cart with the selected seats
        for (String seat : selectedSeats) 
        {
            if (!cartSeats.contains(seat)) 
            {
                cartSeats.add(seat); // Add the seat if not already in cart
            }
        }
    
        // Update the shopping cart label to reflect the current seats in the cart
        String cartSeatsText = cartSeats.isEmpty() ? "No seats in cart" : String.join(",", cartSeats);
        selectedSeat.setText(cartSeatsText);
    }
    

    

    @FXML
    private void handleAddToCartAction() throws Exception
    {
        List<String> cartSeats = ShoppingCart.getInstance().getSelectedSeats();
        ShoppingCart cart = ShoppingCart.getInstance();
    
        // Check if the selected seat is already sold
        for (String seat : selectedSeats) 
        {
            if (cartSeats.contains(seat)) 
            {
                showAlert("Seat already in cart", "The seat " + seat + " is already in the cart.");
                return;
            }
        }
    
        try 
        {
            // Add selected seats to the shopping cart
            ShoppingCart.getInstance().addSeats(selectedSeats);
            System.out.println("Seats added to cart: " + selectedSeats);
    
            // Mark seats as sold in the database
            markSeatsAsSold(selectedSeats);
            for (String seatId : selectedSeats) 
            {
                // Assuming you have a method to get the hall_id for a seat
                String hall_name = cart.getSelectedDaySessionAndHall().getHall();
                reduceHallCapacity(hall_name);
            }
    
            // Mark the selected seats as confirmed and change their color to red
            confirmedSeats.addAll(selectedSeats);
    
            for (String seatId : selectedSeats) 
            {
                System.out.println("THIS WORKED 242: " + seatId);
                Button seatButton = findSeatButtonById(seatId);
                if (seatButton != null) 
                {
                    seatButton.setStyle("-fx-background-color: RED;");
                    seatButton.setDisable(true);
                }
            }
    
            // Clear the selected seats after adding to cart
            selectedSeats.clear();
            updateShoppingCart();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private void reduceHallCapacity(String hall_name) throws Exception 
    {
        try (Connection conn = DataBaseHandler.getConnection()) 
        {
            // Fetch session ID based on the hall name
            String querySessionId = "SELECT session_id FROM Sessions WHERE hall_name = ?";
            PreparedStatement stmtSessionId = conn.prepareStatement(querySessionId);
            stmtSessionId.setString(1, hall_name);
            ResultSet rsSession = stmtSessionId.executeQuery();
            
            int sessionId = -1;
            if (rsSession.next()) 
            {
                sessionId = rsSession.getInt("session_id");
            }
            
            if (sessionId == -1) 
            {
                throw new SQLException("No session found for hall: " + hall_name);
            }
            
            // Now reduce the vacant seats for the correct session ID
            String query = "UPDATE Sessions SET vacant_seats = vacant_seats - 1 WHERE session_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, sessionId);  // Pass the session ID instead of hall_name
            stmt.executeUpdate();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    
    
    private void markSeatsAsSold(List<String> seatLabels) throws Exception 
    {
        // Query to mark seats as occupied
        System.out.println("Marking seats as sold: " + seatLabels);
        String markSeatQuery = "UPDATE Seats SET is_occupied = TRUE WHERE seat_label = ? AND session_id = ?";
    
        // Query to decrement the vacant seats count
        String updateVacantSeatsQuery = "UPDATE Sessions SET vacant_seats = vacant_seats - ? WHERE session_id = ?";
    
        try (Connection conn = DataBaseHandler.getConnection()) 
        {
    
            try (PreparedStatement markSeatStmt = conn.prepareStatement(markSeatQuery))
            {

                PreparedStatement updateVacantSeatsStmt = conn.prepareStatement(updateVacantSeatsQuery);
                int sessionId = ShoppingCart.getInstance().getSelectedDaySessionAndHall().getSessionId();
    
                // Mark each seat as occupied
                for (String seatLabel : seatLabels) 
                {
                    markSeatStmt.setString(1, seatLabel); // Set seat label
                    markSeatStmt.setInt(2, sessionId);   // Set session ID
                    markSeatStmt.addBatch();
                }
                // Execute batch to update all seats
                markSeatStmt.executeBatch();
    
                // Update vacant seats count
                updateVacantSeatsStmt.setInt(1, seatLabels.size()); // Number of seats booked
                updateVacantSeatsStmt.setInt(2, sessionId);         // Session ID
                updateVacantSeatsStmt.executeUpdate();
    
                // Commit the transaction
            } 
            catch (SQLException e) 
            {
                // Rollback in case of error
                throw e;
            } 
            
        }
    }
    
    

    private Button findSeatButtonById(String seatId) 
    {
        for (Node node : seatsGridPane.getChildren()) 
        { // Assuming seats are in a GridPane named seatsGridPane
            if (node instanceof Button && seatId.equals(node.getId())) 
            {
                System.out.println("Found seat button: " + seatId);
                return (Button) node;
            }
        }
        return null;
    }

    private void unmarkSeatsAsAvailable(List<String> seatLabels) throws Exception 
    {
        // Query to mark seats as available
        String unmarkSeatQuery = "UPDATE Seats SET is_occupied = FALSE WHERE seat_label = ? AND session_id = ?";

        // Query to increment the vacant seats count
        String updateVacantSeatsQuery = "UPDATE Sessions SET vacant_seats = vacant_seats + ? WHERE session_id = ?";

        try (Connection conn = DataBaseHandler.getConnection()) 
        {
            try (PreparedStatement unmarkSeatStmt = conn.prepareStatement(unmarkSeatQuery);
                PreparedStatement updateVacantSeatsStmt = conn.prepareStatement(updateVacantSeatsQuery)) 
            {
                int sessionId = ShoppingCart.getInstance().getSelectedDaySessionAndHall().getSessionId();

                // Unmark each seat as available
                for (String seatLabel : seatLabels) 
                {
                    unmarkSeatStmt.setString(1, seatLabel); // Set seat label
                    unmarkSeatStmt.setInt(2, sessionId);    // Set session ID
                    unmarkSeatStmt.addBatch();
                }

                // Execute batch to update all seats
                unmarkSeatStmt.executeBatch();

                // Update vacant seats count
                updateVacantSeatsStmt.setInt(1, seatLabels.size()); // Number of seats released
                updateVacantSeatsStmt.setInt(2, sessionId);         // Session ID
                updateVacantSeatsStmt.executeUpdate();
            } 
            catch (SQLException e) 
            {
                // Rollback in case of error
                throw e;
            } 
        }
    }


    private void showAlert(String title, String message) 
    {
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
        // Unmark confirmed seats as available in the database
        try 
        {
            unmarkSeatsAsAvailable(confirmedSeats);
            System.out.println("Seats marked as available: " + confirmedSeats);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        // Reset seat colors on the UI and enable them again
        for (String seatId : confirmedSeats) 
        {
            Button seatButton = findSeatButtonById(seatId);
            if (seatButton != null) 
            {
                seatButton.setStyle("-fx-background-color: GREEN;"); // Reset to available (green)
                seatButton.setDisable(false); // Enable the button
            }
        }

        // Clear confirmed seats
        confirmedSeats.clear();

        // Load the Step1 FXML file
        Parent backRoot = FXMLLoader.load(getClass().getResource("/help/fxml/step1.fxml"));
        
        // Get the controller for Step 1 (this is required to pass data)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/help/fxml/step2.fxml"));
        backRoot = loader.load();
        
        Step2Controller step2Controller = loader.getController();
        
        // Pass the selected movie to Step 1
        ShoppingCart cart = ShoppingCart.getInstance();
        cart.clearSeats();//noo
        step2Controller.updateSelectedMovie(cart.getSelectedMovie());

        // Get the current scene
        Scene scene = back_button_step3a.getScene();
        
        // Set the new root to the current scene
        scene.setRoot(backRoot);

        // Optionally, update the stage title if needed
        Stage stage = (Stage) back_button_step3a.getScene().getWindow();
        stage.setTitle("Step 1");
        
        // Ensure the stage remains in fullscreen
        stage.setFullScreen(true);
        stage.setFullScreenExitHint(""); // Hide the exit hint
    }

    @FXML
    private void handleSignOutButtonAction(ActionEvent event) throws IOException 
    {
        // Load the login.fxml
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/help/fxml/login.fxml"));
        ShoppingCart cart = ShoppingCart.getInstance();
        cart.clear();

        // Get the current stage
        Stage stage = (Stage) SignoutButton.getScene().getWindow();

        // Set the new root to the current scene
        Scene scene = SignoutButton.getScene();
        scene.setRoot(loginRoot);

        // Update the stage title if needed
        stage.setTitle("Login");

        // Exit fullscreen mode
        stage.setFullScreen(false);
        stage.setFullScreenExitHint(""); // Hide the exit hint
    }
}