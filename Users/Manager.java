import java.time.LocalDate;
import java.util.*;

public class Manager extends Employee 
{
    public Manager(int ID, String Username,String Role,String Name,String Surname,String Phone,LocalDate DOB,LocalDate DOS,String Email,String Password, Boolean DEFAULT_PASSWORD)
    {
        super(ID,  Username, Role, Name, Surname, Phone, DOB, DOS, Email, Password);
    }

    private static Scanner scanner = new Scanner(System.in);

    @Override
    public void Menu(){
        while (true){
            System.out.println("Manager Menu");
            System.out.println("1. View and Update Inventory");
            System.out.println("2. Manage Personnel Records");
            System.out.println("3. Update Prices and Discounts");
            System.out.println("4. View Revenue and Taxes");
            System.out.println("5. Logout");
            System.out.println("WHat would you like to edit?");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch(choice){
                case 1:
                    manageInventory();
                    break;
                case 2:
                    managePersonnel();
                    break;
                case 3:
                    updatePriceandDiscount();
                    break;
                case 4:
                    viewRevenueandTax();
                    break;
                case 5:
                    System.out.println("Logging you out.");
                    return;
                default:
                    System.out.println("Invalid choice. Re-enter choice:");
            }
        }
    }

    private void manageInventory(){}
    private void managePersonnel(){}
    private void updatePriceandDiscount(){}
    private void viewRevenueandTax(){}
}
