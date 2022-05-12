package com.smartboard.controller;

import com.smartboard.model.*;
import com.smartboard.view.EditProfileView;
import com.smartboard.view.LoginView;
import com.smartboard.view.TaskEditorView;
import com.smartboard.view.TextInputDialog;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
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
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class SBController implements Closable, Initializable {

    @FXML
    private TabPane projectsPane;
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
    private final Font HEAD_FONT_SIZE = new Font(14);
    private String taskColor = "";

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

        displayProjects();
    }

    private void displayProjects() {
        int i = 0;
        for (Project project : Data.currentUser.getSubItemList()) {
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
        loadColumns(columnHolder, project);
        ScrollPane scrollPane = new ScrollPane(columnHolder);
        Tab projectTab = new Tab(project.getName(), scrollPane);
        projectsPane.getTabs().add(projectTab);
    }

    private void loadColumns(HBox columnHolder, Project project) {
        for (Column column : project.getSubItemList()) {
            columnHolder.getChildren().add(createColumnView(column));
        }
        // ALT project.getSubItemList().forEach(column -> hBox.getChildren().add(createColumnView(column)));
    }

    private void reLoadColumns() {
        try {
            ScrollPane scrollPane = (ScrollPane) projectsPane.getTabs().get(getCurrentTabIndex()).getContent();
            HBox hBox = (HBox) scrollPane.getContent();
            hBox.getChildren().clear();
            loadColumns(hBox, getCurrentProject());
        } catch (ClassCastException cce) {
            System.out.println(cce.getMessage());
        }
    }

    private VBox createColumnView(Column column) {
        int columnWidth = 310;
        int iconHeight = 17;

        // Column Header
        Button addTaskButton = new Button();
        ImageView addIcon = new ImageView(new Image("add_icon.png"));
        addIcon.preserveRatioProperty().setValue(true);
        addIcon.setFitHeight(iconHeight);
        addTaskButton.setGraphic(addIcon);
        addTaskButton.setOnAction(actionEvent -> {
            try {
                Data.currentColumn = column;
                Data.currentTask = null;
                TaskEditorView.createTaskEditorView("Add");
                reLoadColumns();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        });

        Button deleteButton = new Button();
        ImageView deleteIcon = new ImageView(new Image("delete_f_icon.png"));
        deleteIcon.preserveRatioProperty().setValue(true);
        deleteIcon.setFitHeight(iconHeight);
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setOnAction(
                actionEvent -> onDeleteColumnClicked(column)
        );

        Label columnLabel = new Label(column.getName());
        columnLabel.setFont(HEAD_FONT_SIZE);
        columnLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        columnLabel.setMaxWidth(200);
        columnLabel.setPrefWidth(200);

        ToolBar columnHeader = new ToolBar(addTaskButton, deleteButton, columnLabel);
        columnHeader.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        columnHeader.setStyle("-fx-background-color: lightblue; -fx-border-color: grey;");
        VBox.setMargin(columnHeader, new Insets(5));

        // Column
        VBox columnBox = new VBox(columnHeader);

        for (Task task : column.getSubItemList()) {
            columnBox.getChildren().add(createTaskView(column, task));
        }
        // ALT column.getSubItemList().forEach(task -> columnBox.getChildren().add(createTaskView(task)));

        columnBox.setSpacing(10);
        columnBox.setMaxWidth(columnWidth);
        columnBox.setMinWidth(columnWidth);

        return columnBox;
    }

    private AnchorPane createTaskView(Column column, Task task) {
        // Update Button
        Button updateButton = new Button("Update");
        updateButton.setPrefWidth(65);
        updateButton.setLayoutX(225);
        updateButton.setLayoutY(44);
        updateButton.setOnAction(actionEvent -> {
            try {
                Data.currentTask = task;
                TaskEditorView.createTaskEditorView("Update");
                reLoadColumns();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        });

        // Delete Button
        Button deleteButton = new Button("Delete");
        deleteButton.setPrefWidth(65);
        deleteButton.setLayoutX(225);
        deleteButton.setLayoutY(74);
        deleteButton.setOnAction(
                actionEvent -> onDeleteTaskClicked(column, task)
        );

        // Task name label
        Label taskNameLabel = new Label(task.getName());
        taskNameLabel.setFont(HEAD_FONT_SIZE);
        taskNameLabel.setLayoutX(14);
        taskNameLabel.setLayoutY(6);

        // Completed checkbox
        CheckBox completed = new CheckBox("Completed");
        completed.setSelected(task.isCompleted());
        completed.setLayoutX(14);
        completed.setLayoutY(46);
        completed.setOnAction(actionEvent ->
                {
                    task.setCompleted(completed.isSelected());
                    taskColor = getPaneColor(task);
                    reLoadColumns();
                }
        );

        // Checklist summary
        int numChecklistItems = task.getListSize();
        int completedItems = task.getNumChecklistCompleted();
        String summary = String.format("  %d/%d", completedItems, numChecklistItems);
        Label checklistLabel = new Label(summary);
        ImageView clImage = new ImageView(new Image("checklist_icon.png"));
        clImage.preserveRatioProperty().setValue(true);
        clImage.setFitHeight(22);
        checklistLabel.graphicProperty().setValue(clImage);
        checklistLabel.setLayoutX(14);
        checklistLabel.setLayoutY(86);

        // Due date label
        Label dueDateLabel = new Label("  Not set");
        ImageView ddImage = new ImageView(new Image("alarm_icon.png"));
        ddImage.preserveRatioProperty().setValue(true);
        ddImage.setFitHeight(22);
        dueDateLabel.graphicProperty().setValue(ddImage);
        if (task.getDueDate() != null) {
            dueDateLabel.setText("  " + task.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        dueDateLabel.setLayoutX(100);
        dueDateLabel.setLayoutY(86);

        // Task pane
        taskColor = getPaneColor(task);
        AnchorPane taskPane = new AnchorPane(updateButton, deleteButton, taskNameLabel, completed, checklistLabel, dueDateLabel);
        taskPane.setStyle("-fx-background-color: " + taskColor + "; -fx-border-color: grey;");
        VBox.setMargin(taskPane, new Insets(5));
        taskPane.paddingProperty().setValue(new Insets(5));

        return taskPane;
    }

    private String getPaneColor(Task task) {
        final String NOT_SET = "azure";
        final String APPROACHING = "khaki";
        final String OVERDUE = "orangered";
        final String COMPLETED = "lightgreen";
        final String COMPLETED_LATE = "lightpink";
        String retColor = NOT_SET;

        switch (task.getStatus()){
            case DATE_NOT_SET -> retColor = NOT_SET;
            case APPROACHING -> retColor = APPROACHING;
            case COMPLETED_ON_TIME -> retColor = COMPLETED;
            case OVERDUE -> retColor = OVERDUE;
            case COMPLETED_LATE -> retColor = COMPLETED_LATE;
        }

        return retColor;
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

        } catch (ClassCastException cce) {
            cce.printStackTrace();
        } catch (StringLengthException sle) {
            Utility.errorAlert(sle.getMessage());
        }
    }

    private void addProject() throws StringLengthException {
        String name = TextInputDialog.show("Create a new project", "Project title");

        if (name != null) {
            Project newProject = Data.currentUser.addSubItem(name);
            createProjectView(newProject);
            projectsPane.getSelectionModel().selectLast();
            addProjectToMenu(newProject, projectsPane.getTabs().get(Data.currentUser.getListSize() - 1));
        }
    }

    private void addColumn() throws StringLengthException {
        String name = TextInputDialog.show("Add a new column", "Column name");

        if (name != null) {
            Project currentProject = getCurrentProject();
            currentProject.addSubItem(name);

            reLoadColumns();
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
    private void onEditProfileSelected() {
        try {
            EditProfileView.createEditProfileView();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @FXML
    private void onLogoutMenuItemSelected() {
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
    private void toggleDefaultSetting() {
        Data.currentUser.toggleDefaultProject(getCurrentTabIndex());

        for (int i = 0; i < Data.currentUser.getListSize(); i++) {
            projectsPane.getTabs().get(i).setText(Data.currentUser.getSubItem(i).getName());
        }

    }

    @FXML
    private void onDeleteProjectSelected() {
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
    private void showAboutInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Smart Board");
        alert.setHeaderText("""
                Smart Board version 1.1
                Developer: Simon Mckindley
                Created for Further Programming A2
                May 2022""");
        alert.showAndWait();
    }

    private void onDeleteColumnClicked(Column column) {
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

    private void onDeleteTaskClicked(Column column, Task task) {
        String alertTitle = "Delete Task";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(alertTitle);
        alert.setHeaderText("You're about to delete task - " + task.getName());
        alert.setContentText("Press OK to delete, or cancel to return to Smart Board");

        if (alert.showAndWait().get() == ButtonType.OK) {
            deleteBoardItem(column, task, alertTitle);
        }
    }

    private void deleteBoardItem(BoardItem superItem, BoardItem itemToDelete, String alertTitle) {
        String deleteClass = itemToDelete.getClass().getSimpleName();
        String itemName = itemToDelete.getName();
        if (superItem.removeSubItem(itemToDelete)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(alertTitle);
            alert.setHeaderText(itemName + " successfully deleted.");
            alert.showAndWait();

            switch (deleteClass) {
                case "Project" -> {
                    removeProjectsFromMenu();
                    projectsPane.getTabs().clear();
                    displayProjects();
                }
                case "Column", "Task" -> reLoadColumns();
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
        Alert exitAlert = new Alert(Alert.AlertType.CONFIRMATION);
        exitAlert.setTitle("Logout");
        exitAlert.setHeaderText("You're about to exit Smart Board!");
        exitAlert.setContentText("Press OK to exit, or cancel to return to Smart Board");

        Button button = (Button) event.getTarget();
        Stage stage = (Stage) button.getScene().getWindow();

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