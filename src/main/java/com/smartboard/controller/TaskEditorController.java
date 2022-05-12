package com.smartboard.controller;

import com.smartboard.model.ChecklistItem;
import com.smartboard.model.Data;
import com.smartboard.model.StringLengthException;
import com.smartboard.model.Task;
import com.smartboard.view.TextInputDialog;
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
    public CheckBox completedCheckbox;
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
        if (Data.currentTask != null) {
            Task task = Data.currentTask;
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
                checklistItemList = (ArrayList<ChecklistItem>) task.getSubItemList();
                loadChecklistRows();
            }
        }
    }

    @FXML
    private void onOkButtonClicked(Event event) {
        String alertMessage = "";
        boolean error = true;
        String taskName = taskNameField.getText().strip();
        LocalDate dueDate = datePicker.getValue();

        if (dueDate != null) {
            if (dueDate.isBefore(LocalDate.now())) {
                alertMessage = "Due date is in the past - not updated";
                Utility.errorAlert(alertMessage);
            }
        }

        if (taskName.isBlank()) {
            alertMessage = "Please enter a task name.";
        } else {
            try {
                Task task;
                if (Data.currentTask == null) {
                    task = Data.currentColumn.addSubItem(taskName);
                } else {
                    task = Data.currentTask;
                    task.setName(taskName);
                }
                error = false;
                task.setDescription(taskDescriptionField.getText().strip());
                task.setCompleted(completedCheckbox.isSelected());

                if (dueDate != null) {
                    task.setDueDate(dueDate);
                } else {
                    task.nullDueDate();
                }

                if (checklistRadio.isSelected()) {
                    task.replaceEntireChecklist(checklistItemList);
                } else {
                    task.getSubItemList().clear();
                }

                handleCloseButtonAction(event);

            } catch (StringLengthException sle) {
                alertMessage = sle.getMessage();
            }
        }

        if (error) {
            Utility.errorAlert(alertMessage);
        }

    }

    @FXML
    private void toggleDueDate() {
        if (dueDateRadio.isSelected()) {
            setDatePicker();
        } else {
            datePicker.visibleProperty().setValue(false);
            datePicker.setValue(null);
        }
    }

    private void setDatePicker() {
        datePicker.visibleProperty().setValue(true);
        if (Data.currentTask != null) {
            datePicker.setValue(Data.currentTask.getDueDate());
        }
    }

    @FXML
    private void toggleChecklist() {
        checklistBox.visibleProperty().setValue(checklistRadio.isSelected());
    }

    @FXML
    private void addChecklistItem() {
        String description = TextInputDialog.show("Add a checklist item", "Description");

        if (description != null) {
            checklistItemList.add(new ChecklistItem(description));
            reLoadChecklistRows();
        }
    }

    private void editChecklistItem(ChecklistItem editItem) {
        String description = TextInputDialog.show("Edit a checklist item", "New description");

        if (description != null) {
            for (ChecklistItem item : checklistItemList) {
                if (item == editItem) {
                    item.setDescription(description);
                }
            }

            reLoadChecklistRows();
        }
    }

    private void deleteChecklistItem(ChecklistItem deleteItem) {

        for (int i = 0; i < checklistItemList.size(); i++) {
            if (checklistItemList.get(i) == deleteItem) {
                checklistItemList.remove(deleteItem);
            }
        }

        reLoadChecklistRows();
    }

    private void loadChecklistRows() {
        for (ChecklistItem item : checklistItemList) {
            checklistBox.getChildren().add(createChecklistRow(item));
        }
    }

    private void reLoadChecklistRows() {
        checklistBox.getChildren().removeIf(node -> node instanceof HBox);
        loadChecklistRows();
    }

    private HBox createChecklistRow(ChecklistItem checklistItem) {

        CheckBox checkBox = new CheckBox(checklistItem.getDescription());
        checkBox.setSelected(checklistItem.isChecked());
        HBox.setMargin(checkBox, new Insets(4, 50, 0, 0));
        checkBox.setOnAction(actionEvent ->
                checklistItem.setChecked(checkBox.isSelected())
        );
        HBox cBox = new HBox(checkBox);
        cBox.setMinWidth(200);

        Hyperlink editLink = new Hyperlink("Edit");
        HBox.setMargin(editLink, new Insets(0, 10, 0, 0));
        editLink.setOnAction(actionEvent ->
                editChecklistItem(checklistItem)
        );

        Hyperlink deleteLink = new Hyperlink("Delete");
        deleteLink.setOnAction(actionEvent ->
                deleteChecklistItem(checklistItem)
        );

        HBox rowBox = new HBox(cBox, editLink, deleteLink);
        rowBox.setPrefWidth(540);

        return rowBox;
    }

    @FXML
    @Override
    public void handleCloseButtonAction(@NotNull Event event) {
        Button button = (Button) event.getTarget();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
}
