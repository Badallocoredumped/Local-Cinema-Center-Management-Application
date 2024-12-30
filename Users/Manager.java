import java.lang.reflect.InvocationHandler;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;

import utilities.DataBaseHandler;
import utilities.InputHandler;
import utilities.InputUtil;


public class Manager extends Employee 
{
    /*
    DataBaseHandler dbHandler = new DataBaseHandler();
    InputHandler inHandle = new InputHandler();     

    public Manager(int ID, String Username,String Role,String Name,String Surname,String Phone,LocalDate DOB,LocalDate DOS,String Email,String Password, Boolean DEFAULT_PASSWORD)
    {
        super(ID,  Username, Role, Name, Surname, Phone, DOB, DOS, Email, Password);
    }

    private static Scanner scanner = new Scanner(System.in);

    @Override
    public void Menu(){
        Scanner scanner = InputUtil.scanner;
        String MIString;
        char MInput = '0';
        while (MInput != '5'){
            System.out.println("Welcome to Manager Menu");
            //System.out.println("Good day, " getRole()+ " " + getName()+ " " +getSurname()+ "!");
            System.out.println("1. View and Update Inventory");
            System.out.println("2. Manage Personnel Records");
            System.out.println("3. Update Prices and Discounts");
            System.out.println("4. View Revenue and Taxes");
            System.out.println("5. Logout");
            System.out.println("What would you like to select. Pick from option 1-5: ");

            MIString = scanner.nextLine();
            
            if (MIString.isEmpty() || MIString.length() > 1){
                //clearConsole();
                System.out.println("Invalid input. Enter a number between 1 and 5: ");
            }

            switch(MInput){
                case 1:
                    clearConsole();
                    manageInventory();
                    break;
                case 2:
                    clearConsole();
                    managePersonnel();
                    break;
                case 3:
                    clearConsole();
                    updatePriceandDiscount();
                    break;
                case 4:
                    clearConsole();
                    viewRevenueandTax();
                    break;
                case 5:
                    clearConsole();
                    System.out.println("Logging you out.");
                    return;
            }
        }
        scanner.close();
    }

    protected void manageInventory(){
        
    }


    protected void managePersonnel(){
        
        try(){

            System.out.println("Choose one option: \n1. Hire Employee \n2. Fire Employee ");
            int choice = inHandle.getIntInput();

            if(choice == 1){
                hireEmployee();
            } else if(choice == 2){
                fireEmployee();
            }

        } catch (Exception e) {
            System.err.println("Error Code");
        }
    }

    protected void hireEmployees() {
        String name = inHandle.getStringInput("Enter Employee's name: ");
        String surname = inHandle.getStringInput("Enter Employee's surname: ");
        String role = inHandle.getStringInput("Enter Employee's role: ");
        String username = inHandle.getStringInput("Enter Employee's username: ");
        String password = inHandle.getStringInput("Enter Employee's password: ");
        LocalDate dob = inHandle.getDateInput("Enter Employee's date of birth (YYYY-MM-DD): ");
        LocalDate dos = inHandle.getDateInput("Enter Employee's date of start (YYYY-MM-DD): ");

        dbHandler.hireEmployee(username, name, surname, role, dob, dos);
        System.out.println("Employee hired successfully!");
    }

    protected void fireEmployees() {
        System.out.println("Enter the employee's username to fire: ");
        String username = inHandler.getStringInput(" ");

        if (username.equals(getUsername())) {
            System.out.println("Nice try. You can't fire yourself.");
            return;
        }

        boolean victimFound = dbHandler.fireEmployee(username);
        if (victimFound) {
            System.out.println("Employee fired successfully!");
        } else {
            System.out.println("Employee not found.");
        }
    }



    protected void updatePriceandDiscount(){
        System.out.println("Welcome to System!");
    }
    protected void viewRevenueandTax(){
        System.out.println("Welcome to System!");
    }
}
