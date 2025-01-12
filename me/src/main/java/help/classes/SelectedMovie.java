package help.classes;

public class SelectedMovie 
{
    private static SelectedMovie instance;
    private Movie movie;

    /*private SelectedMovie() 
    {

    }*/

    /**
     * Returns the singleton instance of the SelectedMovie class. 
     * If the instance does not exist, a new instance is created.
     * 
     * @return The singleton instance of the SelectedMovie class.
     */
    public static SelectedMovie getInstance() 
    {
        if (instance == null) 
        {
            instance = new SelectedMovie();
        }
        return instance;
    }

    

    /**
     * Gets the movie object.
     * 
     * @return The movie object.
     */
    public Movie getMovie() 
    {
        return movie;
    }

    /**
     * Sets the movie object.
     * 
     * @param movie The movie object to set.
     */
    public void setMovie(Movie movie) 
    {
        this.movie = movie;
    }
}