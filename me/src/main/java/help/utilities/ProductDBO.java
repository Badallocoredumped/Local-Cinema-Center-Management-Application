package help.utilities;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import help.classes.Product;

import java.io.ByteArrayInputStream;
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
    public List<Product> loadProducts() throws Exception {
        List<Product> products = new ArrayList<>();
        String query = "SELECT image, name, price, stock_quantity, tax_rate, category FROM Products";
    
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                Blob imageBlob = rs.getBlob("image");
                byte[] imageData = getBlobData(imageBlob);
                String name = rs.getString("name");
                BigDecimal price = rs.getBigDecimal("price");
                int stockQuantity = rs.getInt("stock_quantity");
                BigDecimal taxRate = rs.getBigDecimal("tax_rate");
                String category = rs.getString("category");
    
                products.add(new Product(imageData, name, price, stockQuantity, taxRate, category));
            }
        }
    
        return products;
    }


    public boolean insertProduct(Product product) throws Exception 
    {
        String query = "INSERT INTO Products (image, name, price, stock_quantity, tax_rate, category) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataBaseHandler.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Set image BLOB
            if (product.getImageData() != null && product.getImageData().length > 0) {
                stmt.setBlob(1, new ByteArrayInputStream(product.getImageData()));
            } else {
                stmt.setNull(1, java.sql.Types.BLOB);
            }
            
            // Set other fields
            stmt.setString(2, product.getName());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setInt(4, product.getStockAvailability());
            stmt.setBigDecimal(5, product.getTaxRate());
            stmt.setString(6, product.getCategory());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteProduct(String productName) throws Exception {
        // Queries
        String checkQuery = "SELECT COUNT(*) FROM ticket_products WHERE product_name = ?";
        String disableFKChecks = "SET foreign_key_checks = 0";
        String enableFKChecks = "SET foreign_key_checks = 1";
        String deleteQuery = "DELETE FROM Products WHERE name = ?";
        
        try (Connection conn = DataBaseHandler.getConnection()) {
            // Disable foreign key checks temporarily
            try (Statement stmtFK = conn.createStatement()) {
                stmtFK.executeUpdate(disableFKChecks);
            }
    
            // Check if product is associated with any tickets
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, productName);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // Re-enable foreign key checks and throw exception if product has associated tickets
                    try (Statement stmtFK = conn.createStatement()) {
                        stmtFK.executeUpdate(enableFKChecks);
                    }
                    throw new SQLException("Cannot delete product: It is associated with existing tickets");
                }
            }
    
            // Proceed with deleting the product from the Products table
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, productName);
                int rowsAffected = deleteStmt.executeUpdate();
                
                // Re-enable foreign key checks after the delete operation
                try (Statement stmtFK = conn.createStatement()) {
                    stmtFK.executeUpdate(enableFKChecks);
                }
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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

    public boolean updateProduct(String oldName, Product product) throws Exception {
        String disableFKChecks = "SET foreign_key_checks = 0";
        String enableFKChecks = "SET foreign_key_checks = 1";
        String query = "UPDATE Products SET image = ?, name = ?, price = ?, stock_quantity = ?, tax_rate = ?, category = ? WHERE name = ?";
    
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Disable foreign key checks temporarily
            try (Statement stmtFK = conn.createStatement()) {
                stmtFK.executeUpdate(disableFKChecks);
            }
    
            // Set image BLOB
            if (product.getImageData() != null && product.getImageData().length > 0) {
                stmt.setBlob(1, new ByteArrayInputStream(product.getImageData()));
            } else {
                stmt.setNull(1, java.sql.Types.BLOB);
            }
    
            // Set other fields
            stmt.setString(2, product.getName());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setInt(4, product.getStockAvailability());
            stmt.setBigDecimal(5, product.getTaxRate());
            stmt.setString(6, product.getCategory());
            stmt.setString(7, oldName);
    
            int rowsAffected = stmt.executeUpdate();
    
            // Re-enable foreign key checks
            try (Statement stmtFK = conn.createStatement()) {
                stmtFK.executeUpdate(enableFKChecks);
            }
    
            return rowsAffected > 0;
    
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean returnProductsToInventory(int ticketId) throws Exception {
        String sql = "UPDATE Products p " +
                    "JOIN Ticket_Products tp ON p.name = tp.product_name " +
                    "SET p.stock_quantity = p.stock_quantity + tp.quantity " +
                    "WHERE tp.ticket_id = ?";
                    
        try (Connection conn = DataBaseHandler.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ticketId);
            return pstmt.executeUpdate() > 0;
        }
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
