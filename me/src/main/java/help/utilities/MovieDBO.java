package help.utilities;

import java.sql.*;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import help.classes.Movie;

public class MovieDBO {
    
    private byte[] getBlobData(Blob blob) throws SQLException 
    {
        if (blob == null) return null;
        try (InputStream is = blob.getBinaryStream()) 
        {
            return is.readAllBytes();
        } 
        catch (IOException e) 
        {
            throw new SQLException("Error reading BLOB data", e);
        }
    }

    public void debugPosterImage(int movieId) throws Exception {
        String query = "SELECT poster_image FROM Movies WHERE movie_id = ?";
        
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    byte[] posterData = rs.getBytes("poster_image");
                    if (posterData != null) {
                        System.out.println("Movie ID: " + movieId);
                        System.out.println("Poster data length: " + posterData.length);
                        System.out.println("First few bytes: " + 
                            Arrays.toString(Arrays.copyOf(posterData, Math.min(10, posterData.length))));
                    } else {
                        System.out.println("No poster data found for movie ID: " + movieId);
                    }
                }
            }
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

    public List<Movie> findAll() throws Exception 
    {
        System.out.println("Finding all movies...");
        List<Movie> movies = new ArrayList<>();
        
        String query = "SELECT movie_id, title, poster_image, genre, summary, duration FROM Movies";
        
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) 
            {
                byte[] posterData = rs.getBytes("poster_image");
                System.out.println("Loading movie: " + rs.getString("title"));
                System.out.println("Poster data size: " + 
                    (posterData != null ? posterData.length + " bytes" : "null"));
                
                Movie movie = new Movie(
                    rs.getInt("movie_id"),
                    rs.getString("title"),
                    posterData,
                    rs.getString("genre"),
                    rs.getString("summary"),
                    rs.getString("duration")
                );
                movies.add(movie);
            }
        } 
        catch (SQLException e) 
        {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        return movies;
    }
}