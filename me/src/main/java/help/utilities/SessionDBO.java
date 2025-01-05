package help.utilities;

import help.classes.Session;
import help.classes.Movie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionDBO {

    /**
     * Retrieves a list of sessions for the specified movie.
     *
     * @param selectedMovie The movie for which to retrieve sessions.
     * @return A list of Session objects.
          * @throws Exception 
          */
         public List<Session> loadSessions(Movie selectedMovie) throws Exception {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT session_date, start_time, hall_name, vacant_seats FROM Sessions WHERE movie_id = ?";

        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, selectedMovie.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                sessions.add(new Session(
                    rs.getString("session_date"),  // session_date (as day)
                    rs.getString("start_time"),    // start_time (as session)
                    rs.getString("hall_name"),     // hall_name
                    rs.getString("vacant_seats"),  // vacant_seats
                    rs.getInt("session_id")        // session_id
                    ));
                
            }
        }

        return sessions;
    }
}