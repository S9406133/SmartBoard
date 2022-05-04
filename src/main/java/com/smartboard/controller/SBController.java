package com.smartboard.controller;

import com.smartboard.model.Data;
import com.smartboard.model.Project;
import com.smartboard.model.StringLengthException;
import com.smartboard.view.EditProfileView;
import com.smartboard.view.LoginView;
import com.smartboard.view.ProjectView;
import com.smartboard.view.TextInputDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SBController implements Closable, Initializable {

    @FXML
    private TabPane projectsPane;
    private ProjectView projectView;
    @FXML
    private Button mainExitButton;
    @FXML
    private Label toolbarQuote;
    @FXML
    private ImageView toolbarImage;
    public static ImageView staticToolbarImage;
    @FXML
    protected Label toolbarName;
    public static Label staticToolbarName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        staticToolbarImage = toolbarImage;
        staticToolbarName = toolbarName;
        projectsPane.setTabMinWidth(100);

        toolbarImage.setImage(new Image(Data.currentUser.getImagePath()));
        toolbarName.setText(Data.currentUser.getFirstName() + " " + Data.currentUser.getLastName());

        toolbarQuote.setText(Data.getRandomQuote().toString());

        projectView = new ProjectView(projectsPane);

        for (Project project : Data.currentUser.getSubItemList()) {
            projectView.createProjectView(project);
            if (project.isDefault()) {
                projectsPane.getSelectionModel().selectLast();
            }
        }
        //projectsPane.getTabs().addAll(new Tab("init 1"), new Tab("init 2"));
    }

    @FXML
    @Override
    public void handleCloseButtonAction() {
        Stage stage = (Stage) mainExitButton.getScene().getWindow();
        Alert exitAlert = new Alert(Alert.AlertType.CONFIRMATION);
        exitAlert.setTitle("Logout");
        exitAlert.setHeaderText("You're about to exit Smart Board!");
        exitAlert.setContentText("Press OK to exit, or cancel to return to Smart Board");

        if (exitAlert.showAndWait().get() == ButtonType.OK) {
            stage.close();
        }
    }

    @FXML
    protected void showInputDialog(@NotNull ActionEvent event) {
        try {
            String id = ((MenuItem) event.getTarget()).getId();

            switch (id) {
                case "newProject" -> addProject();
                case "newColumn" -> TextInputDialog.show("Add a new column", "Column name");
                case "renameProject" -> renameProject();
            }

        } catch (ClassCastException cce) {
            System.out.println(cce.getMessage());
        } catch (StringLengthException sle) {
            Utility.errorAlert(sle.getMessage());
        }
    }

    private void addProject() throws StringLengthException {
        String name = TextInputDialog.show("Create a new project", "Project title");
        Data.currentUser.addSubItem(name);
        Project newProject = Data.currentUser.getSubItem(Data.currentUser.getListSize() - 1);
        projectView.createProjectView(newProject);
    }

    private void renameProject() throws StringLengthException {
        String newName = TextInputDialog.show("Rename project", "Project title");
        int currentTabIndex = projectsPane.getSelectionModel().getSelectedIndex();
        Data.currentUser.getSubItem(currentTabIndex).setName(newName);
        projectsPane.getTabs().get(currentTabIndex).setText(
                Data.currentUser.getSubItem(currentTabIndex).getName()
        );
    }

    @FXML
    protected void onEditProfileSelected() {
        try {
            EditProfileView.createEditProfileStage();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    @FXML
    protected void onLogoutMenuItemSelected() {
        try {
            LoginView.createLoginStage();
            Stage stage = (Stage) mainExitButton.getScene().getWindow();
            stage.close();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    @FXML
    protected void onDeleteProjectSelected() {
        Alert deleteAlert = new Alert(Alert.AlertType.WARNING);
        deleteAlert.getButtonTypes().add(ButtonType.CANCEL);
        deleteAlert.setTitle("Delete project");
        deleteAlert.setHeaderText("You're about to delete the current project!");
        deleteAlert.setContentText("Press OK to delete, or cancel to return to Smart Board");

        if (deleteAlert.showAndWait().get() == ButtonType.OK) {
            System.out.println("Project has been deleted");
        }
    }

    @FXML
    protected void showAboutInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Smart Board");
        alert.setHeaderText("""
                Smart Board version 1.1
                Developer: Simon Mckindley
                Created for Further Programming A2
                May 2022""");
        alert.showAndWait();
    }

}