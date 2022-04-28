package com.example.smartboard;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class NewUserController implements Closable{

    @FXML
    private Button createUserButton;
    @FXML
    private Button closeButton;

    @FXML
    private void onCreateUserButtonClick(){
        try {
            AlertDialog.show("New user created");
            SmartBoard.createPrimaryStage();
            Stage stage = (Stage) createUserButton.getScene().getWindow();
            stage.close();
        }catch(IOException ioe){
            System.out.println(ioe.getMessage());
        }
    }

    @FXML @Override
    public void handleCloseButtonAction() {
        try {
            SmartBoard.createLoginStage();
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        }catch(IOException ioe){
            System.out.println(ioe.getMessage());
        }
    }

}
