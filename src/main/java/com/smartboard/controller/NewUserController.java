package com.smartboard.controller;

import com.smartboard.model.StringLengthException;
import com.smartboard.model.User_Utils;
import com.smartboard.view.LoginView;
import com.smartboard.view.SmartBoardView;
import com.smartboard.view.View_Utils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewUserController implements Closable, Initializable, UpdatableImage {

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
    private ImageView userImage;
    private String imagePath;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imagePath = User_Utils.defPicturePath;
        userImage.setImage(new Image(imagePath));
    }

    /**
     * Creates a new User object from the entered user data
     */
    @FXML
    private void onCreateUserButtonClick() {
        String username = newUsernameField.getText().strip();
        String firstname = newFirstnameField.getText().strip();
        String lastname = newLastnameField.getText().strip();
        String password = newPasswordField.getText().strip();

        if (!username.isBlank() && !firstname.isBlank() && !lastname.isBlank() && !password.isBlank()) {
            try {
                User_Utils.addNewUser(username, password, firstname, lastname, imagePath);

                String headerText = String.format("New user: %s\nName: %s %s",
                        User_Utils.currentUser.getName(), User_Utils.currentUser.getFirstName(), User_Utils.currentUser.getLastName());

                Alert exitAlert = new Alert(Alert.AlertType.INFORMATION);
                exitAlert.setTitle("New user created");
                exitAlert.setHeaderText(headerText);
                exitAlert.showAndWait();

                SmartBoardView sbView = new SmartBoardView();
                sbView.createSmartBoardStage();
                Stage stage = (Stage) createUserButton.getScene().getWindow();
                stage.close();

            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            } catch (StringLengthException | IllegalArgumentException e) {
                View_Utils.errorAlert(e.getMessage());
            }
        } else {
            View_Utils.errorAlert("Enter text in all fields");
        }
    }

    /**
     * Closes the current stage.
     * Implemented from the Closable interface
     */
    @FXML
    @Override
    public void handleCloseButtonAction(@NotNull Event event) {
        try {
            LoginView.createLoginView();
            Button button = (Button) event.getTarget();
            Stage stage = (Stage) button.getScene().getWindow();
            stage.close();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    /**
     * Displays an image file chooser and sets the chosen image to the current data set
     */
    @FXML
    @Override
    public void onImageClicked(@NotNull Event event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        File imageFile = View_Utils.displayImageFileChooser(stage);

        if (imageFile != null) {
            imagePath = imageFile.getPath();
            userImage.setImage(new Image(imagePath));
        }
    }
}
