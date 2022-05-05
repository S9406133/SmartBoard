package com.smartboard.controller;

import com.smartboard.model.Column;
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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SBController implements Closable, Initializable {

    @FXML
    private TabPane projectsPane;
    private ProjectView projectView;
    @FXML
    private Menu workspaceMenu;
    private ArrayList<MenuItem> itemList;
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

    public SBController() {
        itemList = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        staticToolbarImage = toolbarImage;
        staticToolbarName = toolbarName;
        projectsPane.setTabMinWidth(100);

        toolbarImage.setImage(new Image(Data.currentUser.getImagePath()));
        toolbarName.setText(Data.currentUser.getFirstName() + " " + Data.currentUser.getLastName());
        toolbarQuote.setText(Data.getRandomQuote().toString());

        projectView = new ProjectView(projectsPane);
        displayProjects();
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
                case "newColumn" -> addColumn();
                case "renameProject" -> renameProject();
            }

        } catch (ClassCastException cce) {
            System.out.println(cce.getMessage());
        } catch (StringLengthException sle) {
            Utility.errorAlert(sle.getMessage());
        }
    }

    private void displayProjects() {
        int i = 0;
        for (Project project : Data.currentUser.getSubItemList()) {
            projectView.createProjectView(project);
            if (project.isDefault()) {
                projectsPane.getSelectionModel().selectLast();
            }
            addProjectToMenu(project, projectsPane.getTabs().get(i));
            i++;
        }
    }

    protected void addProjectToMenu(Project project, Tab tab) {
        itemList.add(new MenuItem(project.getName()));
        MenuItem currItem = itemList.get(itemList.size() - 1);
        currItem.setId(project.getName());
        currItem.setOnAction(actionEvent -> {
            projectsPane.getSelectionModel().select(tab);
        });

        workspaceMenu.getItems().add(currItem);
    }

    private void removeProjectsFromMenu() {
        for (MenuItem item : workspaceMenu.getItems()) {
            if (item.getId() != null) {
                if (!item.getId().equals("newProject")) {
                    System.out.println(item.getId());
                    item.setVisible(false);
                }
            }
        }
    }

    private void addProject() throws StringLengthException {
        String name = TextInputDialog.show("Create a new project", "Project title");
        Data.currentUser.addSubItem(name);
        Project newProject = Data.currentUser.getSubItem(Data.currentUser.getListSize() - 1);
        projectView.createProjectView(newProject);
        addProjectToMenu(newProject, projectsPane.getTabs().get(Data.currentUser.getListSize() - 1));
    }

    private void addColumn() throws StringLengthException {
        String name = TextInputDialog.show("Add a new column", "Column name");
        int currentTabIndex = getCurrentTabIndex();
        Project currentProject = getCurrentProject();
        currentProject.addSubItem(name);
        Column newColumn = currentProject.getSubItem(currentProject.getListSize() - 1);

        try {       // Could also clear the pane and reload
            ScrollPane scrollPane = (ScrollPane) projectsPane.getTabs().get(currentTabIndex).getContent();
            HBox hBox = (HBox) scrollPane.getContent();
            hBox.getChildren().add(projectView.createColumnView(newColumn));
        } catch (ClassCastException cce) {
            System.out.println(cce.getMessage());
        }
    }

    private void renameProject() throws StringLengthException {
        String newName = TextInputDialog.show("Rename project", "Project title");
        int currentTabIndex = getCurrentTabIndex();
        Project currentProject = getCurrentProject();
        currentProject.setName(newName);
        projectsPane.getTabs().get(currentTabIndex).setText(currentProject.getName());
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
    protected void onSetDefaultSelected() {
        Project currentProject = getCurrentProject();

        if (!currentProject.isDefault()) {
            Data.currentUser.setDefaultProject(getCurrentTabIndex());

            for (int i = 0; i < Data.currentUser.getListSize(); i++) {
                projectsPane.getTabs().get(i).setText(Data.currentUser.getSubItem(i).getName());
            }
        }
    }

    @FXML
    protected void onDeleteProjectSelected() {
        String alertTitle = "Delete project";
        String projectName = getCurrentProject().getName();
        Alert deleteAlert = new Alert(Alert.AlertType.WARNING);
        deleteAlert.getButtonTypes().add(ButtonType.CANCEL);
        deleteAlert.setTitle(alertTitle);
        deleteAlert.setHeaderText("You're about to delete the current project - " + projectName + "!");
        deleteAlert.setContentText("Press OK to delete, or cancel to return to Smart Board");

        if (deleteAlert.showAndWait().get() == ButtonType.OK) {
            if (Data.currentUser.removeSubItem(getCurrentProject())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(alertTitle);
                alert.setHeaderText("Project - " + projectName + " successfully deleted.");
                alert.showAndWait();

                removeProjectsFromMenu();
                projectsPane.getTabs().clear();
                displayProjects();

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(alertTitle);
                alert.setHeaderText("Error deleting project - " + projectName);
                alert.showAndWait();
            }
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

    private int getCurrentTabIndex() {
        return projectsPane.getSelectionModel().getSelectedIndex();
    }

    private Project getCurrentProject() {
        return Data.currentUser.getSubItem(getCurrentTabIndex());
    }

}