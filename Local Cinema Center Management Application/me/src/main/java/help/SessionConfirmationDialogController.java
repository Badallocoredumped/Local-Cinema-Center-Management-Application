package help;

import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Controller class responsible for handling the session confirmation dialog logic.
 * This dialog allows users to confirm the session details before finalizing their session selection.
 */
public class SessionConfirmationDialogController 
{
    private Stage dialogStage;

    /**
     * Sets the dialog stage for this controller.
     * This method allows the controller to interact with the dialog window
     * it is controlling, typically used for showing or closing the dialog.
     * 
     * @param dialogStage The Stage object representing the dialog window.
     */
    public void setDialogStage(Stage dialogStage) 
    {
        this.dialogStage = dialogStage;
    }

    /**
     * Handles the action when the OK button is clicked in the dialog.
     * This method closes the dialog stage.
     */
    @FXML
    private void handleOkButtonAction() 
    {
        dialogStage.close();
    }
}