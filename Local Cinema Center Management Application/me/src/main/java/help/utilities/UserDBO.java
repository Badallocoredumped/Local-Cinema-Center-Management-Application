package help.utilities;

import help.classes.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Base Object (DBO) class for managing User-related database operations.
 */
public class UserDBO {
    private static final String GET_USER = "SELECT * FROM Users WHERE username = ?";
    private static final String GET_USER_ID = "SELECT user_id FROM Users WHERE username = ?";
    private static final String GET_ALL_USERS = "SELECT * FROM Users";
    private static final String INSERT_USER = "INSERT INTO Users (first_name, last_name, username, password, role) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE Users SET first_name=?, last_name=?, username=?, password=?, role=? WHERE user_id=?";
    private static final String DELETE_USER = "DELETE FROM Users WHERE username = ?";


    /**
     * Retrieves a user from the database by their username.
     * <p>
     * This method queries the database for a user with the specified username. If the user is found,
     * a `User` object is created and populated with the user's details from the database.
     * </p>
     *
     * @param username The username of the user to retrieve.
     * @return The `User` object corresponding to the specified username, or `null` if no user is found.
     */
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


    /**
     * Retrieves the user ID from the database by the specified username.
     * <p>
     * This method queries the database for the user ID associated with the given username. 
     * If a matching user is found, the user ID is returned. Otherwise, it returns -1 to indicate 
     * that no user with the specified username was found.
     * </p>
     *
     * @param username The username of the user whose ID is to be retrieved.
     * @return The user ID of the specified username, or -1 if the user is not found.
     */
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

    /**
     * Retrieves all users from the database.
     * <p>
     * This method executes a query to retrieve all users from the database, including their first name, 
     * last name, username, role, password, and user ID. The results are returned as a list of {@link User} objects.
     * </p>
     *
     * @return A list of {@link User} objects representing all users in the database.
     */
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

    /**
     * Inserts a new user into the database.
     * <p>
     * This method inserts a new user into the database with the provided first name, last name, username, password,
     * and role. The password will be stored as provided, so it should be hashed before calling this method.
     * </p>
     *
     * @param user The {@link User} object containing the user details.
     * @param password The plain-text password for the user.
     * @return {@code true} if the user was successfully inserted into the database, {@code false} otherwise.
     */
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

    /**
     * Updates the details of an existing user in the database.
     * <p>
     * This method updates the first name, last name, username, password, and role of a user in the database based on
     * the provided {@link User} object. The user is identified by their current username, and their user ID is fetched
     * before updating the user details. If the username has changed, the {@code oldString} parameter is used to handle
     * the username change.
     * </p>
     *
     * @param user The {@link User} object containing the updated user details.
     * @param oldString The old username (if the username is being changed), or an empty string if the username is not changed.
     * @return {@code true} if the user details were successfully updated, {@code false} otherwise.
     */
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

    /**
     * Deletes a user from the database.
     * <p>
     * This method deletes a user identified by their username from the database. It performs the deletion by executing
     * the {@code DELETE} SQL statement.
     * </p>
     *
     * @param username The username of the user to be deleted.
     * @return {@code true} if the user was successfully deleted, {@code false} otherwise.
     */
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