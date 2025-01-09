package help.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

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
}