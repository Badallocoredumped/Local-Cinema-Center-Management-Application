
package help.classes;

import java.util.ArrayList;
import java.util.List;

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

    // Default constructor
    public Tickets() 
    {
        this.customerName = "";
        this.seatNumbers = new ArrayList<>();
    }

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

    // Parameterized constructor
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
    public int getDiscountedSeatNumber() {
        return discountedSeatNumber;
    }   
    public void setDiscountedSeatNumber(int discountedSeatNumber) {
        this.discountedSeatNumber = discountedSeatNumber;
    }


    // Getters and Setters
    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public List<String> getSeatNumbers() 
    {
        return seatNumbers;
    }

    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public String getCustomerName() {
        return customerName;
    }

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
    public double getTotalSeatCost() {
        return totalSeatCost;
    }

    public void setTotalSeatCost(double totalSeatCost) {
        this.totalSeatCost = totalSeatCost;
    }

    public double getTotalProductCost() {
        return totalProductCost;
    }

    public void setTotalProductCost(double totalProductCost) {
        this.totalProductCost = totalProductCost;
    }

    public double getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(double totalTax) {
        this.totalTax = totalTax;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    // toString method for printing ticket details
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

    // Helper method to calculate total cost
    public void calculateTotalCost() {
        this.totalCost = this.totalSeatCost + this.totalProductCost + this.totalTax;
 
    }

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
    }
}