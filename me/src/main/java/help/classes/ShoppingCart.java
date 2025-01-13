package help.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing a shopping cart that holds the products (movies and other items).
 */
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

    /**
     * Returns the singleton instance of the ShoppingCart.
     * If the instance is null, it creates a new instance of the ShoppingCart.
     * 
     * @return the singleton instance of the ShoppingCart.
     */
    public static ShoppingCart getInstance() 
    {
        if (instance == null) 
        {
            instance = new ShoppingCart();
        }
        return instance;
    }

    /**
     * Adds a list of selected seats to the shopping cart.
     * The provided seats are added to the existing list of selected seats.
     *
     * @param seats the list of seats to add to the shopping cart.
     */
    public void addSeats(List<String> seats) 
    {
        selectedSeats.addAll(seats);
    }

    /**
     * Returns a new list containing all selected seats.
     * This method provides a copy of the selected seats.
     *
     * @return a list of selected seats.
     */
    public List<String> getSelectedSeats() 
    {
        return new ArrayList<>(selectedSeats);
    }

    /**
     * Returns the currently selected movie.
     * 
     * @return the selected Movie object, or null if no movie is selected.
     */
    public Movie getSelectedMovie() 
    {
        return selectedMovie;
    }

    /**
     * Sets the selected movie.
     * This method updates the selected movie in the shopping cart.
     * 
     * @param selectedMovie the Movie object to set as the selected movie.
     */
    public void setSelectedMovie(Movie selectedMovie) 
    {
        this.selectedMovie = selectedMovie;
    }

    /**
     * Returns the selected session, day, and hall.
     * If no session is selected, it prints a message and returns null.
     * 
     * @return the selected Session object, or null if no session is selected.
     */
    public Session getSelectedDaySessionAndHall() 
    {
        if (selectedDaySessionAndHall == null) 
        {
            System.out.println("No session selected");
            return null;
        }
        return selectedDaySessionAndHall;
    }

    /**
     * Sets the selected session, day, and hall.
     * This method updates the selected session in the shopping cart.
     * 
     * @param selectedDaySessionAndHall the Session object to set as the selected session.
     */
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

    /**
     * Clears the selected seats, movie, day/session/hall, and items bought.
     * This method resets all selections to their initial empty or null state.
     */
    public void clear() 
    {
        selectedSeats.clear();
        selectedMovie = null;
        selectedDaySessionAndHall = null;
        itemsBought.clear();
    }

    /**
    * Clears the selected seats, removing all previously selected seats.
     */
    public void clearSeats() 
    {
        selectedSeats.clear();
    }

    /**
    * Clears the items bought, removing all items from the purchase list.
    */
    public void clearItemsBought() 
    {
        itemsBought.clear();
    }

    /**
     * Clears the selected movie, setting it to null.
     */
    public void clearSelectedMovie() 
    {
        selectedMovie = null;
    }
}
