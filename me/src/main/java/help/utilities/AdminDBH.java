package help.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import help.classes.Movie;
import javafx.beans.property.StringProperty;

public class AdminDBH 
{
    private static final String URL = "jdbc:mysql://localhost:3306/Group16";
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
    
    public static void AddMovie(String title, String poster, byte[] posterData, String genre, String summaryText, String duration) 
    {
        if (dbconnection == null) {
            System.err.println("Database connection failed!!");
            return;
        }
    
        String summaryPath = "C:/Users/ahmed/OneDrive - Kadir Has University/Belgeler/GitHub/Local-Cinema-Center-Management-Application/Movie/Summaries/"
                             + title.replaceAll(" ", "_") + "_summary.txt";
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryPath))) {
            // Write the summary content to the summary file
            writer.write(summaryText);
            writer.newLine();
    
            // Insert the movie into the database
            String query = "INSERT INTO movies (title, poster_url, poster_image, genre, summary, duration) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = dbconnection.prepareStatement(query)) {
                stmt.setString(1, title);
                stmt.setString(2, poster);  // URL or file path of the poster
                stmt.setBytes(3, posterData);   // Byte array of the poster image
                stmt.setString(4, genre);
                stmt.setString(5, summaryPath); // File path of the summary
                stmt.setString(6, duration);
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
    

    public void FullUpdateMovie(int movieId, String newTitle, String newPoster, byte[] newPosterData, String newGenre, String newSummaryText, String newDuration) 
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
                String newSummaryPath = "C:/Users/ahmed/OneDrive - Kadir Has University/Belgeler/GitHub/Local-Cinema-Center-Management-Application/Movie/Summaries/" + newTitle.replaceAll(" ", "_") + "_summary.txt";
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(newSummaryPath))) 
                {

                    writer.write(newSummaryText);
                    writer.newLine();  
                    query = "UPDATE movies SET title = ?,  poster_url = ?, poster_image = ?, genre = ?, summary = ?, duration = ? WHERE movie_id = ?";
                    try(PreparedStatement stmt = dbconnection.prepareStatement(query))
                    {
                        stmt.setString(1, newTitle);
                        stmt.setString(2, newPoster);
                        stmt.setBytes(3, newPosterData);
                        stmt.setString(4, newGenre);
                        stmt.setString(5, newSummaryPath);
                        stmt.setString(6, newDuration);
                        stmt.setInt(7, movieId);
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
    
        String query = "SELECT summary FROM movies WHERE movie_id = ?";
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
    
        query = "DELETE FROM movies WHERE movie_id = ?";
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
    }
    
    
    //Gott fix the errors related to Get All Movies and Get Movie by ID
    public List<Movie> GetAllMovies() {
        List<Movie> movies = new ArrayList<>();

        if (dbconnection == null) {
            System.err.println("Database connection failed!!");
            return movies;
        }

        String query = "SELECT movie_id, title, poster_url, poster_image, genre, summary, duration FROM movies";

        try (PreparedStatement stmt = dbconnection.prepareStatement(query);
             ResultSet infoSet = stmt.executeQuery()) 
             {

            while (infoSet.next()) {
                int id = infoSet.getInt("movie_id");
                String title = infoSet.getString("title");
                String posterFile = infoSet.getString("poster_url");
                String genre = infoSet.getString("genre");
                String summaryPath = infoSet.getString("summary");
                String duration = infoSet.getString("duration");
                byte[] posterImage = infoSet.getBytes("poster_image");
                String summary = ReadSummary(summaryPath);

                Movie movie = new Movie(id, title, posterFile, genre, summary, duration, posterImage);
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

    public void UpdateMovieTitle(int movieId, String newTitle) 
    {
        if (dbconnection == null) 
        {
            System.err.println("Database connection failed!!");
            return;
        }
    
        String query = "UPDATE movies SET title = ? WHERE movie_id = ?";
        try (PreparedStatement stmt = dbconnection.prepareStatement(query)) 
        {
            stmt.setString(1, newTitle);
            stmt.setInt(2, movieId);
            stmt.executeUpdate();
            System.out.println("Title updated successfully.");
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    
    public void UpdateMoviePoster(int movieId, String newPoster) 
    {
        if (dbconnection == null) 
        {
            System.err.println("Database connection failed!!");
            return;
        }
    
        String query = "UPDATE movies SET  poster_url = ? WHERE movie_id = ?";
        try (PreparedStatement stmt = dbconnection.prepareStatement(query)) 
        {
            stmt.setString(1, newPoster);
            stmt.setInt(2, movieId);
            stmt.executeUpdate();
            System.out.println("Poster updated successfully.");
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    
    public void UpdateMovieGenre(int movieId, String newGenre) 
    {
        if (dbconnection == null) 
        {
            System.err.println("Database connection failed!!");
            return;
        }
    
        String query = "UPDATE movies SET genre = ? WHERE movie_id = ?";
        try (PreparedStatement stmt = dbconnection.prepareStatement(query)) 
        {
            stmt.setString(1, newGenre);
            stmt.setInt(2, movieId);
            stmt.executeUpdate();
            System.out.println("Genre updated successfully.");
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    
    public void UpdateMovieSummary(int movieId, String newTitle, String newSummaryText) 
    {
        if (dbconnection == null) 
        {
            System.err.println("Database connection failed!!");
            return;
        }
    
        String newSummaryPath = "C:/Users/ahmed/OneDrive - Kadir Has University/Belgeler/GitHub/Local-Cinema-Center-Management-Application/Movie/Summaries/"
                + newTitle.replaceAll(" ", "_") + "_summary.txt";
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(newSummaryPath))) 
        {
            writer.write(newSummaryText);
            writer.newLine();
    
            String query = "UPDATE movies SET summary = ? WHERE movie_id = ?";
            try (PreparedStatement stmt = dbconnection.prepareStatement(query)) {
                stmt.setString(1, newSummaryPath);
                stmt.setInt(2, movieId);
                stmt.executeUpdate();
                System.out.println("Summary updated successfully.");
            }
        } 
        catch (IOException | SQLException e) 
        {
            e.printStackTrace();
        }
    }
    
    public int getMovieIdFromTitle(String title) 
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

    public void AddSession(int movieId, int hallId, String date, String startTime, String endTime) {
        if (dbconnection == null) 
        {
            System.err.println("Database connection failed!!");
            return;
        }
    
        String overlapQuery = "SELECT COUNT(*) FROM Schedules WHERE hall_id = ? AND date = ? AND " +
                              "((start_time < ? AND end_time > ?) OR " +
                              "(start_time < ? AND end_time > ?) OR " +
                              "(start_time >= ? AND end_time <= ?))";
        try (PreparedStatement stmt = dbconnection.prepareStatement(overlapQuery)) {
            stmt.setInt(1, hallId);
            stmt.setString(2, date);
            stmt.setString(3, endTime);
            stmt.setString(4, startTime);
            stmt.setString(5, startTime);
            stmt.setString(6, endTime);
            stmt.setString(7, startTime);
            stmt.setString(8, endTime);
    
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                System.out.println("Session overlaps with an existing session.");
            }
    
            // Retrieve movie title
            String movieTitleQuery = "SELECT title FROM Movies WHERE movie_id = ?";
            String movieTitle = null;
    
            try (PreparedStatement titleStmt = dbconnection.prepareStatement(movieTitleQuery)) {
                titleStmt.setInt(1, movieId);
                resultSet = titleStmt.executeQuery();
                if (resultSet.next()) {
                    movieTitle = resultSet.getString("title");
                } 
                else 
                {
                    System.out.println("Movie ID not found.");
                }
            } catch (SQLException e) 
            {
                e.printStackTrace();
            }
    
            // Insert schedule and retrieve the generated schedule_id
            String insertQuery = "INSERT INTO Schedules (movie_id, hall_id, date, start_time, end_time) " +
                                 "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = dbconnection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) 
            {
                insertStmt.setInt(1, movieId);
                insertStmt.setInt(2, hallId);
                insertStmt.setString(3, date);
                insertStmt.setString(4, startTime);
                insertStmt.setString(5, endTime);
                insertStmt.executeUpdate();
    
                ResultSet Keys = insertStmt.getGeneratedKeys();
                if (Keys.next()) 
                {
                    int scheduleId = Keys.getInt(1);
    
                    String scheduleName = movieTitle.replaceAll(" ", "") + "_" + scheduleId;
    
                    String updateQuery = "UPDATE Schedules SET schedule_name = ? WHERE schedule_id = ?";
                    try (PreparedStatement updateStmt = dbconnection.prepareStatement(updateQuery)) 
                    {
                        updateStmt.setString(1, scheduleName);
                        updateStmt.setInt(2, scheduleId);
                        updateStmt.executeUpdate();
                    }
    
                    System.out.println("Session added successfully with name: " + scheduleName);
                } 
                else 
                {
                    System.err.println("Failed to retrieve the generated schedule ID.");
                }
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    

    public void DeleteSession(int scheduleName) 
    {
        if (dbconnection == null) 
        {
            System.err.println("Database connection failed!!");
            return;
        }

        String checkQuery = "SELECT sold_ticket FROM Schedules WHERE schedule_name = ?";
        try (PreparedStatement checkStmt = dbconnection.prepareStatement(checkQuery)) 
        {
            checkStmt.setInt(1, scheduleName);
            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next() && resultSet.getBoolean("sold_ticket")) 
            {
                System.out.println("Cannot delete session: tickets have been sold.");
            }

            String deleteQuery = "DELETE FROM Schedules WHERE schedule_name = ?";
            try (PreparedStatement deleteStmt = dbconnection.prepareStatement(deleteQuery)) 
            {
                deleteStmt.setInt(1, scheduleName);
                deleteStmt.executeUpdate();
                System.out.println("Session deleted successfully.");
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    public void UpdateSession(int scheduleId, String newDate, String newStartTime, String newEndTime) 
    {
        if (dbconnection == null) 
        {
            System.err.println("Database connection failed!!");
            return;
        }

        String checkQuery = "SELECT sold_ticket FROM Schedules WHERE schedule_id = ?";
        try (PreparedStatement checkStmt = dbconnection.prepareStatement(checkQuery)) 
        {
            checkStmt.setInt(1, scheduleId);
            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next() && resultSet.getBoolean("sold_ticket")) 
            {
                System.out.println("Cannot update session: tickets have been sold.");
                return;
            }

            String overlapQuery = "SELECT COUNT(*) FROM Schedules WHERE hall_id = (SELECT hall_id FROM Schedules WHERE schedule_id = ?) " +
                                  "AND date = ? AND schedule_id != ? AND " +
                                  "((start_time < ? AND end_time > ?) OR " +
                                  "(start_time < ? AND end_time > ?) OR " +
                                  "(start_time >= ? AND end_time <= ?))";
            try (PreparedStatement stmt = dbconnection.prepareStatement(overlapQuery)) 
            {
                stmt.setInt(1, scheduleId);
                stmt.setString(2, newDate);
                stmt.setInt(3, scheduleId);
                stmt.setString(4, newEndTime);
                stmt.setString(5, newStartTime);
                stmt.setString(6, newStartTime);
                stmt.setString(7, newEndTime);
                stmt.setString(8, newStartTime);
                stmt.setString(9, newEndTime);

                ResultSet overlapResult = stmt.executeQuery();
                if (overlapResult.next() && overlapResult.getInt(1) > 0) {
                    System.out.println("Session overlaps with an existing session.");
                    return;
                }

                String updateQuery = "UPDATE Schedules SET date = ?, start_time = ?, end_time = ? WHERE schedule_id = ?";
                try (PreparedStatement updateStmt = dbconnection.prepareStatement(updateQuery)) 
                {
                    updateStmt.setString(1, newDate);
                    updateStmt.setString(2, newStartTime);
                    updateStmt.setString(3, newEndTime);
                    updateStmt.setInt(4, scheduleId);
                    updateStmt.executeUpdate();
                    System.out.println("Session updated successfully.");
                }
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    public void getMonthlySchedule(int month) 
    {
        if (dbconnection == null) {
            System.err.println("Database connection failed!");
            return;
        }
    
        String query = "SELECT Schedules.schedule_name, Movies.title AS movie_title, Schedules.hall_id, " +
                       "Schedules.date, Schedules.start_time, Schedules.end_time " +
                       "FROM Schedules " +
                       "INNER JOIN Movies ON Schedules.movie_id = Movies.movie_id " +
                       "WHERE MONTH(Schedules.date) = ? " +
                       "ORDER BY Schedules.date, Schedules.start_time";
    
        try (PreparedStatement stmt = dbconnection.prepareStatement(query)) {
            stmt.setInt(1, month);
    
            ResultSet resultSet = stmt.executeQuery();
    
            System.out.println("Monthly Schedule:");
            System.out.println("------------------------------------------------------------");
            while (resultSet.next()) {
                String scheduleName = resultSet.getString("schedule_name");
                String movieTitle = resultSet.getString("movie_title");
                int hallId = resultSet.getInt("hall_id");
                String date = resultSet.getString("date");
                String startTime = resultSet.getString("start_time");
                String endTime = resultSet.getString("end_time");
    
                System.out.printf("Schedule Name: %s | Movie: %s | Hall: %d | Date: %s | Time: %s - %s%n",
                                  scheduleName, movieTitle, hallId, date, startTime, endTime);
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    public void TicketRefund(int ticketId) {
        if (dbconnection == null) {
            System.err.println("Database connection failed!");
            return;
        }

        try 
        {
            String ticketQuery = "UPDATE Tickets SET refunded = TRUE WHERE ticket_id = ? AND refunded = FALSE";
            String updateSeatsQuery = "UPDATE Schedules SET vacant_seats = vacant_seats + 1 " +
                                      "WHERE schedule_id = (SELECT schedule_id FROM Tickets WHERE ticket_id = ?)";

            try (PreparedStatement ticketStmt = dbconnection.prepareStatement(ticketQuery);
                 PreparedStatement seatStmt = dbconnection.prepareStatement(updateSeatsQuery)) {

                ticketStmt.setInt(1, ticketId);
                seatStmt.setInt(1, ticketId);

                int ticketUpdated = ticketStmt.executeUpdate();

                if (ticketUpdated > 0) 
                {
                    seatStmt.executeUpdate();
                    System.out.println("Ticket refunded successfully.");
                } 
                else 
                {
                    System.out.println("No ticket found or already refunded.");
                }
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    public void ProductRefund(int saleId, int refundQuantity) 
    {
        if (dbconnection == null) 
        {
            System.err.println("Database connection failed!");
            return;
        }

        try 
        {
            String validationQuery = "SELECT quantity, refunded_quantity FROM ProductSales WHERE sale_id = ? AND is_refunded = FALSE";
            int purchasedQuantity = 0;
            int alreadyRefunded = 0;

            try (PreparedStatement validationStmt = dbconnection.prepareStatement(validationQuery)) {
                validationStmt.setInt(1, saleId);
                ResultSet resultSet = validationStmt.executeQuery();

                if (resultSet.next()) 
                {
                    purchasedQuantity = resultSet.getInt("quantity");
                    alreadyRefunded = resultSet.getInt("refunded_quantity");
                } 
                else 
                {
                    System.out.println("Sale ID not found or already refunded.");
                    return;
                }

                if (refundQuantity > (purchasedQuantity - alreadyRefunded)) 
                {
                    System.out.println("Refund quantity exceeds available quantity for refund.");
                    return;
                }
            }

            String updateSalesQuery = "UPDATE ProductSales SET refunded_quantity = refunded_quantity + ? WHERE sale_id = ?";
            String updateInventoryQuery = "UPDATE Products SET inventory = inventory + ? " +
                                           "WHERE product_id = (SELECT product_id FROM ProductSales WHERE sale_id = ?)";

            try (PreparedStatement salesStmt = dbconnection.prepareStatement(updateSalesQuery);
                 PreparedStatement inventoryStmt = dbconnection.prepareStatement(updateInventoryQuery)) {

                salesStmt.setInt(1, refundQuantity);
                salesStmt.setInt(2, saleId);
                inventoryStmt.setInt(1, refundQuantity);
                inventoryStmt.setInt(2, saleId);

                salesStmt.executeUpdate();
                inventoryStmt.executeUpdate();
                System.out.println("Product refund processed successfully.");
            }
        } 
        catch (SQLException e) 
        {
            System.err.println("Product refund process failed. Transaction rolled back.");
            e.printStackTrace();
        }        
    } 
}
