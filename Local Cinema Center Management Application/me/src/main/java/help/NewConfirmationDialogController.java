package help;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller class responsible for managing a confirmation dialog.
 * This dialog displays a message and allows the user to  confirm.
 */
public class NewConfirmationDialogController {

    @FXML
    private Label confirmationMessage;

    private Stage dialogStage;
    private boolean confirmed = false;

    /**
     * Sets the dialog stage (the window where the dialog will be displayed).
     * This method allows you to set the Stage associated with this dialog.
     *
     * @param dialogStage The Stage that represents the dialog window.
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Returns whether the user has confirmed the action in the dialog.
     * The confirmation is set to true when the user clicks the confirm button.
     *
     * @return true if the user has confirmed the action, false otherwise.
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Handles the confirm action. This method sets the confirmed flag to true 
     * and closes the dialog window.
     */
    @FXML
    private void handleConfirmAction() {
        confirmed = true;
        dialogStage.close();
    }

    /**
     * Handles the cancel action. This method simply closes the dialog window without setting any confirmation.
     */
    @FXML
    private void handleCancelAction() {
        dialogStage.close();
    }
}