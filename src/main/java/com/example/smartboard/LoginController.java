package com.example.smartboard;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController implements Closable{

    @FXML
    private Button signInButton;
    @FXML
    private Button exitButton;

    @FXML
    private void onSignInButtonClick(){
        try {
            SmartBoard.createPrimaryStage();
            handleCloseButtonAction();
        }catch(IOException ioe){
            System.out.println(ioe.getMessage());
        }
    }

    @FXML
    private void onCreateUserButtonClick(){
        try {
            SmartBoard.createNewUserStage();
            handleCloseButtonAction();
        }catch(IOException ioe){
            System.out.println(ioe.getMessage());
        }
    }

    @FXML @Override
    public void handleCloseButtonAction() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
