package help;

import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Controller for managing confirmation dialogs in the application.
 * This class provides methods to display a confirmation dialog to the user.
 */
public class ConfirmationDialogController 
{
    private Stage dialogStage;

    /**
     * Sets the dialog stage for this controller.
     * 
     * @param dialogStage The stage to be set as the dialog stage.
     */
    public void setDialogStage(Stage dialogStage) 
    {
        this.dialogStage = dialogStage;
    }

    /**
     * Handles the action of clicking the OK button.
     * Closes the current dialog stage when the button is clicked.
     */
    @FXML
    private void handleOkButtonAction() 
    {
        dialogStage.close();
    }
}