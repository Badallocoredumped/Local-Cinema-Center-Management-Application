package help;

import java.io.IOException;
import javafx.fxml.FXML;

public class PrimaryController {

    /**
     * Switches the current scene to the secondary scene by loading the "secondary" FXML file.
     * 
     * @throws IOException If there is an issue with loading the FXML file for the secondary scene.
     */
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
