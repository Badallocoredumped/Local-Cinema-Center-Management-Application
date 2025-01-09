package help.classes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;

public class Movie {
    private int id;
    private StringProperty title;
    private byte[] posterImage; // BLOB data
    private ObjectProperty<Image> posterImageView; // JavaFX Image
    private StringProperty genre;
    private StringProperty summary;
    private StringProperty duration;

    // Constructor
    public Movie(int id, String title, byte[] posterImage, String genre, String summary, String duration) 
    {
        this.id = id;
        this.title = new SimpleStringProperty(title);
        this.posterImage = posterImage;
        this.posterImageView = new SimpleObjectProperty<>(convertToImage(posterImage));
        this.genre = new SimpleStringProperty(genre);
        this.summary = new SimpleStringProperty(summary);
        this.duration = new SimpleStringProperty(duration);
    }


    private Image convertToImage(byte[] imageData) {
        if (imageData != null && imageData.length > 0) {
            return new Image(new ByteArrayInputStream(imageData));
        }
        return null;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public byte[] getPosterImage() 
    {
        return posterImage;
    }

    /* public void setPosterImage(byte[] image) 
    {
        this.posterImage.set(image);
        this.posterImageView.set(convertToImage(image));
    } */

    /* public ObjectProperty<byte[]> posterImageProperty() {
        return posterImage;
    } */

    public Image getPosterImageView() {
        return posterImageView.get();
    }

    public ObjectProperty<Image> posterImageViewProperty() {
        return posterImageView;
    }

    public String getGenre() {
        return genre.get();
    }

    public void setGenre(String genre) {
        this.genre.set(genre);
    }

    public StringProperty genreProperty() {
        return genre;
    }

    public String getSummary() {
        return summary.get();
    }

    public void setSummary(String summary) {
        this.summary.set(summary);
    }

    public StringProperty summaryProperty() {
        return summary;
    }

    public String getDuration() {
        return duration.get();
    }

    public void setDuration(String duration) {
        this.duration.set(duration);
    }

    public StringProperty durationProperty() {
        return duration;
    }
}
