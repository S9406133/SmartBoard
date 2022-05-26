package com.smartboard.controller;

import com.smartboard.model.*;
import com.smartboard.view.*;
import com.smartboard.view.TextInputDialog;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.ResourceBundle;

public class SBController implements Closable, Initializable {

    @FXML
    private TabPane projectsPane;
    @FXML
    private Menu workspaceMenu;
    @FXML
    private Menu projectMenu;
    @FXML
    private Button mainExitButton;
    @FXML
    private Label toolbarQuote;
    @FXML
    private ImageView toolbarImage;
    public static ImageView staticToolbarImage;
    @FXML
    private Label toolbarName;
    public static Label staticToolbarName;
    private final Font HEAD_FONT_SIZE = new Font(14);
    private String dueDateColor = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        staticToolbarImage = toolbarImage;
        staticToolbarName = toolbarName;

        toolbarImage.setImage(new Image(Data.currentUser.getImagePath()));
        toolbarName.setText(Data.currentUser.getFirstName() + " " + Data.currentUser.getLastName());
        toolbarQuote.setText(getRandomQuote().toString());

        displayProjects();

        setProjectMenuDisable();
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

    private void setProjectMenuDisable() {
        projectMenu.disableProperty().setValue(Data.currentUser.getListSize() == 0);
    }

    private void createProjectView(Project project) {
        String projectName = (project.isDefault())? project.getName() + " #" : project.getName();
        HBox columnHolder = new HBox(5);
        loadColumns(columnHolder, project);
        ScrollPane scrollPane = new ScrollPane(columnHolder);
        Tab projectTab = new Tab(projectName, scrollPane);
        projectsPane.getTabs().add(projectTab);
    }

    private void loadColumns(HBox columnHolder, Project project) {
        for (Column column : project.getSubItemList()) {
            columnHolder.getChildren().add(createColumnView(column));
        }
    }

    private void reLoadColumns() {
        try {
            ScrollPane scrollPane = (ScrollPane) projectsPane.getTabs().get(getCurrentTabIndex()).getContent();
            HBox hBox = (HBox) scrollPane.getContent();
            hBox.getChildren().clear();
            loadColumns(hBox, getCurrentProject());
        } catch (ClassCastException cce) {
            cce.printStackTrace();
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
        addTaskButton.setTooltip(new Tooltip("Add a new task to this column"));
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
        deleteButton.setTooltip(new Tooltip("Delete this column"));
        deleteButton.setOnAction(
                actionEvent -> onDeleteColumnClicked(column)
        );

        Label columnLabel = new Label(column.getName());
        columnLabel.setId("columnLabel");
        columnLabel.setFont(HEAD_FONT_SIZE);
        columnLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        columnLabel.setPadding(new Insets(0, 0, 0, 10));
        columnLabel.setMaxWidth(170);
        columnLabel.setPrefWidth(170);
        columnLabel.setOnMouseClicked(mouseEvent -> {
            Data.currentColumn = column;
            showInputDialog(mouseEvent);
        });

        DirectionButton leftButton = new DirectionButton();
        leftButton.setToLeft();
        leftButton.setTooltip(new Tooltip("Move the column left"));
        leftButton.setOnAction(actionEvent -> {
            moveColumn(column, leftButton.getDirection());
            reLoadColumns();
        });

        DirectionButton rightButton = new DirectionButton();
        rightButton.setToRight();
        rightButton.setTooltip(new Tooltip("Move the column right"));
        rightButton.setOnAction(actionEvent -> {
            moveColumn(column, rightButton.getDirection());
            reLoadColumns();
        });

        ToolBar columnHeader = new ToolBar(rightButton, addTaskButton, deleteButton, columnLabel, leftButton);
        columnHeader.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        columnHeader.setStyle("-fx-background-color: ghostwhite; -fx-border-color: grey;");
        VBox.setMargin(columnHeader, new Insets(5));

        // Column
        VBox columnBox = new VBox(columnHeader);

        for (Task task : column.getSubItemList()) {
            columnBox.getChildren().add(createTaskView(column, task));
        }

        columnBox.setSpacing(5);
        columnBox.setMinHeight(510);
        columnBox.setMaxWidth(columnWidth);
        columnBox.setMinWidth(columnWidth);
        columnBox.setStyle("-fx-background-color: steelblue;");

        makeDroppable(columnBox, column);

        return columnBox;
    }

    private AnchorPane createTaskView(Column column, Task task) {
        final String APPROACHING = "khaki";
        final String COMPLETED = "lightgreen";

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
        taskNameLabel.setMaxWidth(200);
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
                    dueDateColor = getStatusColor(task);
                    reLoadColumns();
                }
        );

        DirectionButton upButton = new DirectionButton();
        upButton.setToUp();
        upButton.setLayoutX(235);
        upButton.setLayoutY(6);
        upButton.setTooltip(new Tooltip("Move up the column"));
        upButton.setOnAction(actionEvent -> {
            Data.currentColumn = column;
            moveTask(task, upButton.getDirection());
            reLoadColumns();
        });

