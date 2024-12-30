import java.util.Scanner;

public abstract class Person {
    //Will inherit customer annd Employees (maybe we will just use Person directly as an client).

    protected String name;
    protected String surname;
    protected int ID;

    public static Scanner input = new Scanner(System.in);

    public Person(String Name, String Surname, int ID)
    {
        this.name = Name;
        this.surname = Surname;
        this.ID = ID;
    }

    public String getName() {return name;}
    public void setName(String nametochange) {this.name = nametochange;}
    public String getSurname() {return surname;}
    public void setSurname(String surnametochange) {this.surname = surnametochange;}
    public int getID() {return ID;}

}
