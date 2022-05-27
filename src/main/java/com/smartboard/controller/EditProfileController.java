package com.smartboard.controller;

import com.smartboard.model.StringLengthException;
import com.smartboard.model.User_Utils;
import com.smartboard.view.Utility;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

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
    private TextField editFirstnameField;
    @FXML
    private TextField editLastnameField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUsername.setText(User_Utils.currentUser.getName());
        currentName.setText(User_Utils.currentUser.getFirstName() + " " + User_Utils.currentUser.getLastName());
        editUserImage.setImage(new Image(User_Utils.currentUser.getImagePath()));
    }

    @FXML
    @Override
    public void handleCloseButtonAction(@NotNull Event event) {
        Node button = (Node) event.getTarget();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    @FXML
    @Override
    public void onImageClicked(@NotNull Event event) {
        handleCloseButtonAction(event);
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        File imageFile = Utility.displayImageFileChooser(stage);

        if (imageFile != null) {
            User_Utils.updateUserImagepath(imageFile.getPath());
            SBController.staticToolbarImage.setImage(new Image(User_Utils.currentUser.getImagePath()));
        }
    }

    @FXML
    protected void onUpdateClicked(Event event) {
        String firstname = editFirstnameField.getText().strip();
        String lastname = editLastnameField.getText().strip();
        boolean updated = false;
        String alertMessage = "No data entered";

        try {
            if (!firstname.isBlank()) {
                User_Utils.updateUserFirstname(firstname);
                updated = true;
            }
            if (!lastname.isBlank()) {
                User_Utils.updateUserLastname(lastname);
                updated = true;
            }
        } catch (StringLengthException sle) {
            alertMessage = sle.getMessage();
        }

        if (!updated) {
            Utility.errorAlert(alertMessage);
        } else {
            SBController.staticToolbarName.setText(
                    User_Utils.currentUser.getFirstName() + " " + User_Utils.currentUser.getLastName()
            );
            this.handleCloseButtonAction(event);
        }
    }

}
