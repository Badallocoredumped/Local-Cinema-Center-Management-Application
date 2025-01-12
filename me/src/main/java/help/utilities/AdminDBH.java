package help.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import help.classes.Movie;
import help.classes.Schedule;
import help.classes.Session;
import javafx.beans.property.StringProperty;

public class AdminDBH 
{
    private static final String URL = "jdbc:mysql://localhost:3306/cinemacenter";
    private static final String USER = "myuser";
    private static final String PASSWORD = "1234";
    private static Connection dbconnection;
    
    
    public static Connection getConnection() throws Exception
    {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public AdminDBH()
    {
        try 
        {
            dbconnection = DriverManager.getConnection(URL, USER, PASSWORD);
        
        } 
        catch (SQLException e) 
        {
            System.err.println("Database connection failed!! ");
        }
    }

    public boolean movieExists(String title) throws SQLException {
        String query = "SELECT COUNT(*) FROM movies WHERE title = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    
    public static void AddMovie(String title, byte[] posterData, String genre, String summaryText) throws SQLException 
    {
        if (dbconnection == null) {
            System.err.println("Database connection failed!!");
            return;
        }
    
        /* String summaryPath = "C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Uploads\\"
                             + title.replaceAll(" ", "_") + "_summary.txt"; */
    
        
        
        // Insert the movie into the database
        String query = "INSERT INTO movies (title, poster_image, genre, summary, duration) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbconnection.prepareStatement(query)) 
        {
            stmt.setString(1, title);
            stmt.setBytes(2, posterData);  // URL or file path of the poster
            stmt.setString(3, genre);   // Byte array of the poster image
            stmt.setString(4, summaryText);
            stmt.setInt(5, 120); // File path of the summary
            stmt.executeUpdate();  // Execute the insertion query
            System.out.println("Movie added successfully.");
        }    
    }
    

    public void FullUpdateMovie(int movieId, String newTitle, byte[] newPosterData, String newGenre, String newSummaryText) throws SQLException 
    {
        if (dbconnection == null) 
        {
            System.err.println("Database connection failed!!");
            return; 
        }
        
        String checkTicketsQuery = "SELECT COUNT(*) FROM sessions WHERE movie_id = ?";
        try (PreparedStatement checkStmt = dbconnection.prepareStatement(checkTicketsQuery)) 
        {
            checkStmt.setInt(1, movieId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) 
            {
                System.out.println("Error: Cannot update a movie with sold tickets.");
                return;
            }
        }

        String query = "SELECT summary FROM movies WHERE movie_id = ?";
        try (PreparedStatement selectStmt = dbconnection.prepareStatement(query))
        {
            selectStmt.setInt(1, movieId);
            ResultSet infoSet = selectStmt.executeQuery();

            if (infoSet.next()) 
            {
                
                    query = "UPDATE movies SET title = ?, poster_image = ?, genre = ?, summary = ?, duration = ? WHERE movie_id = ?";
                    try(PreparedStatement stmt = dbconnection.prepareStatement(query))
                    {
                        stmt.setString(1, newTitle);
                        stmt.setBytes(2, newPosterData);
                        stmt.setString(3, newGenre);
                        stmt.setString(4, newSummaryText);
                        stmt.setInt(5, 120);
                        stmt.setInt(6, movieId);
                        stmt.executeUpdate();
                        System.out.println("Movie updated successfully.");
                    } 
            }
        } 
        
    }

    public void deleteMovie(int movieId) throws SQLException {
        if (dbconnection == null) {
            System.err.println("Database connection failed!!");
            return;
        }

        String checkTicketsQuery = "SELECT COUNT(*) FROM sessions WHERE movie_id = ?";
        try (PreparedStatement checkStmt = dbconnection.prepareStatement(checkTicketsQuery)) 
        {
            checkStmt.setInt(1, movieId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) 
            {
                System.out.println("Error: Cannot delete a movie with sold tickets.");
                return;
            }
        }
    
        //if session exists, do not delete
        String query = "DELETE FROM movies WHERE movie_id = ?";
        try (PreparedStatement deleteStmt = dbconnection.prepareStatement(query)) {
            deleteStmt.setInt(1, movieId);
            int rowsAffected = deleteStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Movie deleted successfully.");
            } else {
                System.out.println("No movie found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        query = "SELECT summary FROM movies WHERE movie_id = ?";
        try (PreparedStatement selectStmt = dbconnection.prepareStatement(query)) {
            selectStmt.setInt(1, movieId);
            ResultSet resultSet = selectStmt.executeQuery();
    
            if (resultSet.next()) {
                String summaryPath = resultSet.getString("summary");
                File summaryFile = new File(summaryPath);
                if (summaryFile.exists() && !summaryFile.delete()) {
                    System.err.println("Failed to delete summary file: " + summaryPath);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    //Gott fix the errors related to Get All Movies and Get Movie by ID
    public List<Movie> GetAllMovies() 
    {
        List<Movie> movies = new ArrayList<>();

        if (dbconnection == null) 
        {
            System.err.println("Database connection failed!!");
            return movies;
        }

        String query = "SELECT movie_id, title, genre, summary, duration, poster_image FROM movies";

        try (PreparedStatement stmt = dbconnection.prepareStatement(query);
             ResultSet infoSet = stmt.executeQuery()) 
             {

            while (infoSet.next()) {
                int id = infoSet.getInt("movie_id");
                String title = infoSet.getString("title");
                String genre = infoSet.getString("genre");
                String summary = infoSet.getString("summary");
                String duration = infoSet.getString("duration");
                byte[] posterImage = infoSet.getBytes("poster_image");

                Movie movie = new Movie(id, title, posterImage, genre, summary, duration);
                movies.add(movie);
            }
        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        return movies;
    }
    

    /* public String ReadSummary(String summaryPath) 
    {
        StringBuilder summary = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(summaryPath))) 
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                summary.append(line).append("\n");
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return summary.toString();
    } */

    public ImageIcon readPoster(String posterFilePath) 
    {
        File posterFile = new File(posterFilePath);
        if (posterFile.exists()) 
        {
            return new ImageIcon(posterFile.getAbsolutePath());
        } 
        else 
        {
            System.out.println("Poster file not found.");
            return null;
        }
    }
    
    public static int getMovieIdFromTitle(String title) 
    {
        if (dbconnection == null) 
        {
            System.err.println("Database connection failed!!");
            return -1;
        }
    
        String query = "SELECT movie_id FROM movies WHERE title = ?";
        try (PreparedStatement stmt = dbconnection.prepareStatement(query)) 
        {
            stmt.setString(1, title);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("movie_id");
            } 
            else 
            {
                System.out.println("Movie not found.");
                return 0;
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            return 0;
        }
    }

    public String getMovieTitleFromId(int movieId) throws SQLException 
    {
        if (dbconnection == null) 
        {
            System.err.println("Database connection failed!!");
            return null;
        }

        String title = null;
        String query = "SELECT title FROM movies WHERE movie_id = ?"; 

        try (PreparedStatement statement = dbconnection.prepareStatement(query)) 
        {
            statement.setInt(1, movieId);
            try (ResultSet resultSet = statement.executeQuery()) 
            {
                if (resultSet.next()) 
                {
                    title = resultSet.getString("title");
                }
            }
        }
        return title;
    }

    // Add a new session
    //    public void AddSession(int movieId, String hallName, LocalDate sessionDate, Time startTime) throws SQLException 

    public void AddSession(int movieId, String hallName, LocalDate sessionDate, Time startTime) throws SQLException {
        String insertSession = "INSERT INTO Sessions (movie_id, hall_name, session_date, start_time, vacant_seats) VALUES (?, ?, ?, ?, ?)";
        String insertSeat = "INSERT INTO seats (session_id, hall_name,seat_label, is_occupied) VALUES (?, ?, ?, ?)";
        int hallCapacity = getHallCapacity(hallName);
        List<String> seatLabels = generateSeatLabels(hallName);


        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);
            try {
                // Check for Overlaps
                String checkOverlapQuery = "SELECT COUNT(*) FROM Sessions WHERE hall_name = ? AND session_date = ? AND (? BETWEEN start_time AND ADDTIME(start_time, '1:59:59') OR ADDTIME(?, '1:59:59') BETWEEN start_time AND ADDTIME(start_time, '1:59:59'))";
                try (PreparedStatement checkStmt = dbconnection.prepareStatement(checkOverlapQuery)) 
                {
                    checkStmt.setString(1, hallName);
                    checkStmt.setDate(2, java.sql.Date.valueOf(sessionDate));
                    checkStmt.setTime(3, startTime);
                    checkStmt.setTime(4, startTime);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) 
                    {
                        System.out.println("Error: Overlapping session detected.");
                        return;
                    }
                }

                // Insert session
                PreparedStatement sessionStmt = conn.prepareStatement(insertSession, PreparedStatement.RETURN_GENERATED_KEYS);
                sessionStmt.setInt(1, movieId);
                sessionStmt.setString(2, hallName);
                sessionStmt.setDate(3, java.sql.Date.valueOf(sessionDate));
                sessionStmt.setTime(4, startTime);
                sessionStmt.setInt(5, hallCapacity);
                sessionStmt.executeUpdate();
        
                // Get generated session ID
                ResultSet rs = sessionStmt.getGeneratedKeys();
                if (rs.next()) 
                {
                    int sessionId = rs.getInt(1);
                    System.out.println("Session added successfully. Generated session ID: " + sessionId);
        
                    PreparedStatement seatStmt = conn.prepareStatement(insertSeat);
                    for (String seatLabel : seatLabels) 
                    {
                        System.out.println("Inserting seat: " + seatLabel);
                        seatStmt.setInt(1, sessionId);
                        seatStmt.setString(2, hallName);
                        seatStmt.setString(3, seatLabel);
                        seatStmt.setBoolean(4, false);
                        seatStmt.executeUpdate();
                        System.out.println("Seat added successfully: " + seatLabel);
                    }
                } else {
                    throw new SQLException("Failed to retrieve session ID.");
                }
        
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace(); // Log error details
                throw e;
            }
        }
        
    }
    private int getHallCapacity(String hallName) 
    {
        switch (hallName) {
            case "Hall_A":
                return 16;
                case "Hall_B":
                    return 48;
                default:
                    return 0;
            }
            
    }
    private List<String> generateSeatLabels(String hallName) {
        if (hallName == null || hallName.isBlank()) {
            throw new IllegalArgumentException("Hall name cannot be null or empty");
        }
    
        List<String> labels = new ArrayList<>();
        if ("Hall_A".equalsIgnoreCase(hallName)) {
            for (char row = 'A'; row <= 'D'; row++) {
                for (int col = 1; col <= 4; col++) {
                    labels.add(String.format("%c%d", row, col));
                }
            }
        } else if ("Hall_B".equalsIgnoreCase(hallName)) {
            for (char row = 'A'; row <= 'H'; row++) {
                for (int col = 1; col <= 6; col++) {
                    labels.add(String.format("%c%d", row, col));
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid hall name: " + hallName);
        }
    
        return labels;
    }
    
    
        

    // Update an existing session
    public void UpdateSession(int sessionId, int movieId, String hallName, LocalDate sessionDate, Time startTime) throws SQLException 
    {
        String checkTicketsQuery = "SELECT COUNT(*) FROM Tickets WHERE session_id = ?";
        try (PreparedStatement checkStmt = dbconnection.prepareStatement(checkTicketsQuery)) 
        {
            checkStmt.setInt(1, sessionId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) 
            {
                System.out.println("Error: Cannot update a session with sold tickets.");
                return;
            }
        }

        String checkOverlapQuery = "SELECT COUNT(*) FROM Sessions WHERE hall_name = ? AND session_date = ? AND (? BETWEEN start_time AND ADDTIME(start_time, '1:59:59') OR ADDTIME(?, '1:59:59') BETWEEN start_time AND ADDTIME(start_time, '1:59:59'))";
                try (PreparedStatement checkStmt = dbconnection.prepareStatement(checkOverlapQuery)) 
                {
                    checkStmt.setString(1, hallName);
                    checkStmt.setDate(2, java.sql.Date.valueOf(sessionDate));
                    checkStmt.setTime(3, startTime);
                    checkStmt.setTime(4, startTime);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) 
                    {
                        System.out.println("Error: Overlapping session detected.");
                        return;
                    }
                }

        String updateQuery = "UPDATE Sessions SET movie_id = ?, hall_name = ?, session_date = ?, start_time = ? WHERE session_id = ?";
        try (PreparedStatement stmt = dbconnection.prepareStatement(updateQuery)) 
        {
            stmt.setInt(1, movieId);
            stmt.setString(2, hallName);
            stmt.setDate(3, java.sql.Date.valueOf(sessionDate));
            stmt.setTime(4, startTime);
            stmt.setInt(5, sessionId);
            System.out.println("Session updated successfully");
            stmt.executeUpdate();
        }
    }

    // Delete a session
    public void DeleteSession(int sessionId) throws SQLException 
    {
        String checkTicketsQuery = "SELECT COUNT(*) FROM Tickets WHERE session_id = ?";
        try (PreparedStatement checkStmt = dbconnection.prepareStatement(checkTicketsQuery)) 
        {
            checkStmt.setInt(1, sessionId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) 
            {
                System.out.println("Error: Cannot delete a session with sold tickets.");
                return;
            }
        }

        String deleteQuery = "DELETE FROM Sessions WHERE session_id = ?";
        try (PreparedStatement stmt = dbconnection.prepareStatement(deleteQuery)) 
        {
            stmt.setInt(1, sessionId);
            stmt.executeUpdate();
            System.out.println("session deleted successfully");
        }
    }

    // Get all sessions
    public List<Schedule> GetAllSessions() throws SQLException 
    {
        List<Schedule> schedules = new ArrayList<>();

        if (dbconnection == null) 
        {
            System.err.println("Database connection failed!!");
            return schedules;
        }

        String query = "SELECT * FROM Sessions";

        try (PreparedStatement stmt = dbconnection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery())
        {
            while (rs.next()) 
            {
                int sessionid = rs.getInt("session_id");
                int movieId = rs.getInt("movie_id");
                String hallName = rs.getString("hall_name");
                LocalDate sessionDate = rs.getDate("session_date").toLocalDate();
                Time starTime = rs.getTime("start_time");
                int vacantSeats = rs.getInt("vacant_seats");

                Schedule schedule = new Schedule(sessionid, movieId, hallName, sessionDate, starTime, vacantSeats);
                schedules.add(schedule);
            }
        }
        catch (SQLException e) 
        {
        System.out.println("Error occurred: " + e.getMessage());
        e.printStackTrace();
        }
        return schedules;
    }
}