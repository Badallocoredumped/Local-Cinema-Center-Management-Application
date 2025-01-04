package help.classes;

import java.util.List;
import java.util.stream.Collectors;

import help.utilities.MovieDBO;

public class MovieService 
{
    private MovieDBO movieDao = new MovieDBO();

    public List<Movie> searchByGenre(String genre) 
    {
        return movieDao.findAll().stream()
                .filter(movie -> movie.getGenre().equalsIgnoreCase(genre))
                .collect(Collectors.toList());
    }

    public List<Movie> searchByPartialName(String partialName) 
    {
        return movieDao.findAll().stream()
                .filter(movie -> movie.getTitle().toLowerCase().contains(partialName.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Movie> searchByFullName(String fullName) 
    {
        return movieDao.findAll().stream()
                .filter(movie -> movie.getTitle().equalsIgnoreCase(fullName))
                .collect(Collectors.toList());
    }
}