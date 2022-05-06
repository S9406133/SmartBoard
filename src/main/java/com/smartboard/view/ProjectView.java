package com.smartboard.view;

import com.smartboard.controller.SBController;
import com.smartboard.model.Column;
import com.smartboard.model.Project;
import com.smartboard.model.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ProjectView extends Pane {

    private final TabPane tabPane;

    public ProjectView(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public void createProjectView(Project project) {
        HBox hBox = new HBox();

        for (Column column : project.getSubItemList()) {
            hBox.getChildren().add(createColumnView(column));
        }
        // ALT project.getSubItemList().forEach(column -> hBox.getChildren().add(createColumnView(column)));

        ScrollPane scrollPane = new ScrollPane(hBox);

        Tab projectTab = new Tab(project.getName(), scrollPane);

        tabPane.getTabs().add(projectTab);
    }

    public VBox createColumnView(Column column) {
        // Column Header
        Button addTaskButton = new Button("Add Task");
        Button deleteButton = new Button();
        ImageView deleteIcon = new ImageView(new Image("delete_icon.png"));
        deleteIcon.preserveRatioProperty().setValue(true);
        deleteIcon.setFitHeight(17);
        deleteButton.setGraphic(deleteIcon);
        Label columnLabel = new Label(column.getName());
        columnLabel.setFont(new Font(14));
        columnLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        columnLabel.setPrefWidth(180);
        ToolBar columnHeader = new ToolBar(addTaskButton, deleteButton, columnLabel);
        columnHeader.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        columnHeader.setStyle("-fx-background-color: lightblue; -fx-border-color: grey;");
        VBox.setMargin(columnHeader, new Insets(5));

        // Column
        VBox columnBox = new VBox(columnHeader);

        for (Task task : column.getSubItemList()) {
            columnBox.getChildren().add(createTaskView(task));
        }
        // ALT column.getSubItemList().forEach(task -> columnBox.getChildren().add(createTaskView(task)));

        columnBox.setSpacing(10);
        columnBox.setPrefWidth(300);

        return columnBox;
    }

    public AnchorPane createTaskView(Task task) {
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
        AnchorPane taskPane = new AnchorPane(update, delete, taskName, checklist, dueDate);
        taskPane.prefWidth(300);
        taskPane.setStyle("-fx-background-color: lightgreen; -fx-border-color: grey;");
        VBox.setMargin(taskPane, new Insets(5));
        taskPane.paddingProperty().setValue(new Insets(5));

        return taskPane;
    }

    //TODO
    private String getFormattedDate(LocalDateTime dateTime) {
        String date = String.format("");

        return date;
    }

    //TODO
    private String getChecklistSummary(ArrayList<String> checklist) {

        return "";
    }

}
