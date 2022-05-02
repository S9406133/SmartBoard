package com.smartboard.controller;

import com.smartboard.SmartBoard;
import com.smartboard.model.Data;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController implements Closable {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button exitButton;

    @FXML
    private void onSignInButtonClick() {
        String username = usernameField.getText().strip();
        String password = passwordField.getText().strip();

        if (!username.isBlank() && !password.isBlank()) {
            try {
                Data.login(username, password);

                SmartBoard.createPrimaryStage();

                handleCloseButtonAction();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());

            } catch (IllegalArgumentException iae) {
                Utility.errorAlert(iae.getMessage());
            }
        } else {
            Utility.errorAlert("Enter text in both fields");
        }
    }

    @FXML
    private void onCreateUserButtonClick() {
        try {
            SmartBoard.createNewUserStage();
            handleCloseButtonAction();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    @FXML
    @Override
    public void handleCloseButtonAction() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

}
