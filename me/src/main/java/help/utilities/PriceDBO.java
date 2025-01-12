package help.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PriceDBO
{
    /**
     * Retrieves the base price for a seat in a specific hall.
     * 
     * <p>This method queries the "TicketPricing" table in the database to retrieve the base price for the specified hall.</p>
     * 
     * @param hallName The name of the hall for which the seat price is being retrieved.
     * 
     * @return The base price for a seat in the specified hall. If no price is found, it returns 0.0.
     * 
     * @throws Exception If an error occurs while accessing the database.
     */
    public double getSeatPrice(String hallName) throws Exception {
        String query = "SELECT base_price FROM TicketPricing WHERE hall_name = ?";
        try (Connection connection = DataBaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, hallName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("base_price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Adds a new ticket pricing record for a specified hall.
     * 
     * <p>This method inserts a new row into the "TicketPricing" table, setting the hall name, base price, and discount rate.</p>
     * 
     * @param hallName The name of the hall for which the ticket pricing is being added.
     * @param basePrice The base price for a ticket in the specified hall.
     * @param discountRate The discount rate applicable to the tickets in the specified hall.
     * 
     * @throws Exception If an error occurs while accessing the database or executing the insert query.
     */
    public void addTicketPricing(String hallName, double basePrice, double discountRate) throws Exception {
        String query = "INSERT INTO TicketPricing (hall_name, base_price, discount_rate) VALUES (?, ?, ?)";
        try (Connection connection = DataBaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, hallName);
            statement.setDouble(2, basePrice);
            statement.setDouble(3, discountRate);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the ticket pricing for a specified hall.
     * 
     * <p>This method updates the base price and discount rate for a given ticket pricing record in the "TicketPricing" table based on the provided ID.</p>
     * 
     * @param id The unique identifier of the ticket pricing record to be updated.
     * @param basePrice The new base price for the ticket in the specified hall.
     * @param discountRate The new discount rate for the ticket in the specified hall.
     * 
     * @throws Exception If an error occurs while accessing the database or executing the update query.
     */
    public void updateTicketPricing(int id, double basePrice, double discountRate) throws Exception {
        String query = "UPDATE TicketPricing SET base_price = ?, discount_rate = ? WHERE id = ?";
        try (Connection connection = DataBaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, basePrice);
            statement.setDouble(2, discountRate);
            statement.setInt(3, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a ticket pricing record from the database based on the specified ID.
     * 
     * <p>This method removes the ticket pricing record from the "TicketPricing" table for the specified ID.</p>
     * 
     * @param id The unique identifier of the ticket pricing record to be deleted.
     * 
     * @throws Exception If an error occurs while accessing the database or executing the delete query.
     */
    public void deleteTicketPricing(int id) throws Exception {
        String query = "DELETE FROM TicketPricing WHERE id = ?";
        try (Connection connection = DataBaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the discount rate for a specified hall from the TicketPricing table.
     * 
     * <p>This method queries the database to fetch the discount rate for the given hall name.</p>
     * 
     * @param hallName The name of the hall for which the discount rate is to be retrieved.
     * 
     * @return The discount rate for the specified hall. Returns 0.0 if no discount rate is found or an error occurs.
     * 
     * @throws Exception If an error occurs while accessing the database or executing the query.
     */
    public double getSeatDiscount(String hallName) throws Exception {
        String query = "SELECT discount_rate FROM TicketPricing WHERE hall_name = ?";
        try (Connection connection = DataBaseHandler.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, hallName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("discount_rate");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}