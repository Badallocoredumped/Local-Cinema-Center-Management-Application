import java.time.LocalDate;
import java.util.List;
import utilities.DataBaseHandler; 

public class Cashier extends Employee
{
    public Cashier(int ID, String Username, String Role, String Name, String Surname, String Phone, LocalDate DOB, LocalDate DOS, String Email, String Password, Boolean DEFAULT_PASSWORD)
    {
        super(ID, Username, Role, Name, Surname, Phone, DOB, DOS, Email, Password);
    }

    public List<String> searchByGenre(String genre)
    {
        return DataBaseHandler.searchByGenre(genre);
    }

    public List<String> searchByPartialName(String partialName)
    {
        return DataBaseHandler.searchByPartialName(partialName);
    }

    public List<String> searchByFullName(String fullName)
    {
        return DataBaseHandler.searchByFullName(fullName);
    }

    public List<Integer> getAvailableSeats(int sessionId)
    {
        return DataBaseHandler.getAvailableSeats(sessionId);
    }

    public void updateTicketSales(int ticketId, String customerName)
    {
        DataBaseHandler.updateTicketSales(ticketId, customerName);
    }

    public void updateProductInventory(int productId, int quantity)
    {
        DataBaseHandler.updateProductInventory(productId, quantity);
    }
}