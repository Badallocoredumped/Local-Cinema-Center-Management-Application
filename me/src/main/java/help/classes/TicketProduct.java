package help.classes;

import javafx.beans.property.*;
import java.math.BigDecimal;

public class TicketProduct {

    private IntegerProperty ticketId;
    private StringProperty productName;
    private IntegerProperty quantity;
    private ObjectProperty<BigDecimal> price;

    // Constructor
    public TicketProduct(int ticketId, String productName, int quantity, BigDecimal price) {
        this.ticketId = new SimpleIntegerProperty(ticketId);
        this.productName = new SimpleStringProperty(productName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleObjectProperty<>(price);
    }

    // Getters and Setters for Properties
    public int getTicketId() {
        return ticketId.get();
    }

    public void setTicketId(int ticketId) {
        this.ticketId.set(ticketId);
    }

    public IntegerProperty ticketIdProperty() {
        return ticketId;
    }

    public String getProductName() {
        return productName.get();
    }

    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    public StringProperty productNameProperty() {
        return productName;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price.get();
    }

    public void setPrice(BigDecimal price) {
        this.price.set(price);
    }

    public ObjectProperty<BigDecimal> priceProperty() {
        return price;
    }

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
