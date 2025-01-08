package help.classes;

import javafx.beans.property.*;

import java.math.BigDecimal;

public class Product {

    private final StringProperty image;
    private final StringProperty name;
    private final ObjectProperty<BigDecimal> price;
    private final IntegerProperty stockAvailability;

    // Default constructor with default values
    public Product() 
    {
        this.image = new SimpleStringProperty("");
        this.name = new SimpleStringProperty("");
        this.price = new SimpleObjectProperty<>(BigDecimal.ZERO);  // Default price to 0
        this.stockAvailability = new SimpleIntegerProperty(0);  // Default stock to 0
    }

    // Constructor with parameters
    public Product(String image, String name, BigDecimal price, int stockAvailability) 
    {
        this.image = new SimpleStringProperty(image);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleObjectProperty<>(price);
        this.stockAvailability = new SimpleIntegerProperty(stockAvailability);
    }

    // Getters for JavaFX properties
    public StringProperty imageProperty() {
        return image;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<BigDecimal> priceProperty() {
        return price;
    }

    public IntegerProperty stockAvailabilityProperty() {
        return stockAvailability;
    }

    // Regular getters for non-properties (values)
    public String getImage() {
        return image.get();
    }

    public String getName() {
        return name.get();
    }

    public BigDecimal getPrice() {
        return price.get();
    }

    public int getStockAvailability() {
        return stockAvailability.get();
    }

    // Setters for regular values
    public void setImage(String image) {
        this.image.set(image);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setPrice(BigDecimal price) {
        this.price.set(price);
    }

    public void setStockAvailability(int stockAvailability) 
    {
        this.stockAvailability.set(stockAvailability);
    }
}
