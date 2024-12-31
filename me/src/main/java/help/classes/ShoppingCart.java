package help.classes;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private static ShoppingCart instance;
    private List<String> selectedSeats;

    private ShoppingCart() {
        selectedSeats = new ArrayList<>();
    }

    public static ShoppingCart getInstance() {
        if (instance == null) {
            instance = new ShoppingCart();
        }
        return instance;
    }

    public void addSeats(List<String> seats) {
        selectedSeats.addAll(seats);
    }

    public List<String> getSelectedSeats() {
        return new ArrayList<>(selectedSeats);
    }

    public void clear() {
        selectedSeats.clear();
    }
}