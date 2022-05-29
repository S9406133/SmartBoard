/**
 * This is the controller class for the main Smart Board view
 * It controls all aspects of the Smart Board view
 */

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

        toolbarImage.setImage(new Image(User_Utils.currentUser.getImagePath()));
        toolbarName.setText(User_Utils.currentUser.getFirstName() + " " + User_Utils.currentUser.getLastName());
        toolbarQuote.setText(Quotes_Utils.getRandomQuote().toString());

        displayProjects();

        setProjectMenuDisable();
    }

    /**
     * Adds all the users projects to the view
     */
    private void displayProjects() {
        int i = 0;
        for (Project project : User_Utils.currentUser.getSubItemList()) {
            createProjectView(project);
            if (project.isDefault()) {
                projectsPane.getSelectionModel().selectLast();
            }
            addProjectToMenu(project, projectsPane.getTabs().get(i));
            i++;
        }
    }

    /**
     * Disables the project menu option if the there are no projects displayed
     */
    private void setProjectMenuDisable() {
        projectMenu.disableProperty().setValue(User_Utils.currentUser.getListSize() == 0);
    }

    /**
     * Creates the project tab with the Column Boxes as contents
     */
    private void createProjectView(@NotNull Project project) {
        String projectName = (project.isDefault()) ? project.getName() + " #" : project.getName();
        HBox columnHolder = new HBox(5);
        loadColumns(columnHolder, project);
        ScrollPane scrollPane = new ScrollPane(columnHolder);
        Tab projectTab = new Tab(projectName, scrollPane);
        projectsPane.getTabs().add(projectTab);
    }

    /**
     * Loads each column box into the project columnholder HBox
     */
    private void loadColumns(HBox columnHolder, @NotNull Project project) {
        for (Column column : project.getSubItemList()) {
            columnHolder.getChildren().add(createColumnView(column));
        }
    }

    /**
     * Resets and reloads the columns
     */
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

    /**
     * Creates and returns the column VBox from the column data.
     */
    private @NotNull VBox createColumnView(@NotNull Column column) {
        int columnWidth = 310;
        int iconHeight = 17;

        // Add task button
        Button addTaskButton = new Button();
        ImageView addIcon = new ImageView(new Image("add_icon.png"));
        addIcon.preserveRatioProperty().setValue(true);
        addIcon.setFitHeight(iconHeight);
        addTaskButton.setGraphic(addIcon);
        addTaskButton.setTooltip(new Tooltip("Add a new task to this column"));
        addTaskButton.setOnAction(actionEvent -> {
            try {
                Column_Utils.currentColumn = column;
                Task_Utils.currentTask = null;
                TaskEditorView.createTaskEditorView("Add");
                reLoadColumns();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        });

        // Delete column button
        Button deleteButton = new Button();
        ImageView deleteIcon = new ImageView(new Image("delete_f_icon.png"));
        deleteIcon.preserveRatioProperty().setValue(true);
        deleteIcon.setFitHeight(iconHeight);
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setTooltip(new Tooltip("Delete this column"));
        deleteButton.setOnAction(
                actionEvent -> onDeleteColumnClicked(column)
        );

        // Column header label
        Label columnLabel = new Label(column.getName());
        columnLabel.setId("columnLabel");
        columnLabel.setFont(HEAD_FONT_SIZE);
        columnLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        columnLabel.setPadding(new Insets(0, 0, 0, 10));
        columnLabel.setMaxWidth(170);
        columnLabel.setPrefWidth(170);
        columnLabel.setOnMouseClicked(mouseEvent -> {
            Column_Utils.currentColumn = column;
            showInputDialog(mouseEvent);
        });

        // Move column buttons
        DirectionButton leftButton = new DirectionButton();
        leftButton.setToLeft();
        leftButton.setTooltip(new Tooltip("Move the column left"));
        leftButton.setOnAction(actionEvent -> {
            Controller_Utils.moveColumn(getCurrentProject(), column, leftButton.getDirection());
            reLoadColumns();
        });

        DirectionButton rightButton = new DirectionButton();
        rightButton.setToRight();
        rightButton.setTooltip(new Tooltip("Move the column right"));
        rightButton.setOnAction(actionEvent -> {
            Controller_Utils.moveColumn(getCurrentProject(), column, rightButton.getDirection());
            reLoadColumns();
        });

        // Column header
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

    /**
     * Creates and returns a Task AnchorPane from the task data
     */
    private @NotNull AnchorPane createTaskView(Column column, @NotNull Task task) {
        final String APPROACHING = "khaki";
        final String COMPLETED = "lightgreen";

        // Update Button
        Button updateButton = new Button("Update");
        updateButton.setPrefWidth(65);
        updateButton.setLayoutX(225);
        updateButton.setLayoutY(44);
        updateButton.setOnAction(actionEvent -> {
            try {
                Task_Utils.currentTask = task;
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
                    task.setAllChecklistItems(task.isCompleted());
                    dueDateColor = Controller_Utils.getStatusColor(task);
                    reLoadColumns();
                    DB_Utils.UpdateTask(task);
                    Task_Utils.refreshTaskCLItems(task.getTaskID(), task.getSubItemList());
                }
        );

        // Task move buttons
        DirectionButton upButton = new DirectionButton();
        upButton.setToUp();
        upButton.setLayoutX(235);
        upButton.setLayoutY(6);
        upButton.setTooltip(new Tooltip("Move up the column"));
        upButton.setOnAction(actionEvent -> {
            Column_Utils.currentColumn = column;
            Controller_Utils.moveTask(task, upButton.getDirection());
            reLoadColumns();
        });

        DirectionButton downButton = new DirectionButton();
        downButton.setToDown();
        downButton.setLayoutX(265);
        downButton.setLayoutY(6);
        downButton.setTooltip(new Tooltip("Move down the column"));
        downButton.setOnAction(actionEvent -> {
            Column_Utils.currentColumn = column;
            Controller_Utils.moveTask(task, downButton.getDirection());
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
        dueDateColor = Controller_Utils.getStatusColor(task);
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
            Column_Utils.currentColumn = column;
            Task_Utils.currentTask = task;
            content.putString(task.getName());
            db.setContent(content);
        });

        return taskPane;
    }

    /**
     * Makes the passed in column a droppable location
     */
    private void makeDroppable(@NotNull Node node, Column column) {
        node.setOnDragEntered(dragEvent -> {
            if (column != Column_Utils.currentColumn &&
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

                if (column != Column_Utils.currentColumn) {
                    Controller_Utils.moveTaskToNewColumn(getCurrentProject(), column, Column_Utils.currentColumn, Task_Utils.currentTask);
                    reLoadColumns();
                }
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });
    }

    /**
     * Creates a menuitem for the passed in project and adds it to the workspace menu
     */
    private void addProjectToMenu(@NotNull Project project, Tab tab) {
        MenuItem currItem = new MenuItem(project.getName());
        currItem.setId(project.getName());
        currItem.setOnAction(actionEvent -> projectsPane.getSelectionModel().select(tab));

        workspaceMenu.getItems().add(currItem);
    }

    /**
     * Removes all project menu items from the workspace menu
     */
    private void removeProjectsFromMenu() {
        workspaceMenu.getItems().removeIf(menuItem -> {
            if (menuItem.getId() != null) {
                return !menuItem.getId().equals("newProject");
            }
            return false;
        });
    }

    /**
     * Reloads the current projects to the workspace menu
     */
    private void reLoadWorkspaceMenu() {
        removeProjectsFromMenu();

        int i = 0;
        for (Project project : User_Utils.currentUser.getSubItemList()) {
            addProjectToMenu(project, projectsPane.getTabs().get(i));
            i++;
        }
    }

    /**
     * Calls a method to show an input dialog box depending on the event source
     */
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
            View_Utils.errorAlert(sle.getMessage());
        }
    }

    /**
     * Creates and displays a new project from the text entered
     *
     * @throws StringLengthException If name is less an 2 or greater than 30 characters
     */
    private void addProject() throws StringLengthException {
        String name = TextInputDialog.show("Create a new project", "Project title");

        if (!name.isBlank()) {
            Project newProject = Project_Utils.addNewProject(name);
            createProjectView(newProject);
            projectsPane.getSelectionModel().selectLast();
            addProjectToMenu(newProject, projectsPane.getTabs().get(User_Utils.currentUser.getListSize() - 1));
            setProjectMenuDisable();
        }
    }

    /**
     * Sets a new name for the current project and refreshes the view
     *
     * @throws StringLengthException If name is less an 2 or greater than 30 characters
     */
    private void renameProject() throws StringLengthException {
        String newName = TextInputDialog.show("Rename project", "Project title");

        if (!newName.isBlank()) {
            int currentTabIndex = getCurrentTabIndex();
            Project currentProject = getCurrentProject();
            Project_Utils.updateProjectName(currentProject, newName);
            projectsPane.getTabs().get(currentTabIndex).setText(currentProject.getName());
            reLoadWorkspaceMenu();
        }
    }

    /**
     * Toggles the default setting for the current project and refreshes the view
     */
    @FXML
    private void toggleDefaultSetting() {
        User_Utils.currentUser.toggleDefaultProject(getCurrentTabIndex());

        for (int i = 0; i < User_Utils.currentUser.getListSize(); i++) {
            String projectName = User_Utils.currentUser.getSubItem(i).getName();
            projectName = (User_Utils.currentUser.getSubItem(i).isDefault()) ? projectName + " #" : projectName;
            projectsPane.getTabs().get(i).setText(projectName);
        }

        reLoadWorkspaceMenu();
    }

    /**
     * Creates and displays a new column from the text entered
     *
     * @throws StringLengthException If name is less an 2 or greater than 30 characters
     */
    private void addColumn() throws StringLengthException {
        String name = TextInputDialog.show("Add a new column", "Column name");

        if (!name.isBlank()) {
            Column_Utils.addNewColumn(getCurrentProject(), name);
            reLoadColumns();
        }
    }

    /**
     * Sets a new name for the current column and refeshes the view
     *
     * @throws StringLengthException If name is less an 2 or greater than 30 characters
     */
    private void renameColumn() throws StringLengthException {
        String newName = TextInputDialog.show("Rename column", "Column title");

        if (!newName.isBlank()) {
            Column_Utils.updateColumnName(Column_Utils.currentColumn, newName);
            reLoadColumns();
        }
    }

    /**
     * Displays the Edit profile view
     */
    @FXML
    private void onEditProfileSelected() {
        try {
            EditProfileView.createEditProfileView();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Calls the methods to log out the current user
     */
    @FXML
    private void onLogoutSelected() {
        try {
            LoginView.createLoginView();
            Stage stage = (Stage) mainExitButton.getScene().getWindow();
            stage.close();
            User_Utils.logoutCurrentUser();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Displays a confirmation alert dialogue when delete project is selected.
     * Calls the method to delete if confirmed.
     */
    @FXML
    private void onDeleteProjectSelected() {
        ButtonType buttonType = View_Utils.deleteAlert(getCurrentProject().getName(), "Project");

        if (buttonType == ButtonType.OK) {
            deleteBoardItem(User_Utils.currentUser, getCurrentProject());
        }
    }

    /**
     * Displays a confirmation alert dialogue when delete column is selected.
     * Calls the method to delete if confirmed.
     */
    private void onDeleteColumnClicked(@NotNull Column column) {
        ButtonType buttonType = View_Utils.deleteAlert(column.getName(), "Column");

        if (buttonType == ButtonType.OK) {
            deleteBoardItem(getCurrentProject(), column);
        }
    }

    /**
     * Displays a confirmation alert dialogue when delete task is selected.
     * Calls the method to delete if confirmed.
     */
    private void onDeleteTaskClicked(Column column, @NotNull Task task) {
        ButtonType buttonType = View_Utils.deleteAlert(task.getName(), "Task");

        if (buttonType == ButtonType.OK) {
            deleteBoardItem(column, task);
        }
    }

    /**
     * Method to delete a generic BoardItem and refresh the view
     */
    private void deleteBoardItem(BoardItem<?> superItem, @NotNull BoardItem<?> itemToDelete) {
        String deleteClass = itemToDelete.getClass().getSimpleName();
        String alertTitle = "Delete " + deleteClass;
        String itemName = itemToDelete.getName();
        int itemID = -1;
        switch (deleteClass) {
            case "Project" -> itemID = ((Project) itemToDelete).getProjectID();
            case "Column" -> itemID = ((Column) itemToDelete).getColumnID();
            case "Task" -> itemID = ((Task) itemToDelete).getTaskID();
        }

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
                    DB_Utils.DeleteItem(deleteClass, deleteClass + "ID", itemID);
                }
                case "Column" -> {
                    reLoadColumns();
                    Column_Utils.deleteColumn((Project) superItem, itemID);
                }
                case "Task" -> {
                    reLoadColumns();
                    Task_Utils.deleteTask((Column) superItem, deleteClass, itemID);
                }
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(alertTitle);
            alert.setHeaderText("Error deleting - " + itemName);
            alert.showAndWait();
        }
    }

    /**
     * Closes the current stage.
     * Implemented from the Closable interface
     */
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

    /**
     * Displays an Information dialogue
     */
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

    /**
     * Displays an Information dialogue
     */
    @FXML
    private void showAboutInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Smart Board");
        alert.setHeaderText("""
                        ** Projects **
                - Add Projects to the workspace by selecting 'New Project' in the Workspace menu.
                - The current Project can be Renamed or deleted by selecting the relevant option in the Project menu.
                - A Project can also be set/unset as default in the Project menu.
                The default Project is the Project selected when logging-in.
                        ** Columns **
                - Columns can be added to a Project by selecting 'Add Column' in the Project menu.
                - Columns can be renamed by clicking on blank space in the Column header.
                - Columns can be moved left or right within the Project by
                clicking the left '<' or right '>' buttons in the column header.
                - A Column can be deleted by clicking the delete button in the Column header
                        ** Tasks **
                - Task cards can be added to a Column by clicking on the '+' button in the Column header.
                - The task editor will be displayed, which will allow you to set the Task name, a due date,\040
                a description, add a checklist or set the Task as 'Completed'.
                - Once created a Task can be edited by clicking the 'Update' button on the Task card.
                - A Task can be deleted by clicking the 'Delete' button on the Task card.
                        ** User **
                - Your user profile can be edited by selecting 'Edit Profile' in the User menu,
                or by Clicking on your profile info in the bottom info bar.""");
        alert.showAndWait();
    }

    /**
     * Returns the currently selected Tab index
     */
    private int getCurrentTabIndex() {
        return projectsPane.getSelectionModel().getSelectedIndex();
    }

    /**
     * Returns the current selected Project
     */
    public Project getCurrentProject() {
        return User_Utils.currentUser.getSubItem(getCurrentTabIndex());
    }

}