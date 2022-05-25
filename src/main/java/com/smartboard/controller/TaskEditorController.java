package com.smartboard.controller;

import com.smartboard.model.*;
import com.smartboard.view.TextInputDialog;
import com.smartboard.view.Utility;
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
                checklistItemList = task.getSubItemList();
                System.out.println("Init: "+checklistItemList.size());
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
        String description = taskDescriptionField.getText().strip();

        if (dueDate != null) {
            if (dueDate.isBefore(LocalDate.now())) {
                Utility.errorAlert("Due date is in the past - not updated");
            }
        }

        if (taskName.isBlank()) {
            alertMessage = "Please enter a task name.";
        } else {
            try {
                Task task;
                if (Data.currentTask == null) {
                    task = Data.currentColumn.addSubItem(taskName);
                    task.setOrderIndex(Data.currentColumn.getListSize() - 1);
                    //task.setCompleted(completedCheckbox.isSelected());
                } else {
                    task = Data.currentTask;
                    task.setName(taskName);
                }

                error = false;

                task.setCompleted(completedCheckbox.isSelected());

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
                } else {
                    task.getSubItemList().clear();
                }

                if (Data.currentTask == null) {
                    DB_Utils.InsertNewTask(Data.currentColumn, task);
                } else {
                    DB_Utils.UpdateTask(task);
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
    private void onCompletedClicked() {
//        if (Data.currentTask != null) {
//            Data.currentTask.setCompleted(completedCheckbox.isSelected());
//        } else {
            for (ChecklistItem item : checklistItemList) {
                item.setChecked(completedCheckbox.isSelected());
            }
//        }
        reLoadChecklistRows();
    }

    @FXML
    private void toggleChecklist() {
        checklistBox.visibleProperty().setValue(checklistRadio.isSelected());
    }

    private void setChecklistProgress() {
        double numChecklistCompleted = 0;

        for (ChecklistItem item : checklistItemList) {
            System.out.println("Loop:"+item.isChecked());
            if (item.isChecked()) {
                item.setChecked(true);
                numChecklistCompleted++;
            }
        }

        double progressNum = numChecklistCompleted / checklistItemList.size();
        System.out.println("Comp:"+numChecklistCompleted+"/ Size:"+checklistItemList.size()+" = "+progressNum);

        boolean isComp = (progressNum == 1);
        completedCheckbox.setSelected(isComp);
        Data.currentTask.setCompleted(isComp);

        checklistProgress.setProgress(progressNum);
    }

    @FXML
    private void addChecklistItem() {
        String description = TextInputDialog.show("Add a checklist item", "Description");

        if (!description.isBlank()) {
            checklistItemList.add(new ChecklistItem(description));
            reLoadChecklistRows();
        }
    }

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
        setChecklistProgress();
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
                {
                    checklistItem.setChecked(checkBox.isSelected());
                    System.out.println("Ch: "+checklistItem.isChecked());
                    setChecklistProgress();
                }
        );
        HBox cBox = new HBox(checkBox);
        cBox.setMinWidth(200);

        Hyperlink editLink = new Hyperlink("Edit");
        editLink.setStyle("-fx-underline: false");
        HBox.setMargin(editLink, new Insets(0, 10, 0, 0));
        editLink.setOnAction(actionEvent ->
                editChecklistItem(checklistItem)
        );

        Hyperlink deleteLink = new Hyperlink("Delete");
        deleteLink.setStyle("-fx-underline: false");
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
