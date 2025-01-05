import java.time.LocalDate;
import java.util.Scanner;

public class Manager extends Employee {

    public Manager(int ID, String Username, String Role, String Name, String Surname, String Phone, LocalDate DOB, LocalDate DOS, String Email, String Password) {
        super(ID, Username, Role, Name, Surname, Phone, DOB, DOS, Email, Password);
    }

    Scanner input = new Scanner(System.in);
–
    public void ManagerMenu() {
        String menuString;
        char menuChoice = '0';

        while (menuChoice != '5') {
            System.out.println("Welcome to the Manager Menu");
            System.out.println("1. View and Update Inventory");
            System.out.println("2. Manage Personnel Records");
            System.out.println("3. Update Prices and Discounts");
            System.out.println("4. View Revenue and Taxes");
            System.out.println("5. Logout");
            System.out.print("Please select an option: ");
            menuString = input.nextLine();

            if (menuString.isEmpty() || menuString.length() > 1) {
                Ccleaner();
                System.out.println("Invalid input. Please enter a number between 1 and 5!");
                continue;
            }

            menuChoice = menuString.charAt(0);

            switch (menuChoice) {
                case '1' -> {
                    Ccleaner();
                    manageInventory();
                }
                case '2' -> {
                    Ccleaner();
                    managePersonnelRecords();
                }
                case '3' -> {
                    Ccleaner();
                    updatePricesAndDiscounts();
                }
                case '4' -> {
                    Ccleaner();
                    viewRevenueAndTaxes();
                }
                case '5' -> {
                    Ccleaner();
                    System.out.println("Logging you out.");
                }
                default -> {
                    Ccleaner();
                    System.out.println("Invalid choice. Please select a valid option.");
                }
            }
        }
    }

    private void manageInventory() {
        System.out.println("View and Update Inventory:");
        //placeholder for inventory from db 
        System.out.println("0. Return to Menu");
        System.out.println("1. Beverages - Stock: 100");
        System.out.println("2. Biscuits - Stock: 100");
        System.out.println("3. Toys - Stock: 40");
        

        System.out.print("Select a product to update stock: ");
        String choice = input.nextLine();

        if (choice.equals("0")) return;

        try {
            int productChoice = Integer.parseInt(choice);
            if (productChoice >= 1 && productChoice <= 3) {
                System.out.print("Enter the additional quantity to add: ");
                int additionalStock = Integer.parseInt(input.nextLine());
                //placeholder for updating stock in db
                System.out.println("Stock updated successfully.");
            } else {
                System.out.println("Invalid product choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private void managePersonnelRecords() {
        System.out.println("View and Edit Personnel Records:");
        //placeholder for db
        System.out.println("0. Return to Menu");
        System.out.println("1. John Doe - Role: Cashier");
        System.out.println("2. Jane Smith - Role: Manager");

        System.out.print("Select a personnel record to edit: ");
        String choice = input.nextLine();

        if (choice.equals("0")) return;

        try {
            int personnelChoice = Integer.parseInt(choice);
            if (personnelChoice == 1 || personnelChoice == 2) {
                System.out.println("Edit details for personnel:");
                System.out.print("Enter first name: ");
                String firstName = input.nextLine();
                System.out.print("Enter last name: ");
                String lastName = input.nextLine();
                System.out.print("Enter username: ");
                String username = input.nextLine();
                System.out.print("Enter new password: ");
                String password = input.nextLine();
                System.out.print("Enter new role (cashier/manager): ");
                String role = input.nextLine();
                //placeholder for db
                System.out.println("Personnel record updated successfully.");
            } else {
                System.out.println("Invalid personnel choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private void updatePricesAndDiscounts() {
        System.out.println("Update Prices and Discounts:");
        System.out.println("0. Return to Menu");
        System.out.println("1. Cinema Ticket Price");
        System.out.println("2. Product Prices");
        System.out.println("3. Age-Based Discount Rate");

        System.out.print("Select an option to update: ");
        String choice = input.nextLine();

        if (choice.equals("0")) return;

        try {
            int updateChoice = Integer.parseInt(choice);
            switch (updateChoice) {
                case 1 -> {
                    System.out.print("Enter new cinema ticket price: ");
                    double ticketPrice = Double.parseDouble(input.nextLine());
                    //placeholder for db
                    System.out.println("Cinema ticket price updated successfully.");
                }
                case 2 -> {
                    System.out.print("Enter product ID to update price: ");
                    int productId = Integer.parseInt(input.nextLine());
                    System.out.print("Enter new price for the product: ");
                    double productPrice = Double.parseDouble(input.nextLine());
                    //placeholder for db
                    System.out.println("Product price updated successfully.");
                }
                case 3 -> {
                    System.out.print("Enter new age-based discount rate (e.g., 0.1 for 10%): ");
                    double discountRate = Double.parseDouble(input.nextLine());
                    //placeholder for db
                    System.out.println("Discount rate updated successfully.");
                }
                default -> System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    private void viewRevenueAndTaxes() {
        System.out.println("View Revenue and Taxes:");
        //placeholder for db
        System.out.println("Total Revenue: ");
        System.out.println("Total Tax: ");
    }
}
