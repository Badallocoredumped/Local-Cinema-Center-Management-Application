package help.classes;

import java.util.List;
import java.util.stream.Collectors;

import help.utilities.MovieDBO;

/**
 * Service class that handles operations related to Movie management.
 */
public class MovieService 
{
    private MovieDBO movieDao = new MovieDBO();

    /**
     * Searches for movies by genre.
     *
     * @param genre The genre to search for.
     * @return A list of movies that match the specified genre.
     * @throws Exception If an error occurs during the search operation.
     */
    public List<Movie> searchByGenre(String genre) throws Exception 
    {
        return movieDao.findAll().stream()
                .filter(movie -> movie.getGenre().equalsIgnoreCase(genre))
                .collect(Collectors.toList());
    }

    /**
     * Searches for movies by a partial name.
     *
     * @param partialName The partial name to search for.
     * @return A list of movies whose title contains the specified partial name (case-insensitive).
     * @throws Exception If an error occurs during the search operation.
     */
    public List<Movie> searchByPartialName(String partialName) throws Exception 
    {
        return movieDao.findAll().stream()
                .filter(movie -> movie.getTitle().toLowerCase().contains(partialName.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Searches for movies by an exact title match.
     *
     * @param fullName The full title to search for.
     * @return A list of movies whose title exactly matches the specified full name (case-insensitive).
     * @throws Exception If an error occurs during the search operation.
     */
    public List<Movie> searchByFullName(String fullName) throws Exception 
    {
        return movieDao.findAll().stream()
                .filter(movie -> movie.getTitle().equalsIgnoreCase(fullName))
                .collect(Collectors.toList());
    }
}