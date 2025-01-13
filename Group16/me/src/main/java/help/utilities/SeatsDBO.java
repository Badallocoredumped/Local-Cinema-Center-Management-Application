package help.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Base Object (DBO) class for managing seats-related database operations.
 */
public class SeatsDBO 
{
    /**
     * Unmarks the specified seats as available for the given session.
     * <p>
     * This method updates the seats in the Seats table to set their status as available (not occupied) 
     * and vacant for the given session. It processes a list of seat numbers and updates each seat's 
     * availability status.
     * </p>
     *
     * @param seats A list of seat numbers to be marked as available.
     * @param sessionId The session ID for which the seats are being unmarked.
     * @throws Exception If there is an error during the update.
     */
    public void unmarkSeatsAsAvailable(List<String> seats, int sessionId) throws Exception 
    {
        String query = "UPDATE Seats SET is_occupied = 0, vacant = 1 WHERE seat_number = ? AND session_id = ?";
        
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) 
             {
            
            for (String seatNumber : seats) 
            {
                stmt.setString(1, seatNumber);
                stmt.setInt(2, sessionId);
                int rowsAffected = stmt.executeUpdate();
                System.out.println("Unmarking seat " + seatNumber + ". Rows affected: " + rowsAffected);
            }
        }
    }

    /**
     * Updates the vacant status of all seats for a given session based on their occupancy.
     * <p>
     * This method updates the vacant status for seats in the Seats table. If a seat is not occupied 
     * (i.e., `is_occupied` is 0), it is marked as vacant (i.e., `vacant` is set to 1). If the seat is 
     * occupied, the `vacant` status is set to 0. This update is applied for the specified session.
     * </p>
     *
     * @param sessionId The session ID for which the vacant status of seats needs to be updated.
     * @throws Exception If there is an error during the update.
     */
    public void updateVacantSeats(int sessionId) throws Exception 
    {
        String query = "UPDATE Seats SET vacant = CASE WHEN is_occupied = 0 THEN 1 ELSE 0 END WHERE session_id = ?";
        
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) 
        {   
            stmt.setInt(1, sessionId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Updated vacant status. Rows affected: " + rowsAffected);
        }
    }

    /**
     * Updates the occupancy status of seats for a given ticket.
     * <p>
     * This method updates the `is_occupied` status of seats associated with an active ticket. 
     * It first retrieves the session ID and the list of seat numbers from the ticket, and then 
     * updates the occupancy status of those seats. Additionally, the method adjusts the vacant 
     * seat count for the session accordingly.
     * </p>
     *
     * @param ticketId The ID of the ticket for which the seat occupancy status needs to be updated.
     * @param isOccupied A boolean indicating whether the seats are occupied (true) or unoccupied (false).
     * @return true if the seat occupancy was successfully updated, false otherwise.
     * @throws Exception If there is an error during the update, such as when the ticket is not found or 
     *                   when a database operation fails.
     */
    public boolean updateSeatOccupancy(int ticketId, boolean isOccupied) throws Exception 
    {
        Connection conn = null;
        try {
                conn = DataBaseHandler.getConnection();
                conn.setAutoCommit(false);

                // Get session_id and seat_numbers from ticket
                String ticketSQL = "SELECT session_id, seat_numbers FROM Tickets WHERE ticket_id = ? AND status = 'ACTIVE'";
                
                int sessionId;
                String seatNumbers;
                
                try (PreparedStatement pstmt = conn.prepareStatement(ticketSQL)) {
                    pstmt.setInt(1, ticketId);
                    ResultSet rs = pstmt.executeQuery();
                    if (!rs.next()) {
                        throw new Exception("Active ticket not found");
                    }
                    sessionId = rs.getInt("session_id");
                    seatNumbers = rs.getString("seat_numbers");
                }

                // Update is_occupied for each seat
                String updateSeatsSQL = "UPDATE Seats SET is_occupied = ? " +
                                    "WHERE session_id = ? AND seat_label IN (" + 
                                    String.join(",", Arrays.stream(seatNumbers.split(","))
                                        .map(label -> "'" + label.trim() + "'")
                                        .collect(Collectors.toList())) + ")";
                
                try (PreparedStatement pstmt = conn.prepareStatement(updateSeatsSQL)) {
                    pstmt.setBoolean(1, isOccupied);
                    pstmt.setInt(2, sessionId);
                    pstmt.executeUpdate();
                }

                // Update session vacant seats
                int seatCount = seatNumbers.split(",").length;
                String sessionSQL = "UPDATE Sessions SET vacant_seats = vacant_seats " + 
                                (isOccupied ? "- ?" : "+ ?") + 
                                " WHERE session_id = ?";
                                
                try (PreparedStatement pstmt = conn.prepareStatement(sessionSQL)) {
                    pstmt.setInt(1, seatCount);
                    pstmt.setInt(2, sessionId);
                    pstmt.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (Exception e) {
                if (conn != null) {
                    conn.rollback();
                }
                throw e;
            } finally {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            }
    }
    
}