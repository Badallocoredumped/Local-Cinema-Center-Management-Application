package me.src.main.java.help.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import me.src.main.java.help.classes.Movie;

public class MovieDBO 
{
    public List<Movie> findAll() 
    {
        List<Movie> movies = new ArrayList<>();
        try (Connection conn = DataBaseHandler.getConnection()) 
        {
            String query = "SELECT * FROM Movies";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) 
            {
                Movie movie = new Movie(
                    rs.getInt("movie_id"),
                    rs.getString("title"),
                    rs.getString("poster_url"),
                    rs.getString("genre"),
                    rs.getString("summary"),
                    rs.getString("duration"),
                    rs.getBytes("poster_image") 
                );
                movies.add(movie);
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return movies;
    }
}