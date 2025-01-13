package help.classes;

import javafx.beans.property.*;
import java.math.BigDecimal;

/**
 * Class representing a product in the system.
 */
public class Product {

    private final ObjectProperty<byte[]> imageData;
    private final StringProperty name;
    private final ObjectProperty<BigDecimal> price;
    private final IntegerProperty stockAvailability;
    private final ObjectProperty<BigDecimal> taxRate;
    private final StringProperty category;

    /**
     * Default constructor to initialize an empty product.
     */
    public Product() {
        this.imageData = new SimpleObjectProperty<>(new byte[0]);
        this.name = new SimpleStringProperty("");
        this.price = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.stockAvailability = new SimpleIntegerProperty(0);
        this.taxRate = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.category = new SimpleStringProperty(""); // Initialize category
    }

    /**
     * Constructor with parameters to initialize a product with specified details.
     *
     * @param imageData The image data for the product.
     * @param name The name of the product.
     * @param price The price of the product.
     * @param stockAvailability The available stock for the product.
     * @param taxRate The tax rate applied to the product.
     * @param category The category of the product.
     */
    public Product(byte[] imageData, String name, BigDecimal price, int stockAvailability, BigDecimal taxRate, String category) {
        this.imageData = new SimpleObjectProperty<>(imageData);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleObjectProperty<>(price);
        this.stockAvailability = new SimpleIntegerProperty(stockAvailability);
        this.taxRate = new SimpleObjectProperty<>(taxRate);
        this.category = new SimpleStringProperty(category);
    }

    /**
     * Returns the JavaFX property for the product's image data.
     *
     * @return The image data property.
     */
    public ObjectProperty<byte[]> imageDataProperty() {
        return imageData;
    }

    /**
     * Returns the JavaFX property for the product's name.
     *
     * @return The name property.
     */
    public StringProperty nameProperty() {
        return name;
    }

    /**
     * Returns the JavaFX property for the product's price.
     *
     * @return The price property.
     */
    public ObjectProperty<BigDecimal> priceProperty() {
        return price;
    }

    /**
     * Returns the JavaFX property for the product's stock availability.
     *
     * @return The stock availability property.
     */
    public IntegerProperty stockAvailabilityProperty() {
        return stockAvailability;
    }

    /**
     * Returns the JavaFX property for the product's tax rate.
     *
     * @return The tax rate property.
     */
    public ObjectProperty<BigDecimal> taxRateProperty() {
        return taxRate;
    }

    /**
     * Returns the JavaFX property for the product's category.
     *
     * @return The category property.
     */
    public StringProperty categoryProperty() {
        return category;
    }

    /**
     * Gets the image data of the product.
     *
     * @return The image data.
     */
    public byte[] getImageData() {
        return imageData.get();
    }

    /**
     * Gets the name of the product.
     *
     * @return The name of the product.
     */
    public String getName() {
        return name.get();
    }

    /**
     * Gets the price of the product.
     *
     * @return The price of the product.
     */
    public BigDecimal getPrice() {
        return price.get();
    }

    /**
     * Gets the stock availability of the product.
     *
     * @return The stock availability.
     */
    public int getStockAvailability() {
        return stockAvailability.get();
    }

    /**
     * Gets the tax rate applied to the product.
     *
     * @return The tax rate.
     */
    public BigDecimal getTaxRate() {
        return taxRate.get();
    }

    /**
     * Gets the category of the product.
     *
     * @return The category.
     */
    public String getCategory() {
        return category.get();
    }

    /**
     * Sets the category of the product.
     *
     * @param category The new category.
     */
    public void setCategory(String category) {
        this.category.set(category);
    }

    /**
     * Sets the image data of the product.
     *
     * @param imageData The new image data.
     */
    public void setImageData(byte[] imageData) {
        this.imageData.set(imageData);
    }

    /**
     * Sets the name of the product.
     *
     * @param name The new name.
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * Sets the price of the product.
     *
     * @param price The new price.
     */
    public void setPrice(BigDecimal price) {
        this.price.set(price);
    }

    /**
     * Sets the stock availability of the product.
     *
     * @param stockAvailability The new stock availability.
     */
    public void setStockAvailability(int stockAvailability) {
        this.stockAvailability.set(stockAvailability);
    }

    /**
     * Sets the tax rate of the product.
     *
     * @param taxRate The new tax rate.
     */
    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate.set(taxRate);
    }
}