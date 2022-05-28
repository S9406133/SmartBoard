/**
 * This is the controller class for the main Login view
 * It controls all aspects of the Login view
 */

package com.smartboard.controller;

import com.smartboard.model.User;
import com.smartboard.model.User_Utils;
import com.smartboard.view.NewUserView;
import com.smartboard.view.SmartBoardView;
import com.smartboard.view.View_Utils;
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

    /**
     * Takes the inputted user data and attempts to login
     */
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
                View_Utils.errorAlert(iae.getMessage());
            }
        } else {
            View_Utils.errorAlert("Enter text in both fields");
        }
    }

    /**
     * Checks each User in the User list and attempts to login
     */
    private void login(String username, String password) throws IllegalArgumentException {
        User loggedInUser = null;

        for (User user : User_Utils.users) {
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
            User_Utils.setCurrentUser(loggedInUser);
        }
    }

    /**
     * Displays the Create user view
     */
    @FXML
    private void onCreateUserButtonClick(Event event) {
        try {
            NewUserView.createNewUserView();
            handleCloseButtonAction(event);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    /**
     * Closes the current stage.
     * Implemented from the Closable interface
     */
    @FXML
    @Override
    public void handleCloseButtonAction(@NotNull Event event) {
        Button button = (Button) event.getTarget();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

}
