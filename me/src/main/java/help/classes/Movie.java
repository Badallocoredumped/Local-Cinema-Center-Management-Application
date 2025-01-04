package help.classes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Movie 
{
    private int id;
    private StringProperty title;
    private StringProperty posterUrl;
    private StringProperty genre;
    private StringProperty summary;
    private StringProperty duration;
    private byte[] posterData;

    public Movie(int id, String title, String posterUrl, String genre, String summary, String duration, byte[] posterData) 
    {
        this.id = id;
        this.title = new SimpleStringProperty(title);
        this.posterUrl = new SimpleStringProperty(posterUrl);
        this.genre = new SimpleStringProperty(genre);
        this.summary = new SimpleStringProperty(summary);
        this.duration = new SimpleStringProperty(duration);
        this.posterData = posterData;
    }

    // Getters and setters
    public int getId() 
    {
        return id;
    }

    public void setId(int id) 
    {
        this.id = id;
    }

    public String getTitle() 
    {
        return title.get();
    }

    public void setTitle(String title) 
    {
        this.title.set(title);
    }

    public StringProperty titleProperty() 
    {
        return title;
    }

    public String getPosterUrl() 
    {
        return posterUrl.get();
    }

    public void setPosterUrl(String posterUrl) 
    {
        this.posterUrl.set(posterUrl);
    }

    public StringProperty posterUrlProperty() 
    {
        return posterUrl;
    }

    public String getGenre() 
    {
        return genre.get();
    }

    public void setGenre(String genre) 
    {
        this.genre.set(genre);
    }

    public StringProperty genreProperty() 
    {
        return genre;
    }

    public String getSummary() 
    {
        return summary.get();
    }

    public void setSummary(String summary) 
    {
        this.summary.set(summary);
    }

    public StringProperty summaryProperty() 
    {
        return summary;
    }

    public String getDuration() 
    {
        return duration.get();
    }

    public void setDuration(String duration) 
    {
        this.duration.set(duration);
    }

    public StringProperty durationProperty() 
    {
        return duration;
    }

    public byte[] getPosterData() 
    {
        return posterData;
    }

    public void setPosterData(byte[] posterData) 
    {
        this.posterData = posterData;
    }
}