import java.time.LocalDate;

public class Customer extends Person {
    //Customer will have a customer ID, and a list of orders.
    //The table will have a column to save the order and tickets for each customer.

    protected short age;

    public Customer(short age, String Name, String Surname, int ID)
    {
        super(Name, Surname, ID);
        this.age = age;
    }
}