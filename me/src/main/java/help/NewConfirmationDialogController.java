package help;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class NewConfirmationDialogController {

    @FXML
    private Label confirmationMessage;

    private Stage dialogStage;
    private boolean confirmed = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML
    private void handleConfirmAction() {
        confirmed = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancelAction() {
        dialogStage.close();
    }
}