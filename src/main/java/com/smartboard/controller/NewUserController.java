package com.smartboard.controller;

import com.smartboard.SmartBoard;
import com.smartboard.model.Data;
import com.smartboard.model.StringLengthException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewUserController implements Closable, Initializable {

    @FXML
    private TextField newUsernameField;
    @FXML
    private TextField newFirstnameField;
    @FXML
    private TextField newLastnameField;
    @FXML
    private TextField newPasswordField;
    @FXML
    private Button createUserButton;
    @FXML
    private Button closeButton;
    @FXML
    private ImageView userImage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       userImage.setImage(new Image(Data.defPicturePath));
    }

    @FXML
    private void onCreateUserButtonClick() {
        String username = newUsernameField.getText().strip();
        String firstname = newFirstnameField.getText().strip();
        String lastname = newLastnameField.getText().strip();
        String password = newPasswordField.getText().strip();

        if (!username.isBlank() && !firstname.isBlank() && !lastname.isBlank() && !password.isBlank()) {
            try {
                Data.createUser(username, password, firstname, lastname);
                String headerText = String.format("New user: %s\nName: %s %s",
                        Data.currentUser.getName(), Data.currentUser.getFirstName(), Data.currentUser.getLastName());

                Alert exitAlert = new Alert(Alert.AlertType.INFORMATION);
                exitAlert.setTitle("New user created");
                exitAlert.setHeaderText(headerText);
                exitAlert.showAndWait();

                SmartBoard.createPrimaryStage();
                Stage stage = (Stage) createUserButton.getScene().getWindow();
                stage.close();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            } catch (StringLengthException sle) {
                Utility.errorAlert(sle.getMessage());
            }
        } else {
            Utility.errorAlert("Enter text in all fields");
        }
    }

    @FXML
    @Override
    public void handleCloseButtonAction() {
        try {
            SmartBoard.createLoginStage();
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

}
