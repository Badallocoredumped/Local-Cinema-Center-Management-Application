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
    
    /**
     * Establishes and returns a connection to the database.
     * 
     * This method uses the provided URL, username, and password to connect to the database. 
     * If the connection cannot be established, it throws an exception.
     * 
     * @return A {@link Connection} object representing the connection to the database.
     * @throws Exception If the database connection cannot be established.
     */
    public static Connection getConnection() throws Exception
    {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    /**
     * Constructor for the {@link AdminDBH} class.
     * 
     * This constructor attempts to establish a connection to the database using the provided URL, username, and password.
     * If the connection fails, an error message is printed to the console.
     */
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

    /**
     * Checks if a movie with the given title exists in the database.
     *
     * @param title The title of the movie to check.
     * @return {@code true} if the movie exists in the database, {@code false} otherwise.
     * @throws SQLException If there is an error executing the SQL query or accessing the database.
     */
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
    
    /**
     * Adds a new movie to the database with the specified details.
     * 
     * This method inserts a movie's title, poster image, genre, summary text, and a fixed duration into the "movies" table.
     * It checks if the database connection is valid before attempting to add the movie.
     *
     * @param title The title of the movie to be added.
     * @param posterData The byte array representing the movie's poster image.
     * @param genre The genre of the movie.
     * @param summaryText The summary of the movie.
     * @throws SQLException If there is an error executing the SQL query or accessing the database.
     */
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
    
    /**
     * Updates the details of an existing movie in the database.
     * 
     * This method checks if there are any sold tickets for the movie before proceeding with the update. 
     * It updates the movie's title, poster image, genre, summary, and a fixed duration for the specified movie ID.
     * If there are sold tickets associated with the movie, the update will not be allowed.
     *
     * @param movieId The ID of the movie to be updated.
     * @param newTitle The new title for the movie.
     * @param newPosterData The byte array representing the new poster image.
     * @param newGenre The new genre for the movie.
     * @param newSummaryText The new summary text for the movie.
     * @throws SQLException If there is an error executing the SQL queries or accessing the database.
     */
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

    /**
     * Deletes a movie from the database if no tickets have been sold for it.
     * 
     * This method first checks if there are any sold tickets associated with the movie.
     * If there are sold tickets, the movie cannot be deleted. If no tickets are sold,
     * the movie and its associated summary file are deleted from the database.
     *
     * @param movieId The ID of the movie to be deleted.
     * @throws SQLException If there is an error executing the SQL queries or accessing the database.
     */
    public void deleteMovie(int movieId) throws SQLException 
    {
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
    
    /**
     * Retrieves all movies from the database.
     * 
     * This method fetches the details of all movies from the "movies" table, including the movie ID, title, genre,
     * summary, duration, and poster image. Each movie is then added to a list of {@code Movie} objects.
     * If the database connection fails, an empty list is returned.
     *
     * @return A list of {@code Movie} objects representing all movies in the database.
     */
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

    /*public ImageIcon readPoster(String posterFilePath) 
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
    }*/
    
    /**
     * Retrieves the movie ID associated with the given movie title.
     * 
     * This method queries the database to find the movie ID corresponding to the provided title.
     * If the movie is found, its ID is returned. If the movie is not found, the method returns 0.
     * If the database connection fails, the method returns -1.
     *
     * @param title The title of the movie whose ID is to be retrieved.
     * @return The movie ID if found, 0 if the movie is not found, or -1 if the database connection fails.
     */
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

    /**
     * Retrieves the title of a movie based on its ID.
     * 
     * This method queries the database to find the title of the movie with the specified movie ID.
     * If the movie is found, its title is returned. If the movie is not found, the method returns {@code null}.
     * 
     * @param movieId The ID of the movie whose title is to be retrieved.
     * @return The title of the movie if found, or {@code null} if the movie is not found.
     * @throws SQLException If there is an error executing the SQL query or accessing the database.
     */
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

    /**
     * Adds a new session for a movie in the specified hall at a specific time and date.
     * 
     * This method first checks for any overlapping sessions in the specified hall and time slot. 
     * If no overlap is found, it inserts a new session and populates the seat information for the session.
     * The method also ensures that all database changes are committed as part of a single transaction.
     *
     * @param movieId The ID of the movie for which the session is being added.
     * @param hallName The name of the hall where the session will take place.
     * @param sessionDate The date of the session.
     * @param startTime The start time of the session.
     * @throws SQLException If there is an error executing the SQL queries or accessing the database.
     */
    public void AddSession(int movieId, String hallName, LocalDate sessionDate, Time startTime) throws SQLException 
    {
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

    /**
     * Retrieves the seating capacity of a specified hall.
     * 
     * This method returns the number of seats available in the given hall. If the hall name is not recognized,
     * it returns 0 to indicate an unknown hall.
     *
     * @param hallName The name of the hall for which the capacity is being requested.
     * @return The seating capacity of the specified hall. Returns 0 if the hall name is not recognized.
     */
    private int getHallCapacity(String hallName) 
    {
        switch (hallName) 
        {
            case "Hall_A":
                return 16;
                case "Hall_B":
                    return 48;
                default:
                    return 0;
        }     
    }

    /**
     * Generates a list of seat labels for the specified hall.
     * 
     * This method generates seat labels based on the hall's configuration. It supports "Hall_A" with a 4x4 seating arrangement
     * and "Hall_B" with a 6x8 seating arrangement. If the hall name is invalid or empty, an {@code IllegalArgumentException} is thrown.
     *
     * @param hallName The name of the hall for which seat labels are being generated.
     * @return A list of seat labels for the specified hall.
     * @throws IllegalArgumentException If the hall name is null, empty, or invalid.
     */
    private List<String> generateSeatLabels(String hallName) 
    {
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
    
    
        

    /**
     * Updates an existing session in the database.
     * 
     * This method first checks if any tickets have been sold for the session; if tickets exist, the session cannot be updated. 
     * It then verifies if the new session time overlaps with existing sessions in the specified hall. 
     * If no conflicts are found, the session details (movie ID, hall name, session date, and start time) are updated.
     *
     * @param sessionId The ID of the session to be updated.
     * @param movieId The new movie ID for the session.
     * @param hallName The new hall name for the session.
     * @param sessionDate The new date for the session.
     * @param startTime The new start time for the session.
     * @throws SQLException If there is an error executing the SQL queries or accessing the database.
     */
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

    public void DeleteSession(int sessionId) throws Exception {
        Connection connection = DataBaseHandler.getConnection();
        
        try {
            // Check if there are tickets associated with the session
            String checkTicketsQuery = "SELECT COUNT(*) FROM tickets WHERE session_id = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkTicketsQuery)) {
                checkStmt.setInt(1, sessionId);
                ResultSet resultSet = checkStmt.executeQuery();
                resultSet.next();
                int ticketCount = resultSet.getInt(1);
                
                if (ticketCount > 0) {
                    throw new SQLException("Cannot delete session because tickets have already been purchased for it.");
                }
            }
            
            // Start a transaction to ensure both delete operations succeed
            connection.setAutoCommit(false);
            
            // First, delete all seats associated with the session
            String deleteSeatsQuery = "DELETE FROM seats WHERE session_id = ?";
            try (PreparedStatement seatStmt = connection.prepareStatement(deleteSeatsQuery)) {
                seatStmt.setInt(1, sessionId);
                seatStmt.executeUpdate();
            }
            
            // Then, delete the session itself
            String deleteSessionQuery = "DELETE FROM sessions WHERE session_id = ?";
            try (PreparedStatement sessionStmt = connection.prepareStatement(deleteSessionQuery)) {
                sessionStmt.setInt(1, sessionId);
                sessionStmt.executeUpdate();
            }
            
            // Commit the transaction
            connection.commit();
        } catch (SQLException e) {
            // Rollback in case of an error
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    

                                  
            
    

    /**
     * Retrieves all sessions from the database.
     * 
     * This method queries the database for all sessions and constructs a list of {@link Schedule} objects containing session details.
     * If the database connection fails, an empty list is returned.
     * 
     * @return A list of {@link Schedule} objects representing all sessions in the database.
     * @throws SQLException If there is an error executing the SQL query or accessing the database.
     */
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