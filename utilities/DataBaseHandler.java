package utilities;

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

public class DataBaseHandler
{
    private static final String URL = "jdbc:mysql://localhost:3306/Group16";
    private static final String USER = "myuser";
    private static final String PASSWORD = "1234";
    private Connection dbconnection;


    public static Connection getConnection() throws Exception
    {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public DataBaseHandler()
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

    public void AddMovie(String title, String poster, String genre, String summaryText) 
    {
        if (dbconnection == null) 
        {
            System.err.println("Database connection failed!!");
            return; 
        }
        
        String summaryPath = "C:/Users/ahmed/OneDrive - Kadir Has University/Belgeler/GitHub/Local-Cinema-Center-Management-Application/Movie/Summaries/" + title.replaceAll(" ", "_") + "_summary.txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryPath))) 
        {
            writer.write(summaryText);
            writer.newLine();  

            String query = "INSERT INTO movies (title, poster, genre, summary) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = dbconnection.prepareStatement(query))
            {
                statement.setString(1, title);
                statement.setString(2, poster);
                statement.setString(3, genre);
                statement.setString(4, summaryPath);
                statement.executeUpdate();
                System.out.println("Movie added successfully.");
            }
        } 
        catch (IOException | SQLException e) 
        {
            e.printStackTrace();
        }
    }

    public void FullUpdateMovie(int movieId, String newTitle, String newPoster, String newGenre, String newSummaryText) 
    {
        if (dbconnection == null) 
        {
            System.err.println("Database connection failed!!");
            return; 
        }
        

        String query = "SELECT summary FROM movies WHERE movie_id = ?";
        try (PreparedStatement selectStatement = dbconnection.prepareStatement(query))
        {
            selectStatement.setInt(1, movieId);
            ResultSet infoSet = selectStatement.executeQuery();

            if (infoSet.next()) 
            {
                String newSummaryPath = "C:/Users/ahmed/OneDrive - Kadir Has University/Belgeler/GitHub/Local-Cinema-Center-Management-Application/Movie/Summaries/" + newTitle.replaceAll(" ", "_") + "_summary.txt";
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(newSummaryPath))) 
                {

                    writer.write(newSummaryText);
                    writer.newLine();  
                    query = "UPDATE movies SET title = ?, poster = ?, genre = ?, summary = ? WHERE movie_id = ?";
                    try(PreparedStatement statement = dbconnection.prepareStatement(query))
                    {
                        statement.setString(1, newTitle);
                        statement.setString(2, newPoster);
                        statement.setString(3, newGenre);
                        statement.setString(4, newSummaryPath);
                        statement.setInt(5, movieId);
                        statement.executeUpdate();

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
    
    //Gott fix the errors related to Get All Movies and Get Movie by ID
    public void GetAllMovies() 
    {
        if (dbconnection == null) 
        {
            System.err.println("Database connection failed!!");
            return;
        }

        String countQuery = "SELECT COUNT(*) AS total FROM movies";
        try (PreparedStatement countStatement = dbconnection.prepareStatement(countQuery)) 
        {
            ResultSet countResult = countStatement.executeQuery();
            String query = "SELECT movie_id, title, poster, genre, summary FROM movies";

            try(PreparedStatement statement = dbconnection.prepareStatement(query))
            {
                ResultSet infoSet = statement.executeQuery(query);
        
                while (infoSet.next()) 
                {
                    int id = infoSet.getInt("movie_id");
                    String title = infoSet.getString("title");
                    String posterFile = infoSet.getString("poster");
                    String genre = infoSet.getString("genre");
                    String summaryFile = infoSet.getString("summary");
                    String summary = ReadSummary(summaryFile);
                    ImageIcon poster = new ImageIcon(posterFile);

                    System.out.println("ID: " + id);
                    System.out.println("Title: " + title);
                    System.out.println("Poster: " + poster);
                    System.out.println("Genre: " + genre);
                    System.out.println("Summary: " + summary);
                    System.out.println("----------");
                }
            } 

            int totalMovies = 0;
            if (countResult.next()) 
            {
                totalMovies = countResult.getInt("total");
            }
            System.out.println("\nTotal Movies: " + totalMovies);
        } 
        catch (SQLException e) 
        { 
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace(); 
        }
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

    /*Add the Checjduplicate method here
     * Don't forget individual updates
     * Cancelation and refund operation
     * Session planning (Can not cancel session after ticket has been sold)
     * individual update options
     * add interaction with user()
    */

    public static String authenticate(String username, String password)
    {
        String role = null;
        try (Connection conn = getConnection())
        {
            String query = "SELECT role FROM Users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                role = rs.getString("role");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return role;
    }

    public static List<String> searchByGenre(String genre)
    {
        List<String> movies = new ArrayList<>();
        try (Connection conn = getConnection())
        {
            String query = "SELECT title FROM Movies WHERE genre LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + genre + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                movies.add(rs.getString("title"));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return movies;
    }

    public static List<String> searchByPartialName(String partialName)
    {
        List<String> movies = new ArrayList<>();
        try (Connection conn = getConnection())
        {
            String query = "SELECT title FROM Movies WHERE title LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + partialName + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                movies.add(rs.getString("title"));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return movies;
    }

    public static List<String> searchByFullName(String fullName)
    {
        List<String> movies = new ArrayList<>();
        try (Connection conn = getConnection())
        {
            String query = "SELECT title FROM Movies WHERE title = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, fullName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                movies.add(rs.getString("title"));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return movies;
    }

    public static List<Integer> getAvailableSeats(int sessionId)
    {
        List<Integer> availableSeats = new ArrayList<>();
        try (Connection conn = getConnection())
        {
            String query = "SELECT seat_number FROM Tickets WHERE session_id = ? AND customer_name IS NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                availableSeats.add(rs.getInt("seat_number"));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return availableSeats;
    }

    public static boolean selectSeat(int sessionId, int seatNumber)
    {
        try (Connection conn = getConnection())
        {
            String query = "UPDATE Tickets SET customer_name = ? WHERE session_id = ? AND seat_number = ? AND customer_name IS NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "reserved"); // Placeholder for customer name
            stmt.setInt(2, sessionId);
            stmt.setInt(3, seatNumber);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
    public static void updateTicketSales(int ticketId, String customerName)
    {
        try (Connection conn = getConnection())
        {
            String query = "UPDATE Tickets SET customer_name = ? WHERE ticket_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, customerName);
            stmt.setInt(2, ticketId);
            stmt.executeUpdate();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void updateProductInventory(int productId, int quantity)
    {
        try (Connection conn = getConnection())
        {
            String query = "UPDATE Products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
