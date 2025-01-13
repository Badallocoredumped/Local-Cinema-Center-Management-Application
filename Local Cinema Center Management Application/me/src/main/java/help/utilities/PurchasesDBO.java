package help.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * Data Base Object (DBO) class for managing purchase-related database operations.
 */
public class PurchasesDBO {

    /**
     * Marks the specified seats as sold by inserting them into the purchases table.
     *
     * @param seats A list of seat IDs to be marked as sold.
     * @throws Exception 
     */
    public void markSeatsAsSold(List<String> seats) throws Exception {
        String query = "INSERT INTO purchases (seat_id) VALUES (?)";

        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            for (String seat : seats) {
                pstmt.setString(1, seat);
                pstmt.addBatch();
            }

            pstmt.executeBatch();
        }
    }
}