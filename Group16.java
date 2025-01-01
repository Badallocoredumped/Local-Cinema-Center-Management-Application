

public class Group16 
{
    public static void main(String[] args)
    {  
        DataBaseHandler dbhandler = new DataBaseHandler();
        String title, poster, genre, summary;
        title = "Teca";
        poster = "https://i.imgur.com/8QKQq6L.jpg";
        genre = "Action";
        summary = "Teca is fighting";
        dbhandler.AddMovie(title, poster, genre, summary);
        dbhandler.GetAllMovies();
    }
}
