package com.example.smartboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
            Alert exitAlert = new Alert(Alert.AlertType.INFORMATION);
            exitAlert.setTitle("New user created");
            exitAlert.setHeaderText("New user: " + "username here");
            exitAlert.showAndWait();

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
