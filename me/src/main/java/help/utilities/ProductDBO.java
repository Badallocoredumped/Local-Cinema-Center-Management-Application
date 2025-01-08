package help.utilities;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import help.classes.Product;

import java.math.BigDecimal;

public class ProductDBO 
{

    /**
     * Retrieves a list of products from the database.
     *
     * @return A list of Product objects.
     * @throws Exception 
     */
    public List<Product> loadProducts() throws Exception {
        List<Product> products = new ArrayList<>();
        String query = "SELECT image_path, name, price, stock_quantity FROM Products";

        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Fetch columns from ResultSet with correct column names and types
                String imagePath = rs.getString("image_path");
                String name = rs.getString("name");
                BigDecimal price = rs.getBigDecimal("price");
                int stockQuantity = rs.getInt("stock_quantity");

                // Create a Product object and add it to the list
                products.add(new Product(imagePath, name, price, stockQuantity));
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
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setInt(1, quantity);
            stmt.setString(2, productName);
            stmt.setInt(3, quantity);


            
        } catch (SQLException e) {
            throw new Exception("Error updating stock for product: " + productName, e);
        }
        System.out.println("FINAL QUANTITY: " + quantity);

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
}
