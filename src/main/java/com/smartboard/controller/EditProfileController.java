package com.smartboard.controller;

import com.smartboard.model.Data;
import com.smartboard.model.StringLengthException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class EditProfileController implements Closable, Initializable, UpdatableImage {

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
    @Override
    public void onImageClicked(Event event) {
        handleCloseButtonAction();
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        File imageFile = Utility.displayFileChooser(stage);

        if (imageFile != null) {
            System.out.println(imageFile);
            Data.currentUser.setImagePath(imageFile.getPath());
            SBController.staticToolbarImage.setImage(new Image(Data.currentUser.getImagePath()));
        }
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
        } catch (StringLengthException sle) {
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
