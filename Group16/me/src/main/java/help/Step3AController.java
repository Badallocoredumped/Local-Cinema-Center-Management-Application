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
import help.classes.Tickets;
import help.utilities.DataBaseHandler;
import help.utilities.SeatsDBO;
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

/**
 * Controller class responsible for handling the actions of Step 3A in the cashier multi-step process.
 */
public class Step3AController 
{
    @FXML
    private Button next_button_step3a;
    @FXML
    private Button back_button_step3a;
    @FXML
    private Button add_to_cart_button;
    @FXML
    private Button SignoutButton;
    @FXML
    private Button CloseButton;
    @FXML
    private Button MinimizeButton; 

    @FXML
    private Label selectedSeat;
    @FXML
    private Label selectedMovieLabel;
    @FXML
    private Label selectedSession;


    @FXML
    private Button A1, A2, A3, A4;
    @FXML
    private Button B1, B2, B3, B4;
    @FXML
    private Button C1, C2, C3, C4;
    @FXML
    private Button D1, D2, D3, D4;

    @FXML
    private GridPane seatsGridPane;
    
    private List<String> selectedSeats = new ArrayList<>();
    private List<String> confirmedSeats = new ArrayList<>();

    /**
     * Handles the action when the close button is clicked.
     * Closes the current stage (window).
     *
     * @param event The action event that triggers this method.
     */
    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the action when the minimize button is clicked.
     * Minimizes the current stage (window).
     *
     * @param event The action event that triggers this method.
     */
    @FXML
    private void handleMinimizeButtonAction(ActionEvent event) {
        Stage stage = (Stage) MinimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Initializes the view with the selected movie and session details, and sets up the seat buttons.
     * This method is called when the controller is initialized and sets up the interface based on the 
     * session and movie in the shopping cart.
     * 
     * @throws Exception If an error occurs while fetching session or movie data.
     */
    @FXML
    private void initialize() throws Exception
    {
        ShoppingCart cart = ShoppingCart.getInstance();
        Session session = cart.getSelectedDaySessionAndHall();
        next_button_step3a.setDisable(true);


        selectedMovieLabel.setText(cart.getSelectedMovie().getTitle());
        selectedSession.setText((
            session.getSession() + "\n" +
            session.getDay() + "\n" +
            session.getHall()));

        // Access the selected session
        // Initialize seat buttons
        initializeSeatButtons();
    }

    /**
     * Fetches the list of sold seats for the currently selected session from the database.
     * 
     * @return A list of seat labels representing the sold seats.
     * @throws Exception If an error occurs while querying the database.
     */
    private List<String> fetchSoldSeats() throws Exception {
        List<String> soldSeats = new ArrayList<>();
        ShoppingCart cart = ShoppingCart.getInstance();
        Session session = cart.getSelectedDaySessionAndHall(); // This should give you the session
    
        try (Connection conn = DataBaseHandler.getConnection()) {
            // Query to fetch sold seats by session_id only
            String query = "SELECT seat_label FROM Seats WHERE session_id = ? AND is_occupied = TRUE";
            PreparedStatement stmt = conn.prepareStatement(query);
    
            // Pass in the selected session_id
            stmt.setInt(1, session.getSessionId()); // session_id
    
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                soldSeats.add(rs.getString("seat_label"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        System.out.println("Sold seats: " + soldSeats.size());
        return soldSeats;
    }
    
    

    /**
     * Initializes the seat buttons based on the availability of the seats.
     * Marks seats as available (green), sold (red), or selected (yellow) based on their state.
     * 
     * @throws Exception If an error occurs while fetching sold seat data.
     */
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
    

    /**
         * Handles the selection and deselection of a seat by the user.
         * Updates the seat's color to yellow when selected and green when deselected.
         * 
         * @param seatButton The button representing the selected seat.
         */
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
    
    

    /**
     * Updates the shopping cart with the currently selected seats.
     * Displays the updated seat list in the UI and ensures seats are added to the cart.
     */
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
    

    

    /**
     * Handles the action of adding selected seats to the shopping cart.
     * Checks if the seats are already in the cart and if not, adds them, marks them as sold,
     * and disables the seat buttons in the UI.
     * 
     * @throws Exception If an error occurs while adding the seats to the cart or marking them as sold.
     */
    @FXML
    private void handleAddToCartAction() throws Exception
    {
        List<String> cartSeats = ShoppingCart.getInstance().getSelectedSeats();
        ShoppingCart cart = ShoppingCart.getInstance();

        if (selectedSeats == null || selectedSeats.isEmpty()) 
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Seats Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select at least one seat before adding to cart.");
            
            // Get main stage and configure alert
            Stage mainStage = (Stage) MinimizeButton.getScene().getWindow();
            alert.initOwner(mainStage);
            alert.initModality(Modality.APPLICATION_MODAL);
            
            // Configure alert stage
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.setAlwaysOnTop(true);
            
            alert.showAndWait();
            return;
        }
        next_button_step3a.setDisable(false);
    
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
                reduceHallCapacity(cart.getSelectedDaySessionAndHall().getSessionId());
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

    /**
     * Reduces the vacant seat count for the given session by 1. If no vacant seats are available, an exception is thrown.
     * 
     * @param sessionId the ID of the session for which the vacant seat count will be reduced
     * @throws SQLException if no vacant seats are available or there is an issue updating the database
     */
    private void reduceHallCapacity(int sessionId) throws Exception 
    {
        try (Connection conn = DataBaseHandler.getConnection()) 
        {
            // Get the current vacant seats for the given session ID
            int currentVacantSeats = getVacantSeatsForSession(sessionId);
    
            // Ensure there are vacant seats available to reduce
            if (currentVacantSeats <= 0) 
            {
                throw new SQLException("No vacant seats available for session ID: " + sessionId);
            }
    
            // Reduce vacant seats by 1 (or by the desired number of seats)
            int updatedVacantSeats = currentVacantSeats - 1;  // Decrement by 1
    
            // Now, update the vacant seats for the session to the new value
            String updateQuery = "UPDATE Sessions SET vacant_seats = ? WHERE session_id = ?";
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            
            // Set the updated value of vacant seats
            stmt.setInt(1, updatedVacantSeats);  // Set the updated vacant seats
            stmt.setInt(2, sessionId);           // Set the session ID
    
            // Execute the update
            stmt.executeUpdate();
    
            System.out.println("Updated vacant seats to " + updatedVacantSeats + " for session ID " + sessionId);
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            throw e;  // Rethrow the exception after logging it
        }
    }
    
    

    /**
     * Retrieves the number of vacant seats for a given session.
     * 
     * @param sessionId the ID of the session for which to get the vacant seat count
     * @return the number of vacant seats for the session
     * @throws SQLException if no session is found with the provided session ID or there is an issue querying the database
    */
    private int getVacantSeatsForSession(int sessionId) throws Exception 
    {
        int vacantSeats = -1;  // Default value in case no data is found.
    
        // SQL query to get the current number of vacant seats for a session
        String query = "SELECT vacant_seats FROM Sessions WHERE session_id = ?";
    
        // Try-with-resources to ensure the connection is closed properly
        try (Connection conn = DataBaseHandler.getConnection()) 
        {
            // Prepare the statement and set the session ID
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, sessionId);
    
            // Execute the query and get the result
            ResultSet rs = stmt.executeQuery();
    
            // If a result is returned, extract the vacant seats
            if (rs.next()) 
            {
                vacantSeats = rs.getInt("vacant_seats");
            } 
            else 
            {
                // If no result is found for the session ID
                throw new SQLException("No session found with session_id: " + sessionId);
            }
        }
    
        // Return the number of vacant seats (can be 0 or more)
        return vacantSeats;
    }
    
    
    
    /**
     * Marks a list of seats as sold (occupied) in the database.
     * 
     * @param seatLabels a list of seat labels to be marked as sold
     * @throws SQLException if there is an issue updating the database
     */
    private void markSeatsAsSold(List<String> seatLabels) throws Exception 
    {
        // Query to mark seats as occupied
        System.out.println("Marking seats as sold: " + seatLabels);
        String markSeatQuery = "UPDATE Seats SET is_occupied = TRUE WHERE seat_label = ? AND session_id = ?";
    
        try (Connection conn = DataBaseHandler.getConnection()) 
        {
            try (PreparedStatement markSeatStmt = conn.prepareStatement(markSeatQuery)) 
            {
                int sessionId = ShoppingCart.getInstance().getSelectedDaySessionAndHall().getSessionId();
    
                // Mark each seat as occupied
                for (String seatLabel : seatLabels) 
                {
                    markSeatStmt.setString(1, seatLabel); // Set seat label
                    markSeatStmt.setInt(2, sessionId);    // Set session ID
                    markSeatStmt.addBatch();
                }
    
                // Execute batch to update all seats
                markSeatStmt.executeBatch();
            } 
            catch (SQLException e) 
            {
                // Rollback in case of error
                throw e;
            }
        }
    }
    
    
    
    /**
     * Finds a seat button by its seat label.
     * 
     * @param seatId the ID of the seat to find
     * @return the Button corresponding to the given seat ID, or null if no matching button is found
     */
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

    /**
     * Marks a list of seats as available and updates the vacant seat count for the session.
     * 
     * @param seatLabels a list of seat labels to be unmarked as sold and made available again
     * @throws SQLException if there is an issue updating the database
     */
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


    /**
     * Displays an informational alert to the user with the given title and message.
     * 
     * @param title the title of the alert
     * @param message the message to display in the alert
     */
    private void showAlert(String title, String message) 
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handles the action when the "Next" button is pressed. Loads the next step's FXML and updates the scene.
     * 
     * @throws IOException if there is an issue loading the FXML file for the next step
     */
    @FXML
    private void handleNextButtonAction() throws IOException 
    {
        // Load the step2.fxml
        Parent step2Root = FXMLLoader.load(getClass().getResource("/help/fxml/step4.fxml"));

        // Get the current scene from the Next button
        Scene scene = next_button_step3a.getScene();

        // Set the new root to the current scene
        scene.setRoot(step2Root);

        // Optionally, update the stage title if needed
        Stage stage = (Stage) next_button_step3a.getScene().getWindow();
        stage.setTitle("Step 4");
        
        // Ensure the stage remains in fullscreen
        stage.setFullScreen(true);
        stage.setFullScreenExitHint(""); // Hide the exit hint
    }

    /**
     * Handles the action when the "Back" button is pressed. Unmarks confirmed seats as available and loads the previous step's FXML.
     * 
     * @throws IOException if there is an issue loading the FXML file for the previous step
     */
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
        cart.clearSession();
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

    /**
     * Handles the sign-out action when the user clicks the "Sign Out" button.
     *
     * @param event The action event triggered when the "Sign Out" button is clicked.
     */
    @FXML
    private void handleSignOutButtonAction(ActionEvent event) {
        try {
            // Get instances
            ShoppingCart cart = ShoppingCart.getInstance();
            Tickets ticket = Tickets.getInstance();
            
            // Return seats to available pool
            SeatsDBO seatsDBO = new SeatsDBO();
            Session session = cart.getSelectedDaySessionAndHall();

            List<String> seatsToUnmark = cart.getSelectedSeats();
            // Unmark seats in database if there are any selected seats
            if (!seatsToUnmark.isEmpty()) 
            {
                try 
                {
                    // Create SQL queries
                    String unmarkSeatQuery = "UPDATE Seats SET is_occupied = FALSE WHERE seat_label = ? AND session_id = ?";
                    String updateVacantSeatsQuery = "UPDATE Sessions SET vacant_seats = vacant_seats + ? WHERE session_id = ?";
        
                    try (Connection conn = DataBaseHandler.getConnection();
                        PreparedStatement unmarkSeatStmt = conn.prepareStatement(unmarkSeatQuery);
                        PreparedStatement updateVacantSeatsStmt = conn.prepareStatement(updateVacantSeatsQuery)) {
                        
                        int sessionId = cart.getSelectedDaySessionAndHall().getSessionId();
        
                        // Unmark each seat as available
                        for (String seatLabel : seatsToUnmark) 
                        {
                            unmarkSeatStmt.setString(1, seatLabel);
                            unmarkSeatStmt.setInt(2, sessionId);
                            unmarkSeatStmt.addBatch();
                        }
        
                        // Execute batch to update all seats
                        unmarkSeatStmt.executeBatch();
        
                        // Update vacant seats count
                        updateVacantSeatsStmt.setInt(1, seatsToUnmark.size());
                        updateVacantSeatsStmt.setInt(2, sessionId);
                        updateVacantSeatsStmt.executeUpdate();
                    }
                } 
                catch (SQLException e) 
                {
                    e.printStackTrace();
                    throw e;
                }
            }

            

            // Reset instances
            cart.clear();
            Tickets.resetInstance();

            // Navigate to login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/help/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) SignoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(false); // Ensure full screen
            stage.show();



        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Sign Out Failed");
            alert.setHeaderText("Error During Sign Out");
            alert.setContentText("Failed to properly sign out: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}