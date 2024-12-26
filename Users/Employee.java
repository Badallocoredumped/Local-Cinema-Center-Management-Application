
import java.time.LocalDate;
import java.util.Scanner;



/**
 * The Employee class serves as an base for all employee types in the Cinama system.
 * 
 * This class defines common properties, behaviors, and operations that all employees share.
 * RegularEmployee and Manager classes inherit from the Employee superclass and extends this classes methods
 * 
 */
public class Employee extends Person
{
    protected String Username;
    protected String role;
    protected String phone;
    protected LocalDate DOB;
    protected LocalDate DOS;
    protected String Email;
    protected String Password;

    public static Scanner input = new Scanner(System.in);

    /**
     * Constructs a new Employee object.
     * 
     * @param ID Unique identifier for the employee.
     * @param Username Username for employee login.
     * @param Role Role of the employee in the organization.
     * @param Phone Phone number of the employee.
     * @param DOB Date of birth of the employee.
     * @param DOS Employment start date.
     * @param Email Email address of the employee.
     * @param Password Password for employee login.
     */
    public Employee(int ID, String Username,String Role,String Name,String Surname,String Phone,LocalDate DOB,LocalDate DOS,String Email,String Password)
    {
        super(Name, Surname, ID);
        this.Username = Username;
        this.role = Role;
        this.phone = Phone;
        this.DOB = DOB;
        this.DOS = DOS;
        this.Email = Email;
        this.Password = Password;
    }
    /**  
     * Getter and Setter functions for the Employee class
     * 
     */
    public String getUsername() {return Username;}
    public String getRole() {return role;}
    public LocalDate getBirthday() {return DOB;}
    public LocalDate getEmploymentday() {return DOS;}
    public String getPhone() {return phone;}
    public String getPassword() {return Password;}
    public String getEmail() {return Email;}
    public void setUsername(String Username) {this.Username = Username;}
    public void setRole(String roletochange) {this.role = roletochange;}
    public void setPhone(String phone) { this.phone = phone;}
    public void setEmail(String email) { this.Email = email;}
    public void setPassword(String Password) { this.Password = Password;}
    public void setBirthday(LocalDate birthdaytochange){this.DOB = birthdaytochange;}
    public void setEmploymentDay(LocalDate employmentdatetochange){this.DOS = employmentdatetochange;}

    public void Menu()
    {}

    public void Ccleaner()
    {
        try 
        {
            new ProcessBuilder("cmd","/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) 
        {
            System.err.println("Error Code #Clear");
        }
    }
}