package help.utilities;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import help.classes.Product;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

public class ProductDBO 
{



    private byte[] getBlobData(Blob blob) throws SQLException 
    {
        if (blob == null) return null;
        try (InputStream is = blob.getBinaryStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new SQLException("Error reading BLOB data", e);
        }
    }

    /**
     * Retrieves a list of products from the database.
     *
     * @return A list of Product objects.
     * @throws Exception 
     */
    public List<Product> loadProducts() throws Exception 
    {
        List<Product> products = new ArrayList<>();
        String query = "SELECT image, name, price, stock_quantity, tax_rate FROM Products";
    
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                // Fetch columns from ResultSet with correct column names and types
                Blob imageBlob = rs.getBlob("image");
                byte[] imageData = getBlobData(imageBlob);
                String name = rs.getString("name");
                BigDecimal price = rs.getBigDecimal("price");
                int stockQuantity = rs.getInt("stock_quantity");
                BigDecimal taxRate = rs.getBigDecimal("tax_rate");
    
                // Create a Product object and add it to the list
                products.add(new Product(imageData, name, price, stockQuantity, taxRate));
            }
        }
    
        return products;
    }
    /**
     * Updates the stock quantity of a specific product in the database.
     *
     * @param productName The name of the product whose stock is to be updated.
     * @param quantity    The quantity to subtract from the current stock.
     * @throws Exception if a database access error occurs or if the stock is insufficient
     */
    public void updateProductStock(String productName, int quantity) throws Exception 
    {
        System.out.println("Updating stock for product: " + productName);
        System.out.println("Quantity to subtract: " + quantity);
        String updateQuery = "UPDATE Products SET stock_quantity = stock_quantity - ? WHERE name = ? AND stock_quantity >= ?";

        try (Connection conn = DataBaseHandler.getConnection();
            PreparedStatement stmt = conn.prepareStatement(updateQuery)) 
            {

            stmt.setInt(1, quantity);
            stmt.setString(2, productName);
            stmt.setInt(3, quantity);

            // Execute the update and get number of affected rows
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0)
             {
                throw new Exception("Failed to update stock - insufficient quantity or product not found");
            }
                
        } 
        catch (SQLException e) 
        {
            throw new Exception("Error updating stock for product: " + productName, e);
        }
        System.out.println("Stock successfully updated. Quantity subtracted: " + quantity);
    }
    public void getTaxRate() throws Exception 
    {
        String query = "SELECT tax_rate FROM Products";
        try (Connection connection = DataBaseHandler.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Tax rate: " + resultSet.getDouble("tax_rate"));
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }   
    }

    public void returnProductStock(String productName, int quantity) throws Exception 
        {
        System.out.println("Returning stock for product: " + productName);
        String updateQuery = "UPDATE Products SET stock_quantity = stock_quantity + ? WHERE name = ?";

        try (Connection conn = DataBaseHandler.getConnection();
            PreparedStatement stmt = conn.prepareStatement(updateQuery)) 
            {
            
            stmt.setInt(1, quantity);
            stmt.setString(2, productName);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) 
            {
                throw new Exception("Failed to return stock - product not found: " + productName);
            }
        }
        System.out.println("Stock successfully returned. Quantity added: " + quantity);
    }
}
