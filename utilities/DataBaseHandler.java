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

