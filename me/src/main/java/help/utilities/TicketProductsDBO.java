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

    /**
     * Saves the products purchased with a ticket to the database.
     * <p>
     * This method inserts multiple records into the `Ticket_Products` table, associating each purchased 
     * product with a specific ticket. The products and their quantities are passed in a map, 
     * where each entry consists of a product and the quantity purchased. The method performs a batch insert 
     * to efficiently save all the records at once.
     * </p>
     *
     * @param ticketId The ID of the ticket for which the products were purchased.
     * @param itemsBought A map of products and their quantities, where the key is the product and the value is the quantity.
     * @throws SQLException If there is an error while inserting the products into the database.
     */
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

    /**
     * Retrieves a list of products associated with a specific ticket from the database.
     * <p>
     * This method executes a query to fetch all products related to a ticket identified by `ticketId`. 
     * It retrieves the product name, quantity, and price for each product, and stores them as `TicketProduct` 
     * objects in an observable list. This list is returned to the caller for further use.
     * </p>
     *
     * @param ticketId The ID of the ticket for which products are to be fetched.
     * @return An observable list of `TicketProduct` objects representing the products purchased with the specified ticket.
     * @throws SQLException If there is an error while querying the database or processing the result set.
     */
    public ObservableList<TicketProduct> getTicketProducts(int ticketId) throws Exception 
    {
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

    /**
     * Deletes all products associated with a specific ticket from the database.
     * <p>
     * This method executes a DELETE query to remove all entries in the `Ticket_Products` table
     * that are associated with the specified `ticketId`. The products will be completely removed 
     * from the database for the given ticket.
     * </p>
     *
     * @param ticketId The ID of the ticket whose products are to be deleted.
     * @throws SQLException If there is an error while executing the DELETE query or processing the database connection.
     */
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
