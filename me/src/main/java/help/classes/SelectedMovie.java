package help.classes;

public class SelectedMovie 
{
    private static SelectedMovie instance;
    private Movie movie;

    private SelectedMovie() 
    {

    }

    public static SelectedMovie getInstance() 
    {
        if (instance == null) 
        {
            instance = new SelectedMovie();
        }
        return instance;
    }

    public Movie getMovie() 
    {
        return movie;
    }

    public void setMovie(Movie movie) 
    {
        this.movie = movie;
    }
}