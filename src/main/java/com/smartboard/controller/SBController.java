package com.smartboard.controller;

import com.smartboard.SmartBoard;
import com.smartboard.model.*;
import com.smartboard.view.EditProfileView;
import com.smartboard.view.LoginView;
import com.smartboard.view.ProjectView;
import com.smartboard.view.TextInputDialog;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class SBController implements Closable, Initializable {

    @FXML
    private TabPane projectsPane;
    private VBox columnBox;
    private AnchorPane taskPane;
    //private ProjectView projectView;
    @FXML
    private Menu workspaceMenu;
    private final ArrayList<MenuItem> itemList;
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

        toolbarImage.setImage(new Image(Data.currentUser.getImagePath()));
        toolbarName.setText(Data.currentUser.getFirstName() + " " + Data.currentUser.getLastName());
        toolbarQuote.setText(getRandomQuote().toString());

        //projectView = new ProjectView(projectsPane);
        try {
            displayProjects();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayProjects() throws IOException {
        int i = 0;
        for (Project project : Data.currentUser.getSubItemList()) {
            //projectView.
            createProjectView(project);
            if (project.isDefault()) {
                projectsPane.getSelectionModel().selectLast();
            }
            addProjectToMenu(project, projectsPane.getTabs().get(i));
            i++;
        }
    }

    private void createProjectView(Project project) {
        HBox columnHolder = new HBox();
//        for (Column column : project.getSubItemList()) {
//            columnHolder.getChildren().add(createColumnView(column));
//        }
        loadColumns(columnHolder, project);
        // ALT project.getSubItemList().forEach(column -> hBox.getChildren().add(createColumnView(column)));
        ScrollPane scrollPane = new ScrollPane(columnHolder);
        Tab projectTab = new Tab(project.getName(), scrollPane);
        projectsPane.getTabs().add(projectTab);
    }

    private void loadColumns(HBox columnHolder, Project project){
        for (Column column : project.getSubItemList()) {
            columnHolder.getChildren().add(createColumnView(column));
        }
    }

    private VBox createColumnView(Column column) {
        // Column Header
        Button addTaskButton = new Button("Add Task");


        Button deleteButton = new Button();
        ImageView deleteIcon = new ImageView(new Image("delete_icon.png"));
        deleteIcon.preserveRatioProperty().setValue(true);
        deleteIcon.setFitHeight(17);
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setOnAction(
                actionEvent -> onDeleteColumnClicked(column)
        );

        Label columnLabel = new Label(column.getName());
        columnLabel.setFont(new Font(14));
        columnLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        columnLabel.setPrefWidth(180);

        ToolBar columnHeader = new ToolBar(addTaskButton, deleteButton, columnLabel);
        columnHeader.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        columnHeader.setStyle("-fx-background-color: lightblue; -fx-border-color: grey;");
        VBox.setMargin(columnHeader, new Insets(5));

        // Column
        columnBox = new VBox(columnHeader);

        for (Task task : column.getSubItemList()) {
            columnBox.getChildren().add(createTaskView(task));
        }
        // ALT column.getSubItemList().forEach(task -> columnBox.getChildren().add(createTaskView(task)));

        columnBox.setSpacing(10);
        columnBox.setPrefWidth(300);

        return columnBox;
    }

    private AnchorPane createTaskView(Task task) {
        // Task pane
        Button update = new Button("Update");
        Button delete = new Button("Delete");
        update.setPrefWidth(65);
        update.setLayoutX(225);
        update.setLayoutY(44);
        delete.setPrefWidth(65);
        delete.setLayoutX(225);
        delete.setLayoutY(74);
        Label taskName = new Label(task.getName());
        Label checklist = new Label("Checklist 0/3");
        Label dueDate = new Label("Due Date: " + task.getDueDate().toLocalDate());
        taskName.setLayoutX(14);
        taskName.setLayoutY(6);
        checklist.setLayoutX(14);
        checklist.setLayoutY(46);
        dueDate.setLayoutX(14);
        dueDate.setLayoutY(86);
        taskPane = new AnchorPane(update, delete, taskName, checklist, dueDate);
        taskPane.prefWidth(300);
        taskPane.setStyle("-fx-background-color: lightgreen; -fx-border-color: grey;");
        VBox.setMargin(taskPane, new Insets(5));
        taskPane.paddingProperty().setValue(new Insets(5));

        return taskPane;
    }

    private void addProjectToMenu(@NotNull Project project, Tab tab) {
        itemList.add(new MenuItem(project.getName()));
        MenuItem currItem = itemList.get(itemList.size() - 1);
        currItem.setId(project.getName());
        currItem.setOnAction(actionEvent -> projectsPane.getSelectionModel().select(tab));

        workspaceMenu.getItems().add(currItem);
    }

    private void removeProjectsFromMenu() {
        for (MenuItem item : workspaceMenu.getItems()) {
            if (item.getId() != null) {
                if (!item.getId().equals("newProject")) {
                    item.setVisible(false);
                }
            }
        }
    }

    @FXML
    private void showInputDialog(@NotNull ActionEvent event) {
        try {
            String id = ((MenuItem) event.getTarget()).getId();

            switch (id) {
                case "newProject" -> addProject();
                case "newColumn" -> addColumn();
                case "renameProject" -> renameProject();
            }

        } catch (ClassCastException | IOException e) {
            e.printStackTrace();
        } catch (StringLengthException sle) {
            Utility.errorAlert(sle.getMessage());
        }
    }

    private void addProject() throws StringLengthException, IOException {
        String name = TextInputDialog.show("Create a new project", "Project title");

        if (name != null) {
            Data.currentUser.addSubItem(name);
            Project newProject = Data.currentUser.getSubItem(Data.currentUser.getListSize() - 1);
            //projectView.
            createProjectView(newProject);
            addProjectToMenu(newProject, projectsPane.getTabs().get(Data.currentUser.getListSize() - 1));
        }
    }

    private void addColumn() throws StringLengthException {
        String name = TextInputDialog.show("Add a new column", "Column name");

        if (name != null) {
            int currentTabIndex = getCurrentTabIndex();
            Project currentProject = getCurrentProject();
            currentProject.addSubItem(name);
            Column newColumn = currentProject.getSubItem(currentProject.getListSize() - 1);

            try {       // Could also clear the pane and reload
                ScrollPane scrollPane = (ScrollPane) projectsPane.getTabs().get(currentTabIndex).getContent();
                HBox hBox = (HBox) scrollPane.getContent();

                hBox.getChildren().add(/*projectView.*/createColumnView(newColumn));
            } catch (ClassCastException cce) {
                System.out.println(cce.getMessage());
            }
        }
    }

    private void renameProject() throws StringLengthException {
        String newName = TextInputDialog.show("Rename project", "Project title");

        if (newName != null) {
            int currentTabIndex = getCurrentTabIndex();
            Project currentProject = getCurrentProject();
            currentProject.setName(newName);
            projectsPane.getTabs().get(currentTabIndex).setText(currentProject.getName());
        }
    }

    @FXML
    protected void onEditProfileSelected() {
        try {
            EditProfileView.createEditProfileView();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @FXML
    protected void onLogoutMenuItemSelected() {
        try {
            LoginView.createLoginView();
            Stage stage = (Stage) mainExitButton.getScene().getWindow();
            stage.close();
            Data.currentUser = null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
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
            deleteBoardItem(Data.currentUser, getCurrentProject(), alertTitle);
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

    public void onDeleteColumnClicked(Column column) {
        String alertTitle = "Delete Column";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(alertTitle);
        alert.setHeaderText("You're about to delete column - " + column.getName());
        alert.setContentText("Press OK to delete, or cancel to return to Smart Board");

        if (alert.showAndWait().get() == ButtonType.OK) {
            Project currProject = getCurrentProject();
            deleteBoardItem(currProject, column, alertTitle);
        }
    }

    private void deleteBoardItem(BoardItem superItem, BoardItem itemToDelete, String alertTitle) {
        String itemName = itemToDelete.getName();
        if (superItem.removeSubItem(itemToDelete)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(alertTitle);
            alert.setHeaderText(itemName + " successfully deleted.");
            alert.showAndWait();

            if (superItem.getClass().getSimpleName().equals("User")) {
                removeProjectsFromMenu();
            }
            projectsPane.getTabs().clear();

            try {
                displayProjects();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(alertTitle);
            alert.setHeaderText("Error deleting - " + itemName);
            alert.showAndWait();
        }
    }

    @FXML
    @Override
    public void handleCloseButtonAction(@NotNull Event event) {
        Stage stage = (Stage) mainExitButton.getScene().getWindow();
        Alert exitAlert = new Alert(Alert.AlertType.CONFIRMATION);
        exitAlert.setTitle("Logout");
        exitAlert.setHeaderText("You're about to exit Smart Board!");
        exitAlert.setContentText("Press OK to exit, or cancel to return to Smart Board");

        if (exitAlert.showAndWait().get() == ButtonType.OK) {
            stage.close();
        }
    }

    private Quote getRandomQuote() {
        Random rand = new Random();
        int randomInt = rand.nextInt(Data.QUOTES.length);
        return Data.QUOTES[randomInt];
    }

    private int getCurrentTabIndex() {
        return projectsPane.getSelectionModel().getSelectedIndex();
    }

    private Project getCurrentProject() {
        return Data.currentUser.getSubItem(getCurrentTabIndex());
    }

}