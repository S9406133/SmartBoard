package com.smartboard.controller;

import com.smartboard.model.Data;
import com.smartboard.model.StringLengthException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditProfileController implements Closable, Initializable {

    @FXML
    private ImageView editUserImage;
    @FXML
    private Label currentUsername;
    @FXML
    private Label currentName;
    @FXML
    private Button editCloseButton;
    @FXML
    private TextField editFirstnameField;
    @FXML
    private TextField editLastnameField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUsername.setText(Data.currentUser.getName());
        currentName.setText(Data.currentUser.getFirstName() + " " + Data.currentUser.getLastName());
        editUserImage.setImage(new Image(Data.currentUser.getImagePath()));
    }

    @FXML
    @Override
    public void handleCloseButtonAction() {
        Stage stage = (Stage) editCloseButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void onUpdateClicked() {
        String firstname = editFirstnameField.getText().strip();
        String lastname = editLastnameField.getText().strip();
        boolean updated = false;
        String alertMessage = "No data entered";

        try {
            if (!firstname.isBlank()) {
                Data.currentUser.setFirstName(firstname);
                updated = true;
            }
            if (!lastname.isBlank()) {
                Data.currentUser.setLastName(lastname);
                updated = true;
            }
        }catch(StringLengthException sle){
            alertMessage = sle.getMessage();
        }

        if (!updated) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(alertMessage);
            alert.showAndWait();
        } else {
            SBController.staticToolbarName.setText(Data.currentUser.getFirstName() + " " + Data.currentUser.getLastName());
            this.handleCloseButtonAction();
        }
    }

}
