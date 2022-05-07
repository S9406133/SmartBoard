package com.smartboard.controller;

import com.smartboard.model.Data;
import com.smartboard.model.User;
import com.smartboard.view.NewUserView;
import com.smartboard.view.SmartBoardView;
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
    private Button exitButton;

    @FXML
    private void onSignInButtonClick(Event event) {
        String username = usernameField.getText().strip();
        String password = passwordField.getText().strip();
        System.out.println("init " + username);
        System.out.println("init " + password);

        if (!username.isBlank() && !password.isBlank()) {
            try {
                login(username, password);

                SmartBoardView.createSmartBoardStage();

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
                System.out.println(user.getName());
                if (user.validateLogin(username, password)) {
                    loggedInUser = user;
                    System.out.println(loggedInUser.getName());
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
            NewUserView.createNewUserStage();
            handleCloseButtonAction(event);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    @FXML
    @Override
    public void handleCloseButtonAction(@NotNull Event event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

}
