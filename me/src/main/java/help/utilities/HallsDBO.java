package help.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HallsDBO {

    /**
     * Reduces the capacity of the specified hall by 1.
     *
     * @param hallId The ID of the hall whose capacity is to be reduced.
          * @throws Exception 
          */
         /* public void reduceHallCapacity(int hallId) throws Exception {
        String query = "UPDATE Halls SET capacity = capacity - 1 WHERE hall_id = ?";

        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, hallId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No hall found with ID: " + hallId);
            }
        }
    } */
}