package help.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PriceDBO
{


    

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