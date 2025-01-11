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



    // Add method to get tickets

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