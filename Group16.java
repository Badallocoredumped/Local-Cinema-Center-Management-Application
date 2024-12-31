import utilities.DataBaseHandler;

public class Group16 
{
    //In case y'all want to test admin operations
    public static void main(String[] args)
    {  
        DataBaseHandler dbhandler = new DataBaseHandler();
        String title, poster, genre, summary;
        title = "The Factory";
        poster = "C:/GitHub/Local-Cinema-Center-Management-Application/Movie/Poster/Khas.jpg";
        genre = "Horror";
        summary = "The ghosts of the tobacco factory are back to haunt the group 16, how will they handle it?!!";
        dbhandler.AddMovie(title, poster, genre, summary);
        //dbhandler.FullUpdateMovie(1, title, poster, genre, summary);
        dbhandler.GetAllMovies();
        
    }
}
