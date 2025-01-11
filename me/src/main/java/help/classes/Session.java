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


    public Session(String day, String session, String hall, String vacantSeats,int sessionId) 
    {
        this.day = new SimpleStringProperty(day);
        this.session = new SimpleStringProperty(session);
        this.hall = hall;
        this.vacantSeats = new SimpleStringProperty(vacantSeats);
        this.sessionId = sessionId;
    }

    public int getSessionId() 
    {
        return sessionId;
    }

    public void setSessionId(int sessionId) 
    {
        this.sessionId = sessionId;
    }

    public StringProperty dayProperty() 
    {
        return day;
    }

    public StringProperty sessionProperty() 
    {
        return session;
    }

    public StringProperty hallProperty() 
    {
        return new SimpleStringProperty(hall);
    }

    public StringProperty vacantSeatsProperty() 
    {
        return vacantSeats;
    }

    public String getDay() 
    {
        return day.get();
    }

    public String getSession() 
    {
        if(session == null)
        {
            return null;
        }
        return session.get();
    }

    public String getHall()
    {
        if(hall == null)
        {
            return null;
        }
        return hall;
    }

    public void setHall(String hall) 
    {
        this.hall = hall;
    }

    @Override
    public String toString() 
    {
        return "Date: " + day + ", Time: " + session + ", Hall: " + hall + ", Vacant Seats: " + vacantSeats;
    }
}