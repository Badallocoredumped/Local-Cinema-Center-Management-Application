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
    
    public static void AddMovie(String title, byte[] posterData, String genre, String summaryText, String duration) 
    {
        if (dbconnection == null) {
            System.err.println("Database connection failed!!");
            return;
        }
    
        String summaryPath = "C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Uploads\\"
                             + title.replaceAll(" ", "_") + "_summary.txt";
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryPath))) {
            // Write the summary content to the summary file
            writer.write(summaryText);
            writer.newLine();
    
            // Insert the movie into the database
            String query = "INSERT INTO movies (title, poster_image, genre, summary, duration) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = dbconnection.prepareStatement(query)) {
                stmt.setString(1, title);
                stmt.setBytes(2, posterData);  // URL or file path of the poster
                stmt.setString(3, genre);   // Byte array of the poster image
                stmt.setString(4, summaryPath);
                stmt.setString(5, duration); // File path of the summary
                stmt.executeUpdate();  // Execute the insertion query
                System.out.println("Movie added successfully.");
            }
        } catch (IOException e) {
            System.err.println("Failed to write summary file: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to add movie to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    public void FullUpdateMovie(int movieId, String newTitle, byte[] newPosterData, String newGenre, String newSummaryText, String newDuration) 
    {
        if (dbconnection == null) 
        {
            System.err.println("Database connection failed!!");
            return; 
        }
        

        String query = "SELECT summary FROM movies WHERE movie_id = ?";
        try (PreparedStatement selectStmt = dbconnection.prepareStatement(query))
        {
            selectStmt.setInt(1, movieId);
            ResultSet infoSet = selectStmt.executeQuery();

            if (infoSet.next()) 
            {
                String newSummaryPath = "C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Uploads\\" + newTitle.replaceAll(" ", "_") + "_summary.txt";
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(newSummaryPath))) 
                {

                    writer.write(newSummaryText);
                    writer.newLine();  
                    query = "UPDATE movies SET title = ?, poster_image = ?, genre = ?, summary = ?, duration = ? WHERE movie_id = ?";
                    try(PreparedStatement stmt = dbconnection.prepareStatement(query))
                    {
                        stmt.setString(1, newTitle);
                        stmt.setBytes(2, newPosterData);
                        stmt.setString(3, newGenre);
                        stmt.setString(4, newSummaryPath);
                        stmt.setString(5, newDuration);
                        stmt.setInt(6, movieId);
                        stmt.executeUpdate();
                        System.out.println("Movie updated successfully.");
                    } 
                }
            }
            else 
            {
                System.out.println("Movie not found.");
            }
        } 
        catch (IOException | SQLException e) 
        {
            e.printStackTrace();
        }
    }

    public void deleteMovie(int movieId) {
        if (dbconnection == null) {
            System.err.println("Database connection failed!!");
            return;
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
                String summaryPath = infoSet.getString("summary");
                String duration = infoSet.getString("duration");
                byte[] posterImage = infoSet.getBytes("poster_image");
                String summary = ReadSummary(summaryPath);

                Movie movie = new Movie(id, title, posterImage, genre, summary, duration);
                movies.add(movie);
            }
        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        return movies;
    }
    

    public String ReadSummary(String summaryPath) 
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
    }

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
    public void AddSession(int movieId, String hallName, LocalDate sessionDate, Time startTime) throws SQLException 
    {
        int vacantSeats = hallName.equalsIgnoreCase("Hall_A") ? 16 : 48;

        String checkOverlapQuery = "SELECT COUNT(*) FROM Sessions WHERE hall_name = ? AND session_date = ? AND (? BETWEEN start_time AND ADDTIME(start_time, '2:00:00') OR ADDTIME(?, '2:00:00') BETWEEN start_time AND ADDTIME(start_time, '2:00:00'))";
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

        String insertQuery = "INSERT INTO Sessions (movie_id, hall_name, session_date, start_time, vacant_seats) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbconnection.prepareStatement(insertQuery)) 
        {
            stmt.setInt(1, movieId);
            stmt.setString(2, hallName);
            stmt.setDate(3, java.sql.Date.valueOf(sessionDate));
            stmt.setTime(4, startTime);
            stmt.setInt(5, vacantSeats);
            stmt.executeUpdate();
        }
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

        String checkOverlapQuery = "SELECT COUNT(*) FROM Sessions WHERE hall_name = ? AND session_date = ? AND (? BETWEEN start_time AND ADDTIME(start_time, '2:00:00') OR ADDTIME(?, '2:00:00') BETWEEN start_time AND ADDTIME(start_time, '2:00:00')) AND session_id <> ?";
        try (PreparedStatement checkStmt = dbconnection.prepareStatement(checkOverlapQuery)) 
        {
            checkStmt.setString(1, hallName);
            checkStmt.setDate(2, java.sql.Date.valueOf(sessionDate));
            checkStmt.setTime(3, startTime);
            checkStmt.setTime(4, startTime);
            checkStmt.setInt(5, sessionId);
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