        DirectionButton downButton = new DirectionButton();
        downButton.setToDown();
        downButton.setLayoutX(265);
        downButton.setLayoutY(6);
        downButton.setTooltip(new Tooltip("Move down the column"));
        downButton.setOnAction(actionEvent -> {
            Data.currentColumn = column;
            moveTask(task, downButton.getDirection());
            reLoadColumns();
        });

        // Checklist summary
        int numChecklistItems = task.getListSize();
        int completedItems = task.getNumChecklistCompleted();
        String clLabelColor = (numChecklistItems == completedItems) ? COMPLETED : APPROACHING;
        String summary = String.format("  %d/%d", completedItems, numChecklistItems);
        Label checklistLabel = new Label(summary);
        checklistLabel.setVisible(numChecklistItems > 0);
        ImageView clImage = new ImageView(new Image("checklist_icon.png"));
        clImage.preserveRatioProperty().setValue(true);
        clImage.setFitHeight(22);
        checklistLabel.graphicProperty().setValue(clImage);
        checklistLabel.paddingProperty().setValue(new Insets(0, 5, 0, 5));
        checklistLabel.setStyle("-fx-background-color: " + clLabelColor + "; -fx-border-color: lightgrey;");
        checklistLabel.setLayoutX(14);
        checklistLabel.setLayoutY(86);

