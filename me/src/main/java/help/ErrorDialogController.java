package help;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for managing error dialogs in the application.
 * This class provides methods to display error messages to the user
 * and handle the closing of the error dialog window.
 */
public class ErrorDialogController 
{
    @FXML
    private Label errorMessageLabel;

    private Stage dialogStage;

    /**
     * Sets the dialog stage for this controller.
     * This allows the controller to control the dialog window.
     * 
     * @param dialogStage The stage to be set as the dialog stage.
     */
    public void setDialogStage(Stage dialogStage) 
    {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the error message to be displayed in the dialog.
     * Updates the label with the provided error message text.
     * 
     * @param errorMessage The error message to display.
     */
    public void setErrorMessage(String errorMessage) 
    {
        errorMessageLabel.setText(errorMessage);
    }

    /**
     * Handles the action of clicking the close button.
     * Closes the current dialog stage.
     */
    @FXML
    private void handleCloseButtonAction() 
    {
        dialogStage.close();
    }
}