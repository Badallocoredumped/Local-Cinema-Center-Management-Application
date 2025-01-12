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
    /**
     * Retrieves the binary data from a BLOB (Binary Large Object) as a byte array.
     * 
     * <p>This method reads the binary data stored in a BLOB object and returns it as a byte array. If the BLOB is null, it returns null.</p>
     * 
     * @param blob The BLOB object from which the data will be extracted.
     * @return A byte array containing the binary data of the BLOB, or null if the BLOB is null.
     * 
     * @throws SQLException If an error occurs while reading the BLOB data.
     */
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

    /**
     * Retrieves and prints the poster image data for a specific movie.
     * 
     * <p>This method queries the database for the poster image associated with a movie and prints the details, including the movie ID, the length of the image data, and the first few bytes of the image.</p>
     * 
     * @param movieId The ID of the movie whose poster image data is to be retrieved.
     * 
     * @throws Exception If a database access error occurs or any other exception is thrown during execution.
     */
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

    /**
     * Reads the content of a summary file and returns it as a string.
     * 
     * <p>This method opens the file specified by the provided path, reads its contents line by line, and appends each line to a string. The entire file content is returned as a string.</p>
     * 
     * @param summaryPath The path to the summary file to be read.
     * 
     * @return A string containing the contents of the summary file.
     * 
     * @throws IOException If an I/O error occurs while reading the file.
     */
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

    /**
     * Retrieves all movies from the database and returns them as a list of {@link Movie} objects.
     * 
     * <p>This method queries the "Movies" table in the database to fetch all movie details including the movie ID, title, poster image, genre, summary, and duration.
     * It then creates {@link Movie} objects and adds them to a list, which is returned to the caller.</p>
     * 
     * @return A list of {@link Movie} objects representing all the movies in the database.
     * 
     * @throws Exception If an error occurs while accessing the database.
     */
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