package help;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class SessionConfirmationDialogController 
{
    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) 
    {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void handleOkButtonAction() 
    {
        dialogStage.close();
    }
}