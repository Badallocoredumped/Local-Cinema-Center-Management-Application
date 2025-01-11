package help.utilities;

import help.classes.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDBO {
    private static final String GET_USER = "SELECT * FROM Users WHERE username = ?";
    private static final String GET_USER_ID = "SELECT user_id FROM Users WHERE username = ?";
    private static final String GET_ALL_USERS = "SELECT * FROM Users";
    private static final String INSERT_USER = "INSERT INTO Users (first_name, last_name, username, password, role) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE Users SET first_name=?, last_name=?, username=?, password=?, role=? WHERE user_id=?";
    private static final String DELETE_USER = "DELETE FROM Users WHERE username = ?";

    public User getUser(String username) 
    {
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_USER)) 
             {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("password"),
                        rs.getInt("user_id")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public int getUserIdByUsername(String username) {
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_USER_ID)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_USERS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("password"),
                    rs.getInt("user_id")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean insertUser(User user, String password) {
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USER)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, password);
            stmt.setString(5, user.getRole());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(User user,String oldString) 
    {
        System.out.println("Updating user: " + user.getUsername());
        System.out.println("First Name: " + user.getFirstName());
        System.out.println("Last Name: " + user.getLastName());
        System.out.println("Password: " + user.getPassword());
        System.out.println("Role: " + user.getRole());


        // First get the user ID
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement idStmt = conn.prepareStatement(GET_USER_ID)) {
            
            System.out.println("User: " + user.getUsername());
            idStmt.setString(1, user.getUsername());
            try (ResultSet rs = idStmt.executeQuery()) {
                if (rs.next()) {
                    user.setUserId(rs.getInt("user_id"));
                    System.out.println("User ID: " + user.getUserId());
                    // Now perform the update with the user ID
                    PreparedStatement updateStmt = conn.prepareStatement(UPDATE_USER);
                    updateStmt.setString(1, user.getFirstName());
                    updateStmt.setString(2, user.getLastName());
                    if(!oldString.equals(""))
                    {
                        user.setUsername(oldString);
                    }
                    updateStmt.setString(3, user.getUsername());
                    updateStmt.setString(4, user.getPassword());
                    updateStmt.setString(5, user.getRole());
                    updateStmt.setInt(6, user.getUserId());
                    
                    return updateStmt.executeUpdate() > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser(String username) {
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USER)) {
            stmt.setString(1, username);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}