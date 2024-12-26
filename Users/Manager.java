import java.time.LocalDate;

public class Manager extends Employee 
{
    public Manager(int ID, String Username,String Role,String Name,String Surname,String Phone,LocalDate DOB,LocalDate DOS,String Email,String Password, Boolean DEFAULT_PASSWORD)
    {
        super(ID,  Username, Role, Name, Surname, Phone, DOB, DOS, Email, Password);
    }
}
