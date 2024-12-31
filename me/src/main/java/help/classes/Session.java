package help.classes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Session 
{
    private final StringProperty day;
    private final StringProperty session;
    private String hall;
    private final StringProperty vacantSeats;

    public Session(String day, String session, String hall, String vacantSeats) 
    {
        this.day = new SimpleStringProperty(day);
        this.session = new SimpleStringProperty(session);
        this.hall = hall;
        this.vacantSeats = new SimpleStringProperty(vacantSeats);
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
        return session.get();
    }

    public String getHall()
    {
        return hall;
    }

    public void setHall(String hall) 
    {
        this.hall = hall;
    }
}