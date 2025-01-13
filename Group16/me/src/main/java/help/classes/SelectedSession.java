package help.classes;

/**
 * Class representing a selected session for a movie.
 */
public class SelectedSession 
{
    private static SelectedSession instance;
    private Session session;

    private SelectedSession() 
    {
        this.session = null;
    }

    /**
     * Gets the singleton instance of the SelectedSession class.
     * 
     * @return The singleton instance of SelectedSession.
     */
    public static SelectedSession getInstance() 
    {
        if (instance == null) 
        {
            instance = new SelectedSession();
        }
        return instance;
    }

    /**
     * Gets the current selected session.
     * 
     * @return The selected session object.
     */
    public Session getSession() 
    {
        return session;
    }

    /**
     * Sets the selected session.
     * 
     * @param session The session to set.
     */
    public void setSession(Session session) 
    {
        this.session = session;
    }

    
}