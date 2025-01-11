package help.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import help.classes.Tickets;

public class TicketsDBO {

    private static TicketsDBO instance;
    
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
                    "total_seat_cost, total_product_cost, total_tax, total_cost,discounted_seat_numbers) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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