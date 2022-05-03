package com.smartboard.controller;

import com.smartboard.SmartBoard;
import com.smartboard.model.Data;
import com.smartboard.model.Project;
import com.smartboard.view.LoginView;
import com.smartboard.view.ProjectView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SBController implements Closable, Initializable {

    @FXML
    private TabPane projectsPane;
    private String inputText;
    @FXML
    private Button mainExitButton;
    @FXML
    private Label toolbarQuote;
    @FXML
    private ImageView toolbarImage;
    @FXML
    protected Label toolbarName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toolbarQuote.setText(Data.getRandomQuote().toString());
        setUserData();

        projectsPane.getTabs().addAll(new Tab("init 1"), new Tab("init 2"));
        ProjectView projectView = new ProjectView(projectsPane);

        for (Project project: Data.currentUser.getSubItemList()) {
            projectView.createProjectView(project);
            if (project.isDefault()) {
                System.out.println(project.isDefault());
                projectsPane.getSelectionModel().selectLast();
            }
        }
    }

    public void setUserData(){
        toolbarImage.setImage(new Image(Data.currentUser.getImagePath()));
        toolbarName.setText(Data.currentUser.getFirstName() + " " + Data.currentUser.getLastName());
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
            String title = "", prompt = "";
            String id = ((MenuItem) event.getTarget()).getId();
            System.out.println(id);

            switch (id) {
                case "newProject" -> {
                    title = "Create a new project";
                    prompt = "Project title";
                }
                case "newColumn" -> {
                    title = "Add a new column";
                    prompt = "Column name";
                }
                case "renameProject" -> {
                    title = "Rename project";
                    prompt = "Project title";
                }
            }

            inputText = TextInputDialog.show(title, prompt);
            System.out.println(inputText);
        } catch (ClassCastException cce) {
            System.out.println(cce.getMessage());
        }
    }

    @FXML
    protected void onEditProfileSelected(){
        try {
            String fxmlName = "editprofile.fxml";
            FXMLLoader fxmlLoader = new FXMLLoader(SmartBoard.class.getResource(fxmlName));
            Scene editScene = new Scene(fxmlLoader.load());
            Stage editStage = new Stage(StageStyle.UTILITY);
            editStage.initModality(Modality.APPLICATION_MODAL);
            editStage.setTitle("Edit user profile");
            editStage.getIcons().add(SmartBoard.icon);
            editStage.setScene(editScene);
            editStage.setResizable(false);
            editStage.showAndWait();
        } catch (IOException ioe){
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