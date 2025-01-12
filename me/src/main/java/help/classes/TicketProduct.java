package help.classes;

import javafx.beans.property.*;
import java.math.BigDecimal;

public class TicketProduct {

    private IntegerProperty ticketId;
    private StringProperty productName;
    private IntegerProperty quantity;
    private ObjectProperty<BigDecimal> price;

    /**
     * Constructs a TicketProduct with the specified details.
     * 
     * @param ticketId the unique ID of the ticket product.
     * @param productName the name of the product.
     * @param quantity the quantity of the product.
     * @param price the price of the product.
     */
    public TicketProduct(int ticketId, String productName, int quantity, BigDecimal price) {
        this.ticketId = new SimpleIntegerProperty(ticketId);
        this.productName = new SimpleStringProperty(productName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleObjectProperty<>(price);
    }

    /**
     * Gets the ticket ID.
     * 
     * @return the ticket ID.
     */
    public int getTicketId() {
        return ticketId.get();
    }

    /**
     * Sets the ticket ID.
     * 
     * @param ticketId the ticket ID to set.
     */
    public void setTicketId(int ticketId) {
        this.ticketId.set(ticketId);
    }

    /**
     * Returns the property for the ticket ID.
     * 
     * @return the ticket ID property.
     */
    public IntegerProperty ticketIdProperty() {
        return ticketId;
    }

    /**
     * Gets the product name.
     * 
     * @return the product name.
     */
    public String getProductName() {
        return productName.get();
    }

    /**
     * Sets the product name.
     * 
     * @param productName the product name to set.
     */
    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    /**
     * Returns the property for the product name.
     * 
     * @return the product name property.
     */
    public StringProperty productNameProperty() {
        return productName;
    }

    /**
     * Gets the quantity of the product.
     * 
     * @return the quantity.
     */
    public int getQuantity() {
        return quantity.get();
    }

    /**
     * Sets the quantity of the product.
     * 
     * @param quantity the quantity to set.
     */
    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    /**
     * Returns the property for the quantity.
     * 
     * @return the quantity property.
     */
    public IntegerProperty quantityProperty() {
        return quantity;
    }

    /**
     * Gets the price of the product.
     * 
     * @return the price.
     */
    public BigDecimal getPrice() {
        return price.get();
    }

    /**
     * Sets the price of the product.
     * 
     * @param price the price to set.
     */
    public void setPrice(BigDecimal price) {
        this.price.set(price);
    }

    /**
     * Returns the property for the price.
     * 
     * @return the price property.
     */
    public ObjectProperty<BigDecimal> priceProperty() {
        return price;
    }

    /**
     * Returns a string representation of the TicketProduct.
     * 
     * @return a string representing the TicketProduct.
     */
    @Override
    public String toString() {
        return "TicketProduct{" +
                "ticketId=" + ticketId.get() +
                ", productName='" + productName.get() + '\'' +
                ", quantity=" + quantity.get() +
                ", price=" + price.get() +
                '}';
    }
}
