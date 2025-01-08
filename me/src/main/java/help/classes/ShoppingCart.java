package help.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart {

    private static ShoppingCart instance;
    private List<String> selectedSeats;
    private Movie selectedMovie;
    private Session selectedDaySessionAndHall;
    private Map<Product, Integer> itemsBought; // Map to store products and their quantities

    private ShoppingCart() 
    {
        selectedSeats = new ArrayList<>();
        itemsBought = new HashMap<>();
    }

    // Singleton instance
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
        if (selectedDaySessionAndHall == null) 
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

    /**
     * Adds a product to the cart with the specified quantity.
     * If the product already exists in the cart, it updates the quantity.
     * 
     * @param item     The product to add.
     * @param quantity The quantity of the product.
     */
    public void addItemBought(Product item, int quantity) 
    {
        itemsBought.put(item, itemsBought.getOrDefault(item, 0) + quantity);
    }

    /**
     * Removes a product from the cart.
     * 
     * @param item The product to remove.
     */
    public void removeItemBought(Product item) 
    {
        itemsBought.remove(item);
    }

    /**
     * Gets all items in the cart with their quantities.
     * 
     * @return A map of products and their quantities.
     */
    public Map<Product, Integer> getItemsBought() 
    {
        return new HashMap<>(itemsBought);
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
    public void clearSelectedMovie() 
    {
        selectedMovie = null;
    }
}
