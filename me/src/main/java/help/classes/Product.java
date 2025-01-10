package help.classes;

import javafx.beans.property.*;
import java.math.BigDecimal;

public class Product {
    private final ObjectProperty<byte[]> imageData;
    private final StringProperty name;
    private final ObjectProperty<BigDecimal> price;
    private final IntegerProperty stockAvailability;
    private final ObjectProperty<BigDecimal> taxRate;

    // Default constructor
    public Product() {
        this.imageData = new SimpleObjectProperty<>(new byte[0]);
        this.name = new SimpleStringProperty("");
        this.price = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.stockAvailability = new SimpleIntegerProperty(0);
        this.taxRate = new SimpleObjectProperty<>(BigDecimal.ZERO);
    }

    // Constructor with parameters
    public Product(byte[] imageData, String name, BigDecimal price, int stockAvailability, BigDecimal taxRate) {
        this.imageData = new SimpleObjectProperty<>(imageData);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleObjectProperty<>(price);
        this.stockAvailability = new SimpleIntegerProperty(stockAvailability);
        this.taxRate = new SimpleObjectProperty<>(taxRate);
    }

    // Getters for JavaFX properties
    public ObjectProperty<byte[]> imageDataProperty() {
        return imageData;
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

    public ObjectProperty<BigDecimal> taxRateProperty() {
        return taxRate;
    }

    // Getter methods
    public byte[] getImageData() {
        return imageData.get();
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

    public BigDecimal getTaxRate() {
        return taxRate.get();
    }

    // Setter methods
    public void setImageData(byte[] imageData) {
        this.imageData.set(imageData);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setPrice(BigDecimal price) {
        this.price.set(price);
    }

    public void setStockAvailability(int stockAvailability) {
        this.stockAvailability.set(stockAvailability);
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate.set(taxRate);
    }
}