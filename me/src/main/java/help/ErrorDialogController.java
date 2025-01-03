package me.src.main.java.help;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ErrorDialogController 
{
    @FXML
    private Label errorMessageLabel;

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) 
    {
        this.dialogStage = dialogStage;
    }

    public void setErrorMessage(String errorMessage) 
    {
        errorMessageLabel.setText(errorMessage);
    }

    @FXML
    private void handleCloseButtonAction() 
    {
        dialogStage.close();
    }
}