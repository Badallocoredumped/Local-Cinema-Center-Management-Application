package help;

import java.io.IOException;
import javafx.fxml.FXML;

public class SecondaryController {

    /**
     * Switches the current scene to the "primary" scene.
     * This method is typically used to navigate between scenes within the application.
     * 
     * @throws IOException If the "primary" scene cannot be loaded, an IOException will be thrown.
     */
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}