package com.smartboard.controller;

import com.smartboard.model.Data;
import com.smartboard.model.User;
import com.smartboard.view.NewUserView;
import com.smartboard.view.SmartBoardView;
import com.smartboard.view.Utility;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class LoginController implements Closable {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;

    @FXML
    private void onSignInButtonClick(Event event) {
        String username = usernameField.getText().strip();
        String password = passwordField.getText().strip();

        if (!username.isBlank() && !password.isBlank()) {
            try {
                login(username, password);

                SmartBoardView sbView = new SmartBoardView();
                sbView.createSmartBoardStage();

                handleCloseButtonAction(event);
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());

            } catch (IllegalArgumentException iae) {
                Utility.errorAlert(iae.getMessage());
            }
        } else {
            Utility.errorAlert("Enter text in both fields");
        }
    }

    private void login(String username, String password) throws IllegalArgumentException {
        User loggedInUser = null;

        for (User user : Data.users) {
            if (user.getName().equalsIgnoreCase(username)) {
                if (user.validateLogin(username, password)) {
                    loggedInUser = user;
                    break;
                } else {
                    throw new IllegalArgumentException("Password is incorrect");
                }
            }
        }

        if (loggedInUser == null) {
            throw new IllegalArgumentException("No such Username");
        } else {
            Data.currentUser = loggedInUser;
        }
    }

    @FXML
    private void onCreateUserButtonClick(Event event) {
        try {
            NewUserView.createNewUserView();
            handleCloseButtonAction(event);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    @FXML
    @Override
    public void handleCloseButtonAction(@NotNull Event event) {
        Button button = (Button) event.getTarget();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

}
