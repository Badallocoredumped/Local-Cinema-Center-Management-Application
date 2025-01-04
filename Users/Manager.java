import java.lang.reflect.InvocationHandler;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;
<<<<<<< Updated upstream

import utilities.DataBaseHandler;
import utilities.InputHandler;
import utilities.InputUtil;

=======
>>>>>>> Stashed changes

public class Manager extends Employee 
{
    /*
    DataBaseHandler dbHandler = new DataBaseHandler();
    InputHandler inHandle = new InputHandler();     

    public Manager(int ID, String Username,String Role,String Name,String Surname,String Phone,LocalDate DOB,LocalDate DOS,String Email,String Password, Boolean DEFAULT_PASSWORD)
    {
        super(ID,  Username, Role, Name, Surname, Phone, DOB, DOS, Email, Password);
    }

    Scanner input = new Scanner(System.in);

<<<<<<< Updated upstream
    @Override
    public void Menu(){
        Scanner scanner = InputUtil.scanner;
        String MIString;
        char MInput = '0';
        while (MInput != '5'){
            System.out.println("Welcome to Manager Menu");
            //System.out.println("Good day, " getRole()+ " " + getName()+ " " +getSurname()+ "!");
=======
    
    public void ManagerMenu(){

        String menuString;
        char menuChoice = '0'; 

        while (menuChoice!= '5'){
            System.out.println("Welcome to Manager Menu");
>>>>>>> Stashed changes
            System.out.println("1. View and Update Inventory");
            System.out.println("2. Manage Personnel Records");
            System.out.println("3. Update Prices and Discounts");
            System.out.println("4. View Revenue and Taxes");
            System.out.println("5. Logout");
<<<<<<< Updated upstream
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
=======
            System.out.println("Please select an option: ");

            menuString = input.nextLine();

            switch(menuChoice){
                case '1' -> {
                    Ccleaner();
                    manageInventory();
                }  
                case '2' -> {
                    Ccleaner();
                    managePersonnel();
                }
                case '3' -> {
                    Ccleaner();
                    updatePriceandDiscount();
                }
                case '4' -> {
                    Ccleaner();
                    viewRevenueandTax();
                }
                case '5' -> {
                    Ccleaner();
                    System.out.println("Logging you out.");
                }
                default -> {
                    System.out.println("Invalid choice. Re-enter choice:");
                }
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
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
=======
    private void manageInventory(){
        System.out.println("View and Update Inventory: ");
        System.out.println("1. Bevergaes - Stock: 100");
        System.out.println("2. Biscuits - Stock: 200");
        System.out.println("3. Toys - Stcok: 50");
        System.out.println("Select a product to update stock from 1-3, or 0 to return: ");
        
        String choice = input.nextLine();
        if(choice.equals("0")) return; 
        try {
            int productChoice = Integer.parseInt(choice);
            if (productChoice >= 1 && productChoice <= 3){
                System.out.print("Enter quantity to add to stock: ");
                int additionalStock = Integer.parseInt(input.nextLine());
                System.out.print("Stock update successfully.");
            } else {
                System.out.println("Invalid product choice.");
            }
        
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private void managePersonnel(){
        System.out.println("View and Edit Personnel Records:");
        System.out.println("1. John Doe - Role: Cashier");
        System.out.println("2. Jane Smith - Role: Manager");
        System.out.println("Select a personal record to edit or 0 to return: ");

        String choice = input.nextLine(); 
        if(choice.equals("0")) return;
        
        try {
            int personnelChoice = Integer.parseInt(choice);
            if(personnelChoice ==1 || personnelChoice == 2){
                System.out.println("Editing details for selected personnel.");
                System.out.println("Enter new first name: ");
                String firstName = input.nextLine();
                System.out.println("Enter new last name: ");
                String LastName = input.nextLine();
                System.out.println("Enter new username: ");
                String username = input.nextLine();
                System.out.println("Enter new password:");
                String password  = input.nextLine();
                System.out.println("Enter new role");
                String role = input.nextLine();
                System.out.println("personnel record updated successfully.");
            } else {
                System.out.println("Invalid personnel choice.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
>>>>>>> Stashed changes
        }
    }


<<<<<<< Updated upstream

    protected void updatePriceandDiscount(){
        System.out.println("Welcome to System!");
    }
    protected void viewRevenueandTax(){
        System.out.println("Welcome to System!");
=======
    private void updatePriceandDiscount(){
        System.out.println("Update Prices and Discounts:");
        System.out.println("1. Cinema Ticket Price");
        System.out.println("2. Product Prices");
        System.out.println("3. Age-Based Discount Rate");
        System.out.println("Select an option to update or 0 to return: ");

        String choice = input.nextLine(); 
        if(choice.equals("0")) return;
        
        try {
            int updateChoice = Integer.parseInt(choice);
            switch(updateChoice){
                case 1 -> {
                    System.out.println("Enter new cinema ticket price: ");
                    double ticketPrice = Double.parseDouble(input.nextLine());
                    System.out.println("Cinema ticket price updated successfully.");
                }
                case 2 -> {
                    System.out.println("Enter product ID to update price: ");
                    double productID = Double.parseDouble(input.nextLine());
                    System.out.println("Enter new price to update: ");
                    double productPrice = Double.parseDouble(input.nextLine());
                    System.out.println("Product price updated successfully.");
                }
                case 3 -> {
                    System.out.println("Enter new age-based discount rate(hint: 0.2 for 20%): ");
                    double discountRate = Double.parseDouble(input.nextLine());
                    System.out.println("Discount rate updated successfully.");
                }
                default -> System.out.println("nvalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }        
    }

    private void viewRevenueandTax(){
        System.out.println("View Revenue and Taxes: "); 
        //db fetch rev and tax 
        System.out.println("Total Revenue: "); 
        System.out.println("Total Tax: "); 
>>>>>>> Stashed changes
    }
}
