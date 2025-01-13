
package help.classes;

import java.util.ArrayList;
import java.util.List;

/**
 * The Tickets class represents a movie ticket with associated details.
 * This is a singleton class to ensure a single instance of a ticket is used.
 */
public class Tickets 
{
    private static Tickets instance;
    private int ticketId;
    private int sessionId;
    private List<String> seatNumbers;
    private String customerName;
    private double totalSeatCost;
    private double totalProductCost;
    private double totalTax;
    private double totalCost;
    private int discountedSeatNumber;
    private String status;


    /**
     * Default constructor initializes the ticket with default values.
     */
    public Tickets() 
    {
        this.customerName = "";
        this.seatNumbers = new ArrayList<>();
        this.status = "ACTIVE";  // Set default status

    }

    /**
     * Resets the singleton instance of the Tickets class to its default state.
     * Clears all ticket details and sets the status to null.
     */
    public static void resetInstance() {
        if (instance != null) {
            instance.sessionId = 0;
            instance.seatNumbers.clear();
            instance.customerName = "";
            instance.totalSeatCost = 0.0;
            instance.totalProductCost = 0.0;
            instance.totalTax = 0.0;
            instance.totalCost = 0.0;
            instance.discountedSeatNumber = 0;
            instance = null;
        }
        System.out.println("Tickets instance reset");
    }

    /**
     * Gets the current status of the ticket.
     * 
     * @return the status of the ticket.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the ticket. Valid statuses are "ACTIVE" or "CANCELLED".
     * Throws an exception if the status is neither "ACTIVE" nor "CANCELLED".
     * 
     * @param status the status to set.
     * @throws IllegalArgumentException if the status is neither "ACTIVE" nor "CANCELLED".
     */
    public void setStatus(String status) {
        if (status != null && (status.equals("ACTIVE") || status.equals("CANCELLED"))) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("Status must be either ACTIVE or CANCELLED");
        }
    }

    /**
     * Returns the singleton instance of the {@code Tickets} class.
     * If no instance exists, it creates a new one.
     *
     * @return the singleton instance of {@code Tickets}.
     */
    public static Tickets getInstance() 
    {
        if (instance == null) 
        {
            instance = new Tickets();
            System.out.println("Creating new Tickets instance");
        }
        System.out.println("Current customer name: " + instance.customerName);
        return instance;
    }

    /**
     * Constructs a {@code Tickets} object with the specified session details, seat numbers,
     * customer name, and cost breakdown.
     *
     * @param sessionId            the session ID for the ticket
     * @param seatNumbers          the list of seat numbers
     * @param customerName         the name of the customer
     * @param totalSeatCost        the total cost of seats
     * @param totalProductCost     the total cost of additional products
     * @param totalTax             the total tax applied
     * @param totalCost            the total overall cost
     * @param discountedSeatNumber the number of seats with discounts applied
     */
    public Tickets(int sessionId, List<String> seatNumbers, String customerName, 
                  double totalSeatCost, double totalProductCost, 
                  double totalTax, double totalCost, int discountedSeatNumber) {
        this.sessionId = sessionId;
        this.seatNumbers = seatNumbers;
        this.customerName = customerName;
        this.totalSeatCost = totalSeatCost;
        this.totalProductCost = totalProductCost;
        this.totalTax = totalTax;
        this.totalCost = totalCost;
        this.discountedSeatNumber = discountedSeatNumber;
    }

    /**
     * Returns the number of discounted seats.
     *
     * @return the number of discounted seats
     */
    public int getDiscountedSeatNumber() {
        return discountedSeatNumber;
    }   

    /**
     * Sets the number of discounted seats.
     *
     * @param discountedSeatNumber the number of discounted seats
     */
    public void setDiscountedSeatNumber(int discountedSeatNumber) {
        this.discountedSeatNumber = discountedSeatNumber;
    }


    /**
     * Returns the ticket ID.
     *
     * @return the ticket ID
     */
    public int getTicketId() {
        return ticketId;
    }

    /**
     * Sets the ticket ID.
     *
     * @param ticketId the ticket ID to set
     */
    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    /**
     * Returns the session ID.
     *
     * @return the session ID
     */
    public int getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session ID.
     *
     * @param sessionId the session ID to set
     */
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Returns the list of seat numbers.
     *
     * @return the list of seat numbers
     */
    public List<String> getSeatNumbers() 
    {
        return seatNumbers;
    }

    /**
     * Sets the list of seat numbers.
     *
     * @param seatNumbers the list of seat numbers to set
     */
    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    /**
     * Returns the customer name.
     *
     * @return the customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customer name.
     * Validates that the name is not null or empty.
     *
     * @param customerName the customer name to set
     * @throws IllegalArgumentException if the customer name is null or empty
     */
    public void setCustomerName(String customerName) 
    {
        if (customerName != null && !customerName.trim().isEmpty()) 
        {
            this.customerName = customerName.trim();
            System.out.println("Setting customer name to: " + this.customerName);
        } 
        else 
        {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
    }

    /**
     * Returns the total cost of seats.
     *
     * @return the total seat cost
     */
    public double getTotalSeatCost() {
        return totalSeatCost;
    }

    /**
     * Sets the total cost of seats.
     *
     * @param totalSeatCost the total seat cost to set
     */
    public void setTotalSeatCost(double totalSeatCost) {
        this.totalSeatCost = totalSeatCost;
    }

    /**
     * Returns the total cost of products.
     *
     * @return the total product cost
     */
    public double getTotalProductCost() {
        return totalProductCost;
    }

    /**
     * Sets the total cost of products.
     *
     * @param totalProductCost the total product cost to set
     */
    public void setTotalProductCost(double totalProductCost) {
        this.totalProductCost = totalProductCost;
    }

    /**
     * Returns the total tax applied.
     *
     * @return the total tax
     */
    public double getTotalTax() {
        return totalTax;
    }

    /**
     * Sets the total tax applied.
     *
     * @param totalTax the total tax to set
     */
    public void setTotalTax(double totalTax) {
        this.totalTax = totalTax;
    }

    /**
     * Returns the total overall cost.
     *
     * @return the total cost
     */
    public double getTotalCost() {
        return totalCost;
    }

    /**
     * Sets the total overall cost.
     *
     * @param totalCost the total cost to set
     */
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * Returns a string representation of the ticket.
     *
     * @return a string containing the ticket details
     */
    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", sessionId=" + sessionId +
                ", seatNumbers='" + seatNumbers + '\'' +
                ", customerName='" + customerName + '\'' +
                ", totalSeatCost=" + totalSeatCost +
                ", totalProductCost=" + totalProductCost +
                ", totalTax=" + totalTax +
                ", totalCost=" + totalCost +
                '}';
    }

    /**
     * Calculates the total cost by summing the seat cost, product cost, and tax.
     */
    public void calculateTotalCost() {
        this.totalCost = this.totalSeatCost + this.totalProductCost + this.totalTax;
 
    }

    /**
     * Clears all ticket details, resetting them to default values.
     */
    public void clear() 
    {
        this.sessionId = 0;
        this.seatNumbers.clear();
        this.customerName = "";
        this.totalSeatCost = 0;
        this.totalProductCost = 0;
        this.totalTax = 0;
        this.totalCost = 0;
        this.discountedSeatNumber = 0;
        this.status = "ACTIVE";
    }
}