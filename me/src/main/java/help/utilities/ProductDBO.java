package help.utilities;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import help.classes.Product;

import java.math.BigDecimal;

public class ProductDBO {

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
}
