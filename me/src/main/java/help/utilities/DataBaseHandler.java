package me.src.main.java.help.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHandler
{
    private static final String URL = "jdbc:mysql://localhost:3306/cinemacenter";
    private static final String USER = "root";
    private static final String PASSWORD = "Admin_123";

    public static Connection getConnection() throws Exception
    {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static String authenticate(String username, String password)
    {
        String role = null;
        try (Connection conn = getConnection())
        {
            String query = "SELECT role FROM Users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                role = rs.getString("role");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return role;
    }

    public static List<String> searchByGenre(String genre)
    {
        List<String> movies = new ArrayList<>();
        try (Connection conn = getConnection())
        {
            String query = "SELECT title FROM Movies WHERE genre LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + genre + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                movies.add(rs.getString("title"));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return movies;
    }

    public static List<String> searchByPartialName(String partialName)
    {
        List<String> movies = new ArrayList<>();
        try (Connection conn = getConnection())
        {
            String query = "SELECT title FROM Movies WHERE title LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + partialName + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                movies.add(rs.getString("title"));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return movies;
    }

    public static List<String> searchByFullName(String fullName)
    {
        List<String> movies = new ArrayList<>();
        try (Connection conn = getConnection())
        {
            String query = "SELECT title FROM Movies WHERE title = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, fullName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                movies.add(rs.getString("title"));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return movies;
    }

    public static List<Integer> getAvailableSeats(int sessionId)
    {
        List<Integer> availableSeats = new ArrayList<>();
        try (Connection conn = getConnection())
        {
            String query = "SELECT seat_number FROM Tickets WHERE session_id = ? AND customer_name IS NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                availableSeats.add(rs.getInt("seat_number"));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return availableSeats;
    }

    public static boolean selectSeat(int sessionId, int seatNumber)
    {
        try (Connection conn = getConnection())
        {
            String query = "UPDATE Tickets SET customer_name = ? WHERE session_id = ? AND seat_number = ? AND customer_name IS NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "reserved"); // Placeholder for customer name
            stmt.setInt(2, sessionId);
            stmt.setInt(3, seatNumber);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
    public static void updateTicketSales(int ticketId, String customerName)
    {
        try (Connection conn = getConnection())
        {
            String query = "UPDATE Tickets SET customer_name = ? WHERE ticket_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, customerName);
            stmt.setInt(2, ticketId);
            stmt.executeUpdate();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void updateProductInventory(int productId, int quantity)
    {
        try (Connection conn = getConnection())
        {
            String query = "UPDATE Products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}