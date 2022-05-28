/**
 * This is the controller class for the Task Editor view
 * It controls all aspects of the Task Editor view
 */

package com.smartboard.controller;

import com.smartboard.model.*;
import com.smartboard.view.TextInputDialog;
import com.smartboard.view.View_Utils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TaskEditorController implements Closable, Initializable {

    @FXML
    private CheckBox completedCheckbox;
    @FXML
    private ProgressBar checklistProgress;
    @FXML
    private TextField taskNameField;
    @FXML
    private RadioButton dueDateRadio;
    @FXML
    private DatePicker datePicker;
    @FXML
    private RadioButton checklistRadio;
    @FXML
    private VBox checklistBox;
    private ArrayList<ChecklistItem> checklistItemList = new ArrayList<>();
    @FXML
    private TextArea taskDescriptionField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Task_Utils.currentTask != null) {
            Task task = Task_Utils.currentTask;
            taskNameField.setText(task.getName());
            taskDescriptionField.setText(task.getDescription());
            completedCheckbox.setSelected(task.isCompleted());

            if (task.getDueDate() != null) {
                dueDateRadio.setSelected(true);
                setDatePicker();
            }

            if (task.getListSize() > 0) {
                checklistRadio.setSelected(true);
                checklistBox.visibleProperty().setValue(true);
                checklistItemList = task.getSubItemList();
                loadChecklistRows();
            }
        }
    }

    /**
     * Validates user data and creates a new Task from the data entered
     */
    @FXML
    private void onOkButtonClicked(Event event) {
        String alertMessage = "";
        boolean error = true;
        String taskName = taskNameField.getText().strip();
        LocalDate dueDate = datePicker.getValue();
        String description = taskDescriptionField.getText().strip();

        if (dueDate != null) {
            if (dueDate.isBefore(LocalDate.now())) {
                View_Utils.errorAlert("Due date is in the past - not updated");
            }
        }

        if (taskName.isBlank()) {
            alertMessage = "Please enter a task name.";
        } else {
            try {
                Task task;
                if (Task_Utils.currentTask == null) {
                    task = Column_Utils.currentColumn.addSubItem(taskName);
                    task.setOrderIndex(Column_Utils.currentColumn.getListSize() - 1);
                } else {
                    task = Task_Utils.currentTask;
                    task.setName(taskName);
                }

                error = false;

                if (!description.isBlank()) {
                    task.setDescription(description);
                }

                if (dueDate != null) {
                    task.setDueDate(dueDate);
                } else {
                    task.nullDueDate();
                }

                if (checklistRadio.isSelected()) {
                    task.replaceEntireChecklist(checklistItemList);
                    task.setCompleted(completedCheckbox.isSelected());
                } else {
                    if (Task_Utils.currentTask != null) {
                        Task_Utils.deleteTaskCLItems(task);
                    }
                }

                if (Task_Utils.currentTask == null) {
                    Task_Utils.addNewTask(task);
                } else {
                    DB_Utils.UpdateTask(task);
                }

                handleCloseButtonAction(event);

            } catch (StringLengthException sle) {
                alertMessage = sle.getMessage();
            }
        }

        if (error) {
            View_Utils.errorAlert(alertMessage);
        }

    }

    /**
     * Displays the Date Picker when the radio button is selected
     */
    @FXML
    private void toggleDueDate() {
        if (dueDateRadio.isSelected()) {
            setDatePicker();
        } else {
            datePicker.visibleProperty().setValue(false);
            datePicker.setValue(null);
        }
    }

    /**
     * Sets the value of the Date Picker
     */
    private void setDatePicker() {
        datePicker.visibleProperty().setValue(true);
        if (Task_Utils.currentTask != null) {
            datePicker.setValue(Task_Utils.currentTask.getDueDate());
        }
    }

    /**
     * Sets all Checklist items to checked when Completed checkbox is selected
     */
    @FXML
    private void onCompletedClicked() {
        for (ChecklistItem item : checklistItemList) {
            item.setChecked(completedCheckbox.isSelected());
        }

        reLoadChecklistRows();
    }

    /**
     * Displays the list of Checklist items when the radio button is selected
     */
    @FXML
    private void toggleChecklist() {
        checklistBox.visibleProperty().setValue(checklistRadio.isSelected());
    }

    /**
     * Sets the value of the checklist progress bar
     */
    private void setChecklistProgress() {
        double numChecklistCompleted = 0;

        for (ChecklistItem item : checklistItemList) {
            if (item.isChecked()) {
                numChecklistCompleted++;
            }
        }

        double progressNum = numChecklistCompleted / checklistItemList.size();
        boolean isComp = (progressNum == 1);

        completedCheckbox.setSelected(isComp);
        checklistProgress.setProgress(progressNum);
    }

    /**
     * Creates a new Checklist item and adds it to the list
     */
    @FXML
    private void addChecklistItem() {
        String description = TextInputDialog.show("Add a checklist item", "Description");

        if (!description.isBlank()) {
            checklistItemList.add(new ChecklistItem(description));
            reLoadChecklistRows();
        }
    }

    /**
     * Edits the description of the checklist item and refreshes the view
     */
    private void editChecklistItem(ChecklistItem editItem) {
        String description = TextInputDialog.show("Edit a checklist item", "New description");

        if (!description.isBlank()) {
            for (ChecklistItem item : checklistItemList) {
                if (item == editItem) {
                    item.setDescription(description);
                }
            }

            reLoadChecklistRows();
        }
    }

    /**
     * Deletes the checklist item and refreshes the view
     */
    private void deleteChecklistItem(ChecklistItem deleteItem) {

        for (int i = 0; i < checklistItemList.size(); i++) {
            if (checklistItemList.get(i) == deleteItem) {
                checklistItemList.remove(deleteItem);
            }
        }

        reLoadChecklistRows();
    }

    /**
     * Loads each Checklist item into the View
     */
    private void loadChecklistRows() {
        for (ChecklistItem item : checklistItemList) {
            checklistBox.getChildren().add(createChecklistRow(item));
        }
        setChecklistProgress();
    }

    /**
     * Removes and re-loads the checklist items into the View
     */
    private void reLoadChecklistRows() {
        checklistBox.getChildren().removeIf(node -> node instanceof HBox);
        loadChecklistRows();
    }

    /**
     * Creates and returns a Checklist item row from the item data
     */
    private @NotNull HBox createChecklistRow(@NotNull ChecklistItem checklistItem) {

        // Checklist checkbox
        CheckBox checkBox = new CheckBox(checklistItem.getDescription());
        checkBox.setSelected(checklistItem.isChecked());
        HBox.setMargin(checkBox, new Insets(4, 50, 0, 0));
        checkBox.setOnAction(actionEvent ->
                {
                    checklistItem.setChecked(checkBox.isSelected());
                    setChecklistProgress();
                }
        );
        HBox cBox = new HBox(checkBox);
        cBox.setMinWidth(200);

        // Checklist Edit button
        Hyperlink editLink = new Hyperlink("Edit");
        editLink.setStyle("-fx-underline: false");
        HBox.setMargin(editLink, new Insets(0, 10, 0, 0));
        editLink.setOnAction(actionEvent ->
                editChecklistItem(checklistItem)
        );

        // Checklist Delete button
        Hyperlink deleteLink = new Hyperlink("Delete");
        deleteLink.setStyle("-fx-underline: false");
        deleteLink.setOnAction(actionEvent ->
                deleteChecklistItem(checklistItem)
        );

        HBox rowBox = new HBox(cBox, editLink, deleteLink);
        rowBox.setPrefWidth(540);

        return rowBox;
    }

    /**
     * Closes the current stage.
     * Implemented from the Closable interface
     */
    @FXML
    @Override
    public void handleCloseButtonAction(@NotNull Event event) {
        Button button = (Button) event.getTarget();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
}
