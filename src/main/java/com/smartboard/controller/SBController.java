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
import javafx.scene.Cursor;
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
    private double startX = 0;
    private double startY = 0;

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
        if (Data.currentUser.getListSize() == 0) {
            projectMenu.disableProperty().setValue(true);
        } else {
            projectMenu.disableProperty().setValue(false);
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
        columnLabel.setPadding(new Insets(0, 0, 0, 10));
        columnLabel.setMaxWidth(152);
        columnLabel.setPrefWidth(152);

        Button leftButton = new Button("<");
        leftButton.setOnAction(actionEvent -> {
            moveColumn(column, "<");
            reLoadColumns();
        });

        Button rightButton = new Button(">");
        rightButton.setOnAction(actionEvent -> {
            moveColumn(column, ">");
            reLoadColumns();
        });

        ToolBar columnHeader = new ToolBar(rightButton, addTaskButton, deleteButton, columnLabel, leftButton);
        columnHeader.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        columnHeader.setStyle("-fx-background-color: lightblue; -fx-border-color: grey;");
        VBox.setMargin(columnHeader, new Insets(5));

        // Column
        VBox columnBox = new VBox(columnHeader);

        for (Task task : column.getSubItemList()) {
            columnBox.getChildren().add(createTaskView(column, task));
        }

        columnBox.setSpacing(10);
        columnBox.setMinHeight(500);
        columnBox.setMaxWidth(columnWidth);
        columnBox.setMinWidth(columnWidth);

        // Credit https://jenkov.com/tutorials/javafx/drag-and-drop.html
        columnBox.setOnDragOver(event -> {
            if (event.getGestureSource() != columnBox && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        columnBox.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                event.setDropCompleted(true);
                moveTask(column, Data.currentColumn, Data.currentTask);
                reLoadColumns();
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });

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

        // Task pane`
        AnchorPane taskPane = new AnchorPane(updateButton, deleteButton, taskNameLabel, completed, checklistLabel, dueDateLabel);
        taskColor = getPaneColor(task);
        taskPane.setStyle("-fx-background-color: " + taskColor + "; -fx-border-color: grey;");
        VBox.setMargin(taskPane, new Insets(5));
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

//    private void draggable(Node node) {
//    System.out.println("draggable "+ node);
//        //Prompt the user that the node can be clicked
//
//
//        //Prompt the user that the node can be dragged
//        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
//            node.setCursor(Cursor.MOVE);
//
//            //When a press event occurs, the location coordinates of the event are cached
//            startX = event.getX();
//            startY = event.getY();
//        });
//        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> node.setCursor(Cursor.DEFAULT));
//
//        //Realize drag and drop function
//        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
//            double distanceX = event.getX() - startX;
//            double distanceY = event.getY() - startY;
//
//            double x = node.getLayoutX() + distanceX;
//            double y = node.getLayoutY() + distanceY;
//
//            //After calculating X and y, relocate the node to the specified coordinate point (x, y)
//            node.relocate(x, y);
//        });
//    }
//
//    private void makeDraggable(Node node) {
//        node.setOnMousePressed(mouseEvent -> {
//            startX = mouseEvent.getSceneX() - node.getTranslateX();
//            startY = mouseEvent.getSceneY() - node.getTranslateY();
//        });
//
//        node.setOnDragDetected(mouseEvent -> {
//            node.setTranslateX(mouseEvent.getSceneX() - startX);
//            node.setTranslateY(mouseEvent.getSceneY() - startY);
//        });
//    }

    private String getPaneColor(Task task) {
        final String NOT_SET = "azure";
        final String APPROACHING = "khaki";
        final String OVERDUE = "orangered";
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
            setProjectMenuDisable();
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

    private void moveColumn(Column column, String direction) {
        Project project = getCurrentProject();
        int colIndex = project.getSubItemIndexByObject(column);
        int minIndex = 0;
        int maxIndex = project.getListSize() - 1;

        switch (direction) {
            case "<" -> {
                if (colIndex > minIndex) {
                    Collections.swap(project.getSubItemList(), colIndex, colIndex - 1);
                }
            }
            case ">" -> {
                if (colIndex < maxIndex) {
                    Collections.swap(project.getSubItemList(), colIndex, colIndex + 1);
                }
            }
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

    private void moveTask(Column newColumn, Column currentColumn, Task task) {
        newColumn.getSubItemList().add(task);
        currentColumn.removeSubItem(task);
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
                Developer: Simon Mckindley
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