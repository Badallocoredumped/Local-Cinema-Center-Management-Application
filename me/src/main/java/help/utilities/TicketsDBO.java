package help.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class TicketsDBO {

    

    public void saveTicket(int sessionId, int seatNumber, String customerName, int age, double totalSeatCost, double totalProductCost) throws Exception 
    {
        String insertTicketSQL = "INSERT INTO Tickets (session_id, seat_number, customer_name,total_seat_cost, total_product_cost) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DataBaseHandler.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertTicketSQL)) {

            preparedStatement.setInt(1, sessionId);
            preparedStatement.setInt(2, seatNumber);
            preparedStatement.setString(3, customerName);
            preparedStatement.setDouble(4, totalSeatCost);
            preparedStatement.setDouble(5, totalProductCost);

            preparedStatement.executeUpdate();
        }
    }
}