package help.classes;

import java.sql.Time;
import java.time.LocalDate;
import javafx.beans.property.*;

public class Schedule {

    private final IntegerProperty sessionId;
    private final IntegerProperty movieId;
    private final StringProperty hallName;
    private final ObjectProperty<LocalDate> sessionDate; // Use ObjectProperty for LocalDate
    private final ObjectProperty<Time> startTime; // Use ObjectProperty for Time
    private final IntegerProperty vacantSeats;

    /**
     * Constructor to initialize a movie session schedule.
     *
     * @param sessionId The unique identifier for the session.
     * @param movieId The unique identifier for the movie.
     * @param hallName The name of the hall where the session is taking place.
     * @param sessionDate The date when the session is scheduled.
     * @param startTime The starting time of the session.
     * @param vacantSeats The number of vacant seats available for the session.
     */
    public Schedule(int sessionId, int movieId, String hallName, LocalDate sessionDate, Time startTime, int vacantSeats) {
        this.sessionId = new SimpleIntegerProperty(sessionId);
        this.movieId = new SimpleIntegerProperty(movieId);
        this.hallName = new SimpleStringProperty(hallName);
        this.sessionDate = new SimpleObjectProperty<>(sessionDate);
        this.startTime = new SimpleObjectProperty<>(startTime);
        this.vacantSeats = new SimpleIntegerProperty(vacantSeats);
    }

    // Getter for sessionId property
    /**
     * Gets the session ID property for the movie session.
     * 
     * @return The session ID property.
     */
    public IntegerProperty sessionIdProperty() { return sessionId; }

    // Getter for movieId property
    /**
     * Gets the movie ID property for the movie session.
     * 
     * @return The movie ID property.
     */
    public IntegerProperty movieIdProperty() { return movieId; }

    // Getter for hallName property
    /**
     * Gets the hall name property for the movie session.
     * 
     * @return The hall name property.
     */
    public StringProperty hallNameProperty() { return hallName; }
  
    // Getter for sessionDate property
    /**
     * Gets the session date property for the movie session.
     * 
     * @return The session date property.
     */
    public ObjectProperty<LocalDate> sessionDateProperty() { return sessionDate; }

    // Getter for startTime property
    /**
     * Gets the start time property for the movie session.
     * 
     * @return The start time property.
     */
    public ObjectProperty<Time> startTimeProperty() { return startTime; }

    // Getter for vacantSeats property
    /**
     * Gets the vacant seats property for the movie session.
     * 
     * @return The vacant seats property.
     */
    public IntegerProperty vacantSeatsProperty() { return vacantSeats; }


    // Regular getter methods (non-property)
    /**
     * Gets the session ID.
     * 
     * @return The session ID.
     */
    public int getSessionId() { return sessionId.get(); }
    /**
     * Gets the movie ID.
     * 
     * @return The movie ID.
     */
    public int getMovieId() { return movieId.get(); }

    /**
     * Gets the hall name.
     * 
     * @return The hall name.
     */
    public String getHallName() { return hallName.get(); }

    /**
     * Gets the session date.
     * 
     * @return The session date.
     */
    public LocalDate getSessionDate() { return sessionDate.get(); }

    /**
     * Gets the start time of the session.
     * 
     * @return The start time of the session.
     */
    public Time getStartTime() { return startTime.get(); }
 
    /**
     * Gets the number of vacant seats available for the session.
     * 
     * @return The number of vacant seats.
     */
    public int getVacantSeats() { return vacantSeats.get(); }

    // Regular setter methods
    /**
     * Sets the session ID.
     * 
     * @param sessionId The session ID to set.
     */
    public void setSessionId(int sessionId) { this.sessionId.set(sessionId); }
  
    /**
     * Sets the movie ID.
     * 
     * @param movieId The movie ID to set.
     */
    public void setMovieId(int movieId) { this.movieId.set(movieId); }

    /**
     * Sets the hall name.
     * 
     * @param hallName The hall name to set.
     */
    public void setHallName(String hallName) { this.hallName.set(hallName); }

    /**
     * Sets the session date.
     * 
     * @param sessionDate The session date to set.
     */
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate.set(sessionDate); }
    /**
     * Sets the start time of the session.
     * 
     * @param startTime The start time to set.
     */
    public void setStartTime(Time startTime) { this.startTime.set(startTime); }
    /**
     * Sets the number of vacant seats available for the session.
     * 
     * @param vacantSeats The number of vacant seats to set.
     */
    public void setVacantSeats(int vacantSeats) { this.vacantSeats.set(vacantSeats); }


    /**
     * Gets the session ID (same as getSessionId).
     * 
     * @return The session ID.
     */
    public int getID(){ return getSessionId();}
}