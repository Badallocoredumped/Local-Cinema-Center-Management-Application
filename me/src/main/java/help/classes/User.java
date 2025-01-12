package help.classes;
public class User 
{
    private String firstName;
    private String lastName;
    private String username;
    private String role;
    private String password; // Add this field
    private int userId;

    public User(String firstName, String lastName, String username, String role) 
    {
        this(firstName, lastName, username, role, "", 0);
    }

    public User(String firstName, String lastName, String username, String role, String password, int userId) 
    {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.role = role;
        this.password = password;
    }

    // Add this getter
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    // Getters and setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}