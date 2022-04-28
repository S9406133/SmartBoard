package com.example.smartboard;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class SBController implements Closable{
    @FXML
    private MenuItem logout;
    @FXML
    private Button mainExitButton;

    @FXML @Override
    public void handleCloseButtonAction() {
        Stage stage = (Stage) mainExitButton.getScene().getWindow();
        Alert exitAlert = new Alert(Alert.AlertType.CONFIRMATION);
        exitAlert.setTitle("Logout");
        exitAlert.setHeaderText("You're about to logout!");
        exitAlert.setContentText("Press OK to continue, or cancel to return to Smart Board");

        if (exitAlert.showAndWait().get() == ButtonType.OK){
            System.out.println("You successfully logged out");
            stage.close();
        }
    }

    @FXML
    protected void onLogoutMenuItemSelected(){
        try {
            SmartBoard.createLoginStage();
            Stage stage = (Stage) mainExitButton.getScene().getWindow();
            stage.close();
        }catch(IOException ioe){
            System.out.println(ioe.getMessage());
        }
    }
}