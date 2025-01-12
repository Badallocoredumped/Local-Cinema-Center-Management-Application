package help.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import help.classes.Product;
import help.classes.TicketProduct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TicketProductsDBO {

    // Save ticket products to the database
    public void saveTicketProducts(int ticketId, Map<Product, Integer> itemsBought) throws Exception 
    {
        String sql = "INSERT INTO Ticket_Products (ticket_id, product_name, quantity, price) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DataBaseHandler.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) 
             {
            
            // Loop through each product and quantity
            for (Map.Entry<Product, Integer> entry : itemsBought.entrySet()) 
            {
                // Set parameters for each product in the batch insert
                pstmt.setInt(1, ticketId); // ticket_id
                pstmt.setString(2, entry.getKey().getName()); // product_name
                pstmt.setInt(3, entry.getValue()); // quantity
                pstmt.setDouble(4, entry.getKey().getPrice().doubleValue()); // price

                // Add to batch for execution
                pstmt.addBatch();
            }

            // Execute the batch insert
            pstmt.executeBatch();
        } 
        catch (SQLException e) 
        {
            // Handle any SQL exceptions, maybe log or throw again
            e.printStackTrace();
            throw new SQLException("Error saving ticket products to the database", e);
        }
    }

    public ObservableList<TicketProduct> getTicketProducts(int ticketId) throws Exception {
    ObservableList<TicketProduct> products = FXCollections.observableArrayList();
    String sql = "SELECT ticket_id, product_name, quantity, price FROM Ticket_Products WHERE ticket_id = ?";
    
    try (Connection conn = DataBaseHandler.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, ticketId);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            TicketProduct product = new TicketProduct(
                rs.getInt("ticket_id"),
                rs.getString("product_name"),
                rs.getInt("quantity"),
                rs.getBigDecimal("price")
            );
            products.add(product);
        }
    }
    return products;
}

    
    
    


    public void deleteTicketProducts(int ticketId) throws Exception 
    {
        String sql = "DELETE FROM Ticket_Products WHERE ticket_id = ?";
        
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) 
             {
            
            pstmt.setInt(1, ticketId);
            pstmt.executeUpdate();
            
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            throw new SQLException("Error deleting ticket products from database", e);
        }
    }
}
