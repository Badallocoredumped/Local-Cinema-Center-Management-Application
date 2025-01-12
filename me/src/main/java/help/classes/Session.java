package help.classes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Session 
{
    private final StringProperty day;
    private final StringProperty session;
    private  String hall;
    private final StringProperty vacantSeats;
    private int sessionId;

    /**
     * Constructor to initialize a session object with specified values.
     * 
     * @param day The day of the session.
     * @param session The time of the session.
     * @param hall The name of the hall.
     * @param vacantSeats The number of vacant seats available.
     * @param sessionId The ID of the session.
     */
    public Session(String day, String session, String hall, String vacantSeats,int sessionId) 
    {
        this.day = new SimpleStringProperty(day);
        this.session = new SimpleStringProperty(session);
        this.hall = hall;
        this.vacantSeats = new SimpleStringProperty(vacantSeats);
        this.sessionId = sessionId;
    }

    /**
     * Gets the session ID.
     * 
     * @return The session ID.
     */
    public int getSessionId() 
    {
        return sessionId;
    }

    /**
     * Sets the session ID.
     * 
     * @param sessionId The session ID to set.
     */
    public void setSessionId(int sessionId) 
    {
        this.sessionId = sessionId;
    }

    /**
     * Gets the day property for the session.
     * 
     * @return The day as a StringProperty.
     */
    public StringProperty dayProperty() 
    {
        return day;
    }

    /**
     * Gets the session time property.
     * 
     * @return The session time as a StringProperty.
     */
    public StringProperty sessionProperty() 
    {
        return session;
    }

    /**
     * Gets the hall property for the session.
     * 
     * @return The hall as a StringProperty.
     */
    public StringProperty hallProperty() 
    {
        return new SimpleStringProperty(hall);
    }

    /**
     * Gets the vacant seats property for the session.
     * 
     * @return The vacant seats as a StringProperty.
     */
    public StringProperty vacantSeatsProperty() 
    {
        return vacantSeats;
    }

    /**
     * Gets the day of the session.
     * 
     * @return The day of the session as a String.
     */
    public String getDay() 
    {
        return day.get();
    }

    /**
     * Gets the session time.
     * 
     * @return The session time as a String.
     */
    public String getSession() 
    {
        if(session == null)
        {
            return null;
        }
        return session.get();
    }

    /**
     * Gets the hall name.
     * 
     * @return The hall name as a String.
     */
    public String getHall()
    {
        if(hall == null)
        {
            return null;
        }
        return hall;
    }

    /**
     * Sets the hall name.
     * 
     * @param hall The hall name to set.
     */
    public void setHall(String hall) 
    {
        this.hall = hall;
    }

    /**
     * Converts the session to a String representation.
     * 
     * @return A string describing the session details.
     */
    @Override
    public String toString() 
    {
        return "Date: " + day + ", Time: " + session + ", Hall: " + hall + ", Vacant Seats: " + vacantSeats;
    }
}