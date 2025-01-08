package help.utilities;

import java.sql.*;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import help.classes.Movie;

public class MovieDBO {
    
    private byte[] getBlobData(Blob blob) throws SQLException {
        if (blob == null) return null;
        try (InputStream is = blob.getBinaryStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new SQLException("Error reading BLOB data", e);
        }
    }

    public List<Movie> findAll() throws Exception {
        System.out.println("Finding all movies...");
        List<Movie> movies = new ArrayList<>();
        
        String query = "SELECT movie_id, title, poster_image, genre, summary, duration FROM Movies";
        
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Movie movie = new Movie(
                    rs.getInt("movie_id"),
                    rs.getString("title"),
                    getBlobData(rs.getBlob("poster_image")),
                    rs.getString("genre"),
                    rs.getString("summary"),
                    rs.getString("duration")
                );
                movies.add(movie);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        return movies;
    }
}