import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseHandler 
{
    private final String url = "jdbc:mysql://localhost:3306/Group16"; 
    private final String username = "myuser"; 
    private final String password = "1234"; 
    private Connection dbconnection;

    public DataBaseHandler()
    {
        try 
        {
            dbconnection = DriverManager.getConnection(url, username, password);
        
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

            String query2do = "INSERT INTO movies (title, poster, genre, summary) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = dbconnection.prepareStatement(query2do))
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

        String query2do = "SELECT summary FROM movies WHERE id = ?";
        try (PreparedStatement selectStatement = dbconnection.prepareStatement(query2do))
        {
            selectStatement.setInt(1, movieId);
            ResultSet infoSet = selectStatement.executeQuery();

            if (infoSet.next()) 
            {
                String summaryPath = infoSet.getString("summary");

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryPath))) 
                {
                    writer.write(newSummaryText);
                    writer.newLine();  
                    query2do = "UPDATE movies SET title = ?, poster = ?, genre = ?, summary = ? WHERE id = ?";
                    try(PreparedStatement statement = dbconnection.prepareStatement(query2do))
                    {
                        statement.setString(1, newTitle);
                        statement.setString(2, newPoster);
                        statement.setString(3, newGenre);
                        statement.setString(4, summaryPath);
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

        String countQuery = "SELECT COUNT(*) AS total FROM employees";
        try (PreparedStatement countStatement = dbconnection.prepareStatement(countQuery)) 
        {
            ResultSet countResult = countStatement.executeQuery();
            String query2do = "SELECT id, title, poster, genre, summary FROM movies";

            try(PreparedStatement statement = dbconnection.prepareStatement(query2do))
            {
                ResultSet infoSet = statement.executeQuery(query2do);
        
                while (infoSet.next()) 
                {
                    int id = infoSet.getInt("id");
                    String title = infoSet.getString("title");
                    String poster = infoSet.getString("poster");
                    String genre = infoSet.getString("genre");
                    String summaryFile = infoSet.getString("summary");
                    String summary = ReadSummary(summaryFile);

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
}

public class DataBaseHandler
{
    private static final String URL = "jdbc:mysql://localhost:3306/cinemacenter";
    private static final String USER = "root";
    private static final String PASSWORD = "Admin_123";

    public static Connection getConnection() throws Exception
    {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

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