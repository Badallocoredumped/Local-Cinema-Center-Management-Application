package help.utilities;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHandler
{
    private static final String URL = "jdbc:mysql://localhost:3306/cinemacenter";
    private static final String USER = "myuser";
    private static final String PASSWORD = "1234";

    public static Connection getConnection() throws Exception
    {
        System.out.println("Connecting to database...");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    

    /**
     * Authenticates a user by checking the provided username and password against the database.
     * 
     * @param username The username provided by the user for authentication.
     * @param password The password provided by the user for authentication.
     * @return The role of the user if the authentication is successful, otherwise null.
     *         Returns the role as a String (e.g., "admin", "user"), or null if authentication fails.
     */
    public static String authenticate(String username, String password)
    {
        String role = null;
        try (Connection conn = getConnection())
        {
        

            String query = "SELECT role FROM Users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                role = rs.getString("role");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return role;
    }

    /**
     * Searches for movies in the database based on the specified genre.
     * 
     * <p>This method queries the database for movie titles that match the given genre. It performs 
     * a case-insensitive search using the LIKE operator, which allows for partial matches.</p>
     * 
     * @param genre The genre of movies to search for. This parameter can be a partial or full genre name.
     * 
     * @return A list of movie titles that match the provided genre. If no movies are found, an empty 
     *         list is returned.
     */
    public static List<String> searchByGenre(String genre)
    {
        List<String> movies = new ArrayList<>();
        try (Connection conn = getConnection())
        {
            String query = "SELECT title FROM Movies WHERE genre LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + genre + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                movies.add(rs.getString("title"));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return movies;
    }

    /**
     * Searches for movie titles in the database based on a partial name.
     * 
     * <p>This method queries the database for movie titles that contain the specified partial name. 
     * It performs a case-insensitive search using the LIKE operator, which allows for matching any 
     * part of the movie title.</p>
     * 
     * @param partialName The partial or full name of the movie to search for. This parameter can be 
     *                    any substring within the title.
     * 
     * @return A list of movie titles that contain the provided partial name. If no movies are found, 
     *         an empty list is returned.
     */
    public static List<String> searchByPartialName(String partialName)
    {
        List<String> movies = new ArrayList<>();
        try (Connection conn = getConnection())
        {
            String query = "SELECT title FROM Movies WHERE title LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + partialName + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                movies.add(rs.getString("title"));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return movies;
    }

    /**
     * Searches for movie titles in the database that exactly match the given full name.
     * 
     * <p>This method queries the database for movie titles that exactly match the specified full name 
     * using the equality operator in SQL.</p>
     * 
     * @param fullName The exact name of the movie to search for.
     * 
     * @return A list of movie titles that exactly match the provided full name. If no movies are found, 
     *         an empty list is returned.
     */
    public static List<String> searchByFullName(String fullName)
    {
        List<String> movies = new ArrayList<>();
        try (Connection conn = getConnection())
        {
            String query = "SELECT title FROM Movies WHERE title = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, fullName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                movies.add(rs.getString("title"));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return movies;
    }

    /**
     * Retrieves the available seats for a given session where no customer has been assigned to the seat.
     * 
     * <p>This method queries the database for seat numbers in a specific session that are still available 
     * (i.e., seats that are not yet occupied, determined by the absence of a customer name in the database).</p>
     * 
     * @param sessionId The ID of the session for which available seats are to be retrieved.
     * 
     * @return A list of seat numbers that are available (i.e., not yet assigned to a customer) in the specified session.
     *         If no available seats are found, an empty list is returned.
     */
    public static List<Integer> getAvailableSeats(int sessionId)
    {
        List<Integer> availableSeats = new ArrayList<>();
        try (Connection conn = getConnection())
        {
            String query = "SELECT seat_number FROM Tickets WHERE session_id = ? AND customer_name IS NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                availableSeats.add(rs.getInt("seat_number"));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return availableSeats;
    }

    /**
     * Selects a seat for a given session by reserving it for a customer.
     * 
     * <p>This method updates the database to mark a seat as reserved by assigning a placeholder customer name 
     * (e.g., "reserved"). The seat is identified by its session ID and seat number. The method ensures that only 
     * seats that are currently unreserved (i.e., where the customer name is NULL) can be selected.</p>
     * 
     * @param sessionId The ID of the session for which the seat selection is being made.
     * @param seatNumber The number of the seat being selected.
     * 
     * @return {@code true} if the seat was successfully selected and reserved, {@code false} otherwise.
     */
    public static boolean selectSeat(int sessionId, int seatNumber)
    {
        try (Connection conn = getConnection())
        {
            String query = "UPDATE Tickets SET customer_name = ? WHERE session_id = ? AND seat_number = ? AND customer_name IS NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "reserved"); // Placeholder for customer name
            stmt.setInt(2, sessionId);
            stmt.setInt(3, seatNumber);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the customer information for a given ticket.
     * 
     * <p>This method assigns a customer name to an existing ticket by updating the customer_name field in the 
     * Tickets table. The ticket is identified by its unique ticket ID, and the method updates the ticket with 
     * the specified customer name.</p>
     * 
     * @param ticketId The ID of the ticket to be updated.
     * @param customerName The name of the customer purchasing or reserving the ticket.
     */
    public static void updateTicketSales(int ticketId, String customerName)
    {
        try (Connection conn = getConnection())
        {
            String query = "UPDATE Tickets SET customer_name = ? WHERE ticket_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, customerName);
            stmt.setInt(2, ticketId);
            stmt.executeUpdate();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Updates the inventory of a product by reducing its stock quantity.
     * 
     * <p>This method decreases the stock quantity of a specified product in the Products table. The product is 
     * identified by its unique product ID, and the specified quantity is subtracted from its current stock quantity.</p>
     * 
     * @param productId The ID of the product whose inventory is being updated.
     * @param quantity The number of units to subtract from the product's stock quantity.
     */
    public static void updateProductInventory(int productId, int quantity)
    {
        try (Connection conn = getConnection())
        {
            String query = "UPDATE Products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}