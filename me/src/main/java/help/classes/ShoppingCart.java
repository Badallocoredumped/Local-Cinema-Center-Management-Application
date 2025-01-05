package help.classes;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private static ShoppingCart instance;
    private List<String> selectedSeats;
    private Movie selectedMovie;
    private Session selectedDaySessionAndHall;
    private List<String> itemsBought;

    private ShoppingCart() 
    {
        selectedSeats = new ArrayList<>();
        itemsBought = new ArrayList<>();
    }

    //basically a global point of access to the instance
    public static ShoppingCart getInstance() 
    {
        if (instance == null) 
        {
            instance = new ShoppingCart();
        }
        return instance;
    }

    public void addSeats(List<String> seats) 
    {
        selectedSeats.addAll(seats);
    }

    public List<String> getSelectedSeats() 
    {
        return new ArrayList<>(selectedSeats);
    }

    public Movie getSelectedMovie() 
    {
        return selectedMovie;
    }

    public void setSelectedMovie(Movie selectedMovie) 
    {
        this.selectedMovie = selectedMovie;
    }

    public Session getSelectedDaySessionAndHall() 
    {
        if(selectedDaySessionAndHall == null)
        {
            System.out.println("No session selected");
            return null;
        }
        return selectedDaySessionAndHall;
    }

    public void setSelectedDaySessionAndHall(Session selectedDaySessionAndHall) 
    {
        this.selectedDaySessionAndHall = selectedDaySessionAndHall;
    }

    public List<String> getItemsBought() 
    {
        return new ArrayList<>(itemsBought);
    }

    public void addItemBought(String item) 
    {
        itemsBought.add(item);
    }

    public void clearSession() 
    {
        selectedDaySessionAndHall = null;
    }
    public void clear() 
    {
        selectedSeats.clear();
        selectedMovie = null;
        selectedDaySessionAndHall = null;
        itemsBought.clear();
    }
    public void clearSeats() 
    {
        selectedSeats.clear();
    }

    public void clearItemsBought() 
    {
        itemsBought.clear();
    }
}