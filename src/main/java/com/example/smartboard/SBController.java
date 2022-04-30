package com.example.smartboard;

import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SBController implements Closable, Initializable {
    String inputText;
    @FXML
    ScrollPane scrollPane;
    @FXML
    private Button mainExitButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    @Override
    public void handleCloseButtonAction() {
        Stage stage = (Stage) mainExitButton.getScene().getWindow();
        Alert exitAlert = new Alert(Alert.AlertType.CONFIRMATION);
        exitAlert.setTitle("Logout");
        exitAlert.setHeaderText("You're about to logout!");
        exitAlert.setContentText("Press OK to continue, or cancel to return to Smart Board");

        if (exitAlert.showAndWait().get() == ButtonType.OK) {
            stage.close();
        }
    }

    @FXML
    protected void createNewItem(@NotNull ActionEvent event) {
        try {
            String title = "", prompt = "";
            String id = ((MenuItem) event.getTarget()).getId();
            System.out.println(id);

            if (id.equals("newProject")){
                title = "Create a new project";
                prompt = "Project title";
            }else if (id.equals("newColumn")){
                title = "Add a new column";
                prompt = "Column name";
            }

            inputText = TextInputDialog.show(title, prompt);
            System.out.println(inputText);
        } catch (ClassCastException cce) {
            System.out.println(cce.getMessage());
        }
    }

    @FXML
    protected void onLogoutMenuItemSelected() {
        try {
            SmartBoard.createLoginStage();
            Stage stage = (Stage) mainExitButton.getScene().getWindow();
            stage.close();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

}