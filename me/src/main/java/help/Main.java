package me.src.main.java.help;

import me.src.main.java.help.utilities.DataBaseHandler;

public class Main {
    public static void main(String[] args) 
    {
        // Test database connection
        try {
            if (DataBaseHandler.getConnection() != null) 
            {
                System.out.println("Database connection successful!");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }

        // Test authentication
        String role = DataBaseHandler.authenticate("admin", "password123");
        System.out.println("Authenticated role: " + role);

        // Test search by genre
        System.out.println("Movies in genre 'Action': " + DataBaseHandler.searchByGenre("Action"));

        // Test search by partial name
        System.out.println("Movies with 'Avengers' in title: " + DataBaseHandler.searchByPartialName("Avengers"));

        // Test search by full name
        System.out.println("Movies with title 'Inception': " + DataBaseHandler.searchByFullName("Inception"));

        // Test get available seats
        System.out.println("Available seats for session 1: " + DataBaseHandler.getAvailableSeats(1));

        // Test select seat
        boolean seatSelected = DataBaseHandler.selectSeat(1, 5);
        System.out.println("Seat 5 selected for session 1: " + seatSelected);

        // Test update ticket sales
        DataBaseHandler.updateTicketSales(1, "John Doe");
        System.out.println("Ticket sales updated for ticket 1");

        // Test update product inventory
        DataBaseHandler.updateProductInventory(1, 2);
        System.out.println("Product inventory updated for product 1");
    }
}