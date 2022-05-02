package com.smartboard.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditProfileController implements Closable {

    @FXML
    protected Button editCloseButton;
    @FXML
    protected TextField editFirstnameField;
    @FXML
    protected TextField editLastnameField;

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

        if (!firstname.isBlank()){
            System.out.println("updated - First name: " + firstname);
            updated = true;
        }
        if (!lastname.isBlank()){
            System.out.println("updated - Last name: " + lastname);
            updated = true;
        }

        if (!updated) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("No data entered");
            alert.showAndWait();
        } else {
            this.handleCloseButtonAction();
        }
    }

}
