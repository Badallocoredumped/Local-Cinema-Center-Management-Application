package help.classes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;

/**
 * Represents a movie with details such as title, genre, release year, and duration.
 */
public class Movie {
    private int id;
    private StringProperty title;
    private byte[] posterImage; // BLOB data
    private ObjectProperty<Image> posterImageView; // JavaFX Image
    private StringProperty genre;
    private StringProperty summary;
    private StringProperty duration;

    /**
     * Constructs a new {@code Movie} object with the specified details.
     * 
     * @param id The unique identifier for the movie.
     * @param title The title of the movie.
     * @param posterImage The poster image of the movie as a byte array.
     * @param genre The genre of the movie.
     * @param summary A brief summary or description of the movie.
     * @param duration The duration of the movie in a suitable format (e.g., "120 min").
     */
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

    /**
     * Converts a byte array representing image data into an {@link Image} object.
     * 
     * @param imageData The byte array representing the image data.
     * @return An {@link Image} object corresponding to the byte array, or {@code null} if the byte array is empty or {@code null}.
     */
    private Image convertToImage(byte[] imageData) {
        if (imageData != null && imageData.length > 0) {
            return new Image(new ByteArrayInputStream(imageData));
        }
        return null;
    }                                                                               

    // Getters and setters

    /**
     * Gets the ID of the movie.    
     *
     * @return
     */
    public int getId() {
        return id;
    }
    /**
     * Sets the ID of the movie.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the title of the movie.
     *
     * @return The title of the movie.
     */
    public String getTitle() {
        return title.get();
    }

    /**
     * Sets the title of the movie.
     *
     * @param title The title to set for the movie.
     */
    public void setTitle(String title) {
        this.title.set(title);
    }

    /**
     * Gets the title property of the movie as a {@link StringProperty}.
     *
     * @return The title property of the movie.
     */
    public StringProperty titleProperty() {
        return title;
    }

    /**
     * Gets the poster image data as a byte array.
     *
     * @return The byte array representing the poster image data.
     */
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

    /**
     * Gets the poster image as an {@link Image} object.
     *
     * @return The poster image as an {@link Image}.
     */
    public Image getPosterImageView() {
        return posterImageView.get();
    }

    /**
     * Gets the poster image view property of the movie as an {@link ObjectProperty}.
     *
     * @return The poster image view property of the movie.
     */
    public ObjectProperty<Image> posterImageViewProperty() {
        return posterImageView;
    }

    /**
     * Gets the genre of the movie.
     *
     * @return The genre of the movie.
     */
    public String getGenre() {
        return genre.get();
    }

    /**
     * Sets the genre of the movie.
     *
     * @param genre The genre to set for the movie.
     */
    public void setGenre(String genre) {
        this.genre.set(genre);
    }

    /**
     * Gets the genre property of the movie as a {@link StringProperty}.
     *
     * @return The genre property of the movie.
     */
    public StringProperty genreProperty() {
        return genre;
    }

    /**
     * Gets the summary of the movie.
     *
     * @return The summary of the movie.
     */
    public String getSummary() {
        return summary.get();
    }

    /**
     * Sets the summary of the movie.
     *
     * @param summary The summary to set for the movie.
     */
    public void setSummary(String summary) {
        this.summary.set(summary);
    }

    /**
     * Gets the summary property of the movie as a {@link StringProperty}.
     *
     * @return The summary property of the movie.
     */
    public StringProperty summaryProperty() {
        return summary;
    }

    /**
     * Gets the duration of the movie.
     *
     * @return The duration of the movie.
     */
    public String getDuration() {
        return duration.get();
    }

    /**
     * Sets the duration of the movie.
     *
     * @param duration The duration to set for the movie.
     */
    public void setDuration(String duration) {
        this.duration.set(duration);
    }

    /**
     * Gets the duration property of the movie as a {@link StringProperty}.
     *
     * @return The duration property of the movie.
     */
    public StringProperty durationProperty() {
        return duration;
    }
}