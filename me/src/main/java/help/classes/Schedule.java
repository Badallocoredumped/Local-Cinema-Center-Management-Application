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

    public Schedule(int sessionId, int movieId, String hallName, LocalDate sessionDate, Time startTime, int vacantSeats) {
        this.sessionId = new SimpleIntegerProperty(sessionId);
        this.movieId = new SimpleIntegerProperty(movieId);
        this.hallName = new SimpleStringProperty(hallName);
        this.sessionDate = new SimpleObjectProperty<>(sessionDate);
        this.startTime = new SimpleObjectProperty<>(startTime);
        this.vacantSeats = new SimpleIntegerProperty(vacantSeats);
    }

    // Getters for properties (important!)
    public IntegerProperty sessionIdProperty() { return sessionId; }
    public IntegerProperty movieIdProperty() { return movieId; }
    public StringProperty hallNameProperty() { return hallName; }
    public ObjectProperty<LocalDate> sessionDateProperty() { return sessionDate; }
    public ObjectProperty<Time> startTimeProperty() { return startTime; }
    public IntegerProperty vacantSeatsProperty() { return vacantSeats; }

    // Regular getters (optional, but good practice)
    public int getSessionId() { return sessionId.get(); }
    public int getMovieId() { return movieId.get(); }
    public String getHallName() { return hallName.get(); }
    public LocalDate getSessionDate() { return sessionDate.get(); }
    public Time getStartTime() { return startTime.get(); }
    public int getVacantSeats() { return vacantSeats.get(); }

    // Regular setters (optional)
    public void setSessionId(int sessionId) { this.sessionId.set(sessionId); }
    public void setMovieId(int movieId) { this.movieId.set(movieId); }
    public void setHallName(String hallName) { this.hallName.set(hallName); }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate.set(sessionDate); }
    public void setStartTime(Time startTime) { this.startTime.set(startTime); }
    public void setVacantSeats(int vacantSeats) { this.vacantSeats.set(vacantSeats); }

    public int getID(){ return getSessionId();}
}