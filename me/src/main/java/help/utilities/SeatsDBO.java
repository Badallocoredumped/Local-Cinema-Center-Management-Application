package help.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SeatsDBO 
{
    
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