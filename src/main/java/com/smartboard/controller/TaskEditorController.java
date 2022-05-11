package com.smartboard.controller;

import com.smartboard.model.ChecklistItem;
import com.smartboard.model.Data;
import com.smartboard.model.StringLengthException;
import com.smartboard.model.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
                System.out.println("checklist: " + task.getListSize());
                // Todo show checklist
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
                //TODO set values
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

                System.out.println("name " + task.getName() + "\ndate: " + task.getDueDate() + "\ndescr: " + task.getDescription());

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
    @Override
    public void handleCloseButtonAction(@NotNull Event event) {
        Button button = (Button) event.getTarget();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
}
