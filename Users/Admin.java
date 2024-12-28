import java.time.LocalDate;
import java.util.Scanner;

public class Admin extends Employee
{
    public Admin(int ID, String Username,String Role,String Name,String Surname,String Phone,LocalDate DOB,LocalDate DOS,String Email,String Password, Boolean DEFAULT_PASSWORD)
    {
        super(ID,  Username, Role, Name, Surname, Phone, DOB, DOS, Email, Password);
    }


    /*Admin Menu
     * 
     * View movies
     * Manage movies
     *      Add movie
     *      Update movies (Specific section or entire movie)
     * Manage schedule
     *      Add new schedule
     *      Update schedule
     * Manage refunds(tickets and/or products.)
    */

    Scanner input = new Scanner(System.in);

    public void AdminMenu()
    {
        String AMString;
        char AMInput = '0';

        while (AMInput != '0') 
        {
            System.out.println("Welcome to the Admin Menu");
            System.out.println("1. View Movies");
            System.out.println("2. Manage Movies");
            System.out.println("3. Manage Schedule");
            System.out.println("4. Manage Refunds");
            System.out.println("5. Logout");
            System.out.print("Please select an option: ");
            AMString = input.nextLine();

            if(AMString.isEmpty() || AMString.length() > 1)
            {
                Ccleaner();
                System.out.println("You entered an invalid input. Please enter a number between 1 and 5!!");
                System.out.println();
                continue;
            }

            AMInput = AMString.charAt(0);
            System.out.println();

            if (AMInput < '1' || AMInput > '5') 
            {
                Ccleaner();
                System.out.println("You entered an invalid input. Please enter a number between 1 and 5!!");
                System.out.println();
                continue;
            }
        }

        switch (AMInput) 
        {
            case '1'-> 
            {
                Ccleaner();
                ViewMovies();
                System.out.println("Enter anything to return");
                input.nextLine();
                Ccleaner();
            }

            case '2'-> 
            {
                Ccleaner();
                ManageMovies();
                System.out.println("Enter anything to return");
                input.nextLine();
                Ccleaner();
            }
            
            case '3'-> 
            {
                Ccleaner();
                ManageSchedule();
                System.out.println("Enter anything to return");
                input.nextLine();
                Ccleaner();
            }

            case '4'-> 
            {
                Ccleaner();
                ManageRefunds();
                System.out.println("Enter anything to return");
                input.nextLine();
                Ccleaner();
            }

            case '5'-> //Return to login 
            {
                Ccleaner();
                return;
            }
        }
    }

    public void ManageMovies()
    {
        String MMString;
        char MMInput = '0';

        while(MMInput != '4')
        {
        System.out.println("1. Add Movie");
        System.out.println("2. Update Movie");
        System.out.println("3. Delete Movie");
        System.out.println("4. Return to Main menu");
        System.out.println("Select an option: ");
        MMString = input.nextLine();

            if(MMString.isEmpty() || MMString.length() > 1)
            {
                Ccleaner();
                System.out.println("You entered an invalid input. Please enter a number between 1 and 5!!");
                System.out.println();
                continue;
            }

            MMInput = MMString.charAt(0);
            System.out.println();

            if (MMInput < '1' || MMInput > '4') 
            {
                Ccleaner();
                System.out.println("You entered an invalid input. Please enter a number between 1 and 5!!");
                System.out.println();
                continue;
            }

            switch (MMInput) 
            {
                case '1'-> 
                {
                    Ccleaner();
                    System.out.println("Enter anything to return");
                    input.nextLine();
                    Ccleaner();
                }

                case '2'-> 
                {
                    Ccleaner();
                    System.out.println("Enter anything to return");
                    input.nextLine();
                    Ccleaner();
                }
                
                case '3'-> 
                {
                    Ccleaner();
                    System.out.println("Enter anything to return");
                    input.nextLine();
                    Ccleaner();
                }

                case '4'-> //Return to menu 
                {
                    Ccleaner();
                    return;
                }
            }
        }
    }

    public void ManageSchedule()
    {
        String MSString;
        char MSInput = '0';

        while(MSInput != '4')
        {
            System.out.println("1. Add Schedule");
            System.out.println("2. Update Schdule");
            System.out.println("3. Delete Schedule");
            System.out.println("4. Return to Main menu");
            System.out.println("Select an option: ");
            MSString = input.nextLine();

            if(MSString.isEmpty() || MSString.length() > 1)
            {
                Ccleaner();
                System.out.println("You entered an invalid input. Please enter a number between 1 and 5!!");
                System.out.println();
                continue;
            }

            MSInput = MSString.charAt(0);
            System.out.println();

            if (MSInput < '1' || MSInput > '4') 
            {
                Ccleaner();
                System.out.println("You entered an invalid input. Please enter a number between 1 and 5!!");
                System.out.println();
                continue;
            }

            switch (MSInput) 
            {
                case '1'-> 
                {
                    Ccleaner();
                    System.out.println("Enter anything to return");
                    input.nextLine();
                    Ccleaner();
                }

                case '2'-> 
                {
                    Ccleaner();
                    System.out.println("Enter anything to return");
                    input.nextLine();
                    Ccleaner();
                }
                
                case '3'-> 
                {
                    Ccleaner();
                    System.out.println("Enter anything to return");
                    input.nextLine();
                    Ccleaner();
                }

                case '4'-> //Return to menu 
                {
                    Ccleaner();
                    return;
                }
            }
        }
    }

    public void ManageRefunds()
    {
        String MRString;
        char MRInput = '0';

        while (MRInput != '0') 
        {
            System.out.println("1. Refund ticket");
            System.out.println("2. Refund products");
            System.out.println("3. Refund ticket and products");
            System.out.println("4. Full refund");
            System.out.println("5. Return to Main menu");
            System.out.println("Select an option: ");
            MRString = input.nextLine();

            if(MRString.isEmpty() || MRString.length() > 1)
            {
                Ccleaner();
                System.out.println("You entered an invalid input. Please enter a number between 1 and 5!!");
                System.out.println();
                continue;
            }

            MRInput = MRString.charAt(0);
            System.out.println();

            if (MRInput < '1' || MRInput > '5') 
            {
                Ccleaner();
                System.out.println("You entered an invalid input. Please enter a number between 1 and 5!!");
                System.out.println();
                continue;
            }

            switch (MRInput) 
            {
                case '1'-> 
                {
                    Ccleaner();
                    System.out.println("Enter anything to return");
                    input.nextLine();
                    Ccleaner();
                }

                case '2'-> 
                {
                    Ccleaner();
                    System.out.println("Enter anything to return");
                    input.nextLine();
                    Ccleaner();
                }
                
                case '3'-> 
                {
                    Ccleaner();
                    System.out.println("Enter anything to return");
                    input.nextLine();
                    Ccleaner();
                }

                case '4'-> 
                {
                    Ccleaner();
                    System.out.println("Enter anything to return");
                    input.nextLine();
                    Ccleaner();
                }

                case '5'-> //Return to menu 
                {
                    Ccleaner();
                    return;
                }
            }
        }
    }

    //Probably will handle everything below this point in database handler
    public void ViewMovies()
    {
        
    }

    public void AddMovie()
    {

    }

    public void UpdateMovie()
    {

    }
}
