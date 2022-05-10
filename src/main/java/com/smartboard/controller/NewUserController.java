package com.smartboard.controller;

import com.smartboard.model.Data;
import com.smartboard.model.StringLengthException;
import com.smartboard.model.User;
import com.smartboard.view.LoginView;
import com.smartboard.view.SmartBoardView;
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
        imagePath = Data.defPicturePath;
        userImage.setImage(new Image(imagePath));
    }

    @FXML
    private void onCreateUserButtonClick() {
        String username = newUsernameField.getText().strip();
        String firstname = newFirstnameField.getText().strip();
        String lastname = newLastnameField.getText().strip();
        String password = newPasswordField.getText().strip();

        if (!username.isBlank() && !firstname.isBlank() && !lastname.isBlank() && !password.isBlank()) {
            try {
                createUser(username, password, firstname, lastname);
                Data.currentUser.setImagePath(imagePath);
                String headerText = String.format("New user: %s\nName: %s %s",
                        Data.currentUser.getName(), Data.currentUser.getFirstName(), Data.currentUser.getLastName());

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
                Utility.errorAlert(e.getMessage());
            }
        } else {
            Utility.errorAlert("Enter text in all fields");
        }
    }

    private void createUser(String username, String password, String firstName, String lastName)
            throws IllegalArgumentException, StringLengthException {

        if (usernameExists(username)) {
            throw new IllegalArgumentException("This Username already exists");
        }

        Data.users.add(new User(username, password, firstName, lastName));
        Data.currentUser = Data.users.get(Data.users.size() - 1);
    }

    /**
     * Method to find if the username already exists in the users list
     */
    private boolean usernameExists(String username) {
        boolean returnVal = false;

        for (User user : Data.users) {
            if (username.equalsIgnoreCase(user.getName())) {
                returnVal = true;
                break;
            }
        }

        return returnVal;
    }

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

    @FXML
    @Override
    public void onImageClicked(@NotNull Event event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        File imageFile = Utility.displayFileChooser(stage);

        if (imageFile != null) {
            System.out.println(imageFile);
            imagePath = imageFile.getPath();
            userImage.setImage(new Image(imagePath));
        }
    }
}
