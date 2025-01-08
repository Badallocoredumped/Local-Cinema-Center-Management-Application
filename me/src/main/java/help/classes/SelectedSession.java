package help.classes;

public class SelectedSession 
{
    private static SelectedSession instance;
    private Session session;

    private SelectedSession() 
    {
        this.session = null;
    }

    public static SelectedSession getInstance() 
    {
        if (instance == null) 
        {
            instance = new SelectedSession();
        }
        return instance;
    }

    public Session getSession() 
    {
        return session;
    }

    public void setSession(Session session) 
    {
        this.session = session;
    }

    public String getDay() 
    {
        if (session != null) 
        {
            return session.getDay();
        }
        return null;
    }

    public String getSessionTime() 
    {
        if (session != null) 
        {
            return session.getSession();
        }
        return null;
    }
}