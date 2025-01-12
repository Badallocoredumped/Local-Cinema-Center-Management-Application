package help.utilities;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import help.classes.TicketProduct;
import help.classes.Tickets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TicketsDBO {

    private static TicketsDBO instance;


    /**
     * Retrieves all tickets from the database.
     * <p>
     * This method executes a SELECT query to fetch all the ticket records from the `tickets` table.
     * It maps the retrieved data to `Tickets` objects and returns an `ObservableList` containing 
     * all the tickets.
     * </p>
     *
     * @return An `ObservableList` of `Tickets` objects containing all the ticket records from the database.
     * @throws SQLException If there is an error while executing the SELECT query or processing the database connection.
     */
    public ObservableList<Tickets> getTickets() throws Exception {
        ObservableList<Tickets> tickets = FXCollections.observableArrayList();
        String sql = "SELECT * FROM tickets";

        try (Connection conn = DataBaseHandler.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Tickets ticket = new Tickets();
                ticket.setTicketId(rs.getInt("ticket_id"));
                ticket.setSessionId(rs.getInt("session_id"));
                ticket.setCustomerName(rs.getString("customer_name"));
                ticket.setTotalSeatCost(rs.getDouble("total_seat_cost"));
                ticket.setTotalProductCost(rs.getDouble("total_product_cost"));
                ticket.setTotalTax(rs.getDouble("total_tax"));
                ticket.setTotalCost(rs.getDouble("total_cost"));
                ticket.setStatus(rs.getString("status"));
                tickets.add(ticket);
            }
        }
        return tickets;
    }

    /**
     * Retrieves the list of products associated with a specific ticket from the database.
     * <p>
     * This method executes a SELECT query to fetch product details (name, quantity, and price) 
     * from the `TicketProducts` table for a specific ticket, identified by its ticket ID.
     * It maps the retrieved data to `TicketProduct` objects and returns an `ObservableList` containing 
     * the products associated with the ticket.
     * </p>
     *
     * @param ticketId The ID of the ticket whose products are to be retrieved.
     * @return An `ObservableList` of `TicketProduct` objects containing the products associated with the specified ticket.
     * @throws SQLException If there is an error while executing the SELECT query or processing the database connection.
     */
    public ObservableList<TicketProduct> getTicketProducts(int ticketId) throws Exception 
    {
        ObservableList<TicketProduct> ticketProducts = FXCollections.observableArrayList();

        String sql = "SELECT product_name, quantity, price FROM TicketProducts WHERE ticket_id = ?";
        
        try (Connection conn = DataBaseHandler.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the ticket ID for the query
            pstmt.setInt(1, ticketId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String productName = rs.getString("product_name");
                    int quantity = rs.getInt("quantity");
                    BigDecimal price = rs.getBigDecimal("price");

                    // Create TicketProduct object and add to the list
                    TicketProduct product = new TicketProduct(ticketId, productName, quantity, price);
                    ticketProducts.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching ticket products.", e);
        }
        
        return ticketProducts;
    }

    /**
     * Creates a new ticket record in the database.
     * <p>
     * This method validates the provided ticket data, inserts the ticket information into the `tickets` table,
     * and returns the generated ticket ID. The ticket's status is set to "ACTIVE". If the insertion is successful, 
     * the method returns the generated ticket ID; otherwise, it throws an exception.
     * </p>
     *
     * @param ticket The `Tickets` object containing the ticket details to be inserted into the database.
     * @return The generated ticket ID if the ticket is successfully created.
     * @throws IllegalArgumentException If the provided `ticket` is null, or if required fields such as 
     *         seat numbers or customer name are missing.
     * @throws SQLException If there is an error during the SQL execution or retrieving the generated ticket ID.
     */
    public int createTicket(Tickets ticket) throws Exception 
    {
        // Input validation
        if (ticket == null) 
        {
            throw new IllegalArgumentException("Ticket cannot be null");
        }
        if (ticket.getSeatNumbers() == null || ticket.getSeatNumbers().isEmpty()) 
        {
            throw new IllegalArgumentException("Seat numbers cannot be empty");
        }
        if (ticket.getCustomerName() == null || ticket.getCustomerName().trim().isEmpty()) 
        {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }

        // SQL query
        String sql = "INSERT INTO tickets (session_id, seat_numbers, customer_name, " +
                    "total_seat_cost, total_product_cost, total_tax, total_cost,discounted_seat_numbers,status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, ticket.getSessionId());
            pstmt.setString(2, String.join(",", ticket.getSeatNumbers()));
            pstmt.setString(3, ticket.getCustomerName());
            pstmt.setDouble(4, ticket.getTotalSeatCost());
            pstmt.setDouble(5, ticket.getTotalProductCost());
            pstmt.setDouble(6, ticket.getTotalTax());
            pstmt.setDouble(7, ticket.getTotalCost());
            pstmt.setInt(8, ticket.getDiscountedSeatNumber());
            pstmt.setString(9, "ACTIVE");

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) 
            {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) 
                {
                    int ticketId = rs.getInt(1);
                    ticket.setTicketId(ticketId);
                    return ticketId;
                }
            }
            throw new SQLException("Failed to create ticket, no ID obtained.");
        }
    }

    /**
     * Deletes a ticket record from the database by its ticket ID.
     * <p>
     * This method removes a ticket from the `tickets` table using the provided ticket ID. It returns a boolean 
     * indicating whether the deletion was successful. If the deletion fails, an exception is thrown.
     * </p>
     *
     * @param ticketId The ID of the ticket to be deleted.
     * @return `true` if the ticket is successfully deleted, `false` if no rows were affected (i.e., no ticket found).
     * @throws SQLException If there is an error during the SQL execution.
     */
    public boolean deleteTicket(int ticketId) throws Exception {
        String sql = "DELETE FROM tickets WHERE ticket_id = ?";
        
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, ticketId);
            int affectedRows = pstmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting ticket: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Cancels a ticket by updating its status to 'CANCELLED'.
     * <p>
     * This method updates the `status` of the ticket in the `tickets` table to 'CANCELLED' based on the provided
     * ticket ID. It returns a boolean indicating whether the cancellation was successful. If the cancellation fails,
     * an exception is thrown.
     * </p>
     *
     * @param ticketId The ID of the ticket to be cancelled.
     * @return `true` if the ticket status was successfully updated to 'CANCELLED', `false` if no rows were affected
     *         (i.e., no ticket found with the provided ID).
     * @throws SQLException If there is an error during the SQL execution.
     */
    public boolean cancelTicket(int ticketId) throws Exception 
    {
        String sql = "UPDATE tickets SET status = 'CANCELLED' WHERE ticket_id = ?";
        
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, ticketId);
            int affectedRows = pstmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error cancelling ticket: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Resets the singleton instance of `TicketsDBO` and clears the provided ticket's data.
     * <p>
     * This method sets the `TicketsDBO` instance to `null` if it exists, effectively resetting the singleton instance.
     * Additionally, it clears the provided `ticket`'s data by calling its `clear()` method.
     * </p>
     *
     * @param ticket The ticket object whose data should be cleared.
     * @throws Exception If an error occurs while resetting the instance or clearing the ticket data.
     */
    public static void resetInstance(Tickets ticket) 
    {
        try 
        {
            if (instance != null) 
            {
                instance = null;
                ticket.clear();
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error resetting TicketsDBO instance: " + e.getMessage());
        }
    }

    

    
}