        // Due date label
        dueDateColor = getStatusColor(task);
        Label dueDateLabel = new Label("  Not set");
        ImageView ddImage = new ImageView(new Image("alarm_icon.png"));
        ddImage.preserveRatioProperty().setValue(true);
        ddImage.setFitHeight(22);
        dueDateLabel.graphicProperty().setValue(ddImage);
        dueDateLabel.paddingProperty().setValue(new Insets(0, 5, 0, 5));
        dueDateLabel.setStyle("-fx-background-color: " + dueDateColor + "; -fx-border-color: lightgrey;");
        if (task.getDueDate() != null) {
            dueDateLabel.setText("  " + task.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        dueDateLabel.setLayoutX(100);
        dueDateLabel.setLayoutY(86);

        // Task pane`
        AnchorPane taskPane = new AnchorPane(
                updateButton, deleteButton, taskNameLabel, upButton, downButton, completed, checklistLabel, dueDateLabel);
        taskPane.setStyle("-fx-background-color: azure; -fx-border-color: grey;");
        VBox.setMargin(taskPane, new Insets(0, 5, 0, 5));
        taskPane.paddingProperty().setValue(new Insets(5));
        taskPane.setCursor(Cursor.HAND);

        // Credit https://jenkov.com/tutorials/javafx/drag-and-drop.html
        taskPane.setOnDragDetected((MouseEvent event) -> {
            Dragboard db = taskPane.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            Data.currentColumn = column;
            Data.currentTask = task;
            content.putString(task.getName());
            db.setContent(content);
        });

        return taskPane;
    }

    private void makeDroppable(Node node, Column column) {
        node.setOnDragEntered(dragEvent -> {
            if (dragEvent.getGestureSource() != node &&
                    dragEvent.getDragboard().hasString()) {
                node.setStyle("-fx-background-color: lightblue;");
            }
            dragEvent.consume();
        });

        node.setOnDragExited(dragEvent -> {
            node.setStyle("-fx-background-color: steelblue;");
            dragEvent.consume();
        });

        // Credit https://jenkov.com/tutorials/javafx/drag-and-drop.html
        node.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        node.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                event.setDropCompleted(true);

                if (column != Data.currentColumn) {
                    moveTaskToNewColumn(column, Data.currentColumn, Data.currentTask);
                    reLoadColumns();
                }
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });
    }

    private String getStatusColor(Task task) {
        final String NOT_SET = "azure";
        final String APPROACHING = "khaki";
        final String OVERDUE = "red";
        final String COMPLETED = "lightgreen";
        final String COMPLETED_LATE = "lightpink";
        String retColor = NOT_SET;

        switch (task.getStatus()) {
            case DATE_NOT_SET -> retColor = NOT_SET;
            case APPROACHING -> retColor = APPROACHING;
            case COMPLETED_ON_TIME -> retColor = COMPLETED;
            case OVERDUE -> retColor = OVERDUE;
            case COMPLETED_LATE -> retColor = COMPLETED_LATE;
        }

        return retColor;
    }

    private void addProjectToMenu(@NotNull Project project, Tab tab) {
        MenuItem currItem = new MenuItem(project.getName());
        currItem.setId(project.getName());
        currItem.setOnAction(actionEvent -> projectsPane.getSelectionModel().select(tab));

        workspaceMenu.getItems().add(currItem);
    }

    private void removeProjectsFromMenu() {
        workspaceMenu.getItems().removeIf(menuItem -> {
            if (menuItem.getId() != null) {
                return !menuItem.getId().equals("newProject");
            }
            return false;
        });
    }

    private void reLoadWorkspaceMenu() {
        removeProjectsFromMenu();

        int i = 0;
        for (Project project : Data.currentUser.getSubItemList()) {
            addProjectToMenu(project, projectsPane.getTabs().get(i));
            i++;
        }
    }

    @FXML
    private void showInputDialog(@NotNull Event event) {
        try {
            EventTarget target = event.getTarget();
            String id = "";

            if (target instanceof MenuItem) {
                id = ((MenuItem) target).getId();
            } else if (target instanceof Label) {
                id = ((Label) target).getId();
            }

            switch (id) {
                case "newProject" -> addProject();
                case "newColumn" -> addColumn();
                case "renameProject" -> renameProject();
                case "columnLabel" -> renameColumn();
            }

        } catch (ClassCastException cce) {
            cce.printStackTrace();
        } catch (StringLengthException sle) {
            Utility.errorAlert(sle.getMessage());
        }
    }

    private void addProject() throws StringLengthException {
        String name = TextInputDialog.show("Create a new project", "Project title");

        if (!name.isBlank()) {
            Project newProject = Data.addNewProject(name); // Data.currentUser.addSubItem(name);
            createProjectView(newProject);
            projectsPane.getSelectionModel().selectLast();
            addProjectToMenu(newProject, projectsPane.getTabs().get(Data.currentUser.getListSize() - 1));
            setProjectMenuDisable();
        }
    }

    private void addColumn() throws StringLengthException {
        String name = TextInputDialog.show("Add a new column", "Column name");

        if (!name.isBlank()) {
            Column newColumn = Data.addNewColumn(getCurrentProject(), name); // getCurrentProject().addSubItem(name);
            newColumn.setOrderIndex(getCurrentProject().getSubItemIndex(newColumn));
            reLoadColumns();
        }
    }

    private void renameProject() throws StringLengthException {
        String newName = TextInputDialog.show("Rename project", "Project title");

        if (!newName.isBlank()) {
            int currentTabIndex = getCurrentTabIndex();
            Project currentProject = getCurrentProject();
            //currentProject.setName(newName);
            Data.updateProjectName(currentProject, newName);
            projectsPane.getTabs().get(currentTabIndex).setText(currentProject.getName());
            reLoadWorkspaceMenu();
        }
    }

    private void renameColumn() throws StringLengthException {
        String newName = TextInputDialog.show("Rename column", "Column title");

        if (!newName.isBlank()) {
            Data.updateColumnName(Data.currentColumn, newName);
            //Data.currentColumn.setName(newName);
            reLoadColumns();
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
    private void onLogoutSelected() {
        try {
            LoginView.createLoginView();
            Stage stage = (Stage) mainExitButton.getScene().getWindow();
            stage.close();
            Data.logoutCurrentUser();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @FXML
    private void toggleDefaultSetting() {
        Data.currentUser.toggleDefaultProject(getCurrentTabIndex());

        for (int i = 0; i < Data.currentUser.getListSize(); i++) {
            String projectName = Data.currentUser.getSubItem(i).getName();
            projectName = (Data.currentUser.getSubItem(i).isDefault())? projectName + " #" : projectName;
            projectsPane.getTabs().get(i).setText(projectName);
        }

        reLoadWorkspaceMenu();
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

    private void deleteBoardItem(BoardItem<?> superItem, BoardItem<?> itemToDelete, String alertTitle) {
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
                    setProjectMenuDisable();
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

    private void moveTaskToNewColumn(Column newColumn, Column currentColumn, Task task) {
        newColumn.getSubItemList().add(task);
        currentColumn.removeSubItem(task);
    }

    private void moveColumn(Column column, String direction) {
        Project project = getCurrentProject();
        int colIndex = project.getSubItemIndex(column);

        moveItem(project.getSubItemList(), colIndex, direction);
    }

    private void moveTask(Task task, String direction) {
        int taskIndex = Data.currentColumn.getSubItemIndex(task);

        moveItem(Data.currentColumn.getSubItemList(), taskIndex, direction);
    }

    private void moveItem(ArrayList<?> itemList, int itemIndex, String direction) {
        int minIndex = 0;
        int maxIndex = itemList.size() - 1;

        switch (direction) {
            case "left", "up" -> {
                if (itemIndex > minIndex) {
                    Collections.swap(itemList, itemIndex, itemIndex - 1);
                }
            }
            case "right", "down" -> {
                if (itemIndex < maxIndex) {
                    Collections.swap(itemList, itemIndex, itemIndex + 1);
                }
            }
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

    @FXML
    private void showVersionInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Smart Board Version");
        alert.setHeaderText("""
                Smart Board version 1.1
                Developer: Simon Mckindley S9406133
                Created for Further Programming A2
                May 2022""");
        alert.showAndWait();
    }

    @FXML
    private void showAboutInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Smart Board");
        alert.setHeaderText("""
                Include instructions here""");
        alert.showAndWait();
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