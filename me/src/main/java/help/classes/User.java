package help.classes;
public class User 
{
    private String firstName;
    private String lastName;
    private String username;
    private String role;
    private String password; // Add this field
    private int userId;

    /**
     * Constructs a {@code User} object with the specified first name, last name, username, and role.
     * This constructor sets a default password and user ID.
     *
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param username  the username of the user
     * @param role      the role of the user
     */
    public User(String firstName, String lastName, String username, String role) 
    {
        this(firstName, lastName, username, role, "", 0);
    }

    /**
     * Constructs a {@code User} object with the specified first name, last name, username, role,
     * password, and user ID.
     *
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param username  the username of the user
     * @param role      the role of the user
     * @param password  the password of the user
     * @param userId    the user ID
     */
    public User(String firstName, String lastName, String username, String role, String password, int userId) 
    {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.role = role;
        this.password = password;
    }

    /**
     * Returns the password of the user.
     *
     * @return the password of the user
     */
    public String getPassword() { return password; }


    /**
     * Sets the password of the user.
     *
     * @param password the password to set
     */
    public void setPassword(String password) { this.password = password; }
    
    /**
     * Returns the user ID.
     *
     * @return the user ID
     */
    public int getUserId() { return userId; }

    /**
     * Sets the user ID.
     *
     * @param userId the user ID to set
     */
    public void setUserId(int userId) { this.userId = userId; }

    /**
     * Returns the first name of the user.
     *
     * @return the first name of the user
     */
    public String getFirstName() { return firstName; }

    /**
     * Sets the first name of the user.
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /**
     * Returns the last name of the user.
     *
     * @return the last name of the user
     */
    public String getLastName() { return lastName; }

    /**
     * Sets the last name of the user.
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * Returns the username of the user.
     *
     * @return the username of the user
     */
    public String getUsername() { return username; }

    /**
     * Sets the username of the user.
     *
     * @param username the username to set
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Returns the role of the user.
     *
     * @return the role of the user
     */
    public String getRole() { return role; }

    /**
     * Sets the role of the user.
     *
     * @param role the role to set
     */
    public void setRole(String role) { this.role = role; }
}