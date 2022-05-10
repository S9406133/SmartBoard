package com.smartboard.controller;

import com.smartboard.model.Data;
import com.smartboard.model.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ResourceBundle;

public class TaskEditorController implements Closable, Initializable {

    @FXML
    private TextField taskName;
    @FXML
    private RadioButton dueDateRadio;
    @FXML
    private DatePicker datePicker;
    @FXML
    private RadioButton checklistRadio;
    @FXML
    private VBox checklistBox;
    @FXML
    private TextArea taskDescription;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Data.currentTask != null) {
            Task task = Data.currentTask;
            taskName.setText(task.getName());
            taskDescription.setText(task.getDescription());

            if (task.getDueDate() != null) {
                setDatePicker();
            }
        }

    }

    @FXML
    private void toggleDueDate() {
        if (dueDateRadio.isSelected()){
            setDatePicker();
        } else {
            datePicker.visibleProperty().setValue(false);
            datePicker.setValue(null);
        }
    }

    private void setDatePicker(){
        datePicker.visibleProperty().setValue(true);
        if (Data.currentTask != null) {
            datePicker.setValue(Data.currentTask.getDueDate());
        }
    }

    @FXML
    @Override
    public void handleCloseButtonAction(@NotNull Event event) {
        Button button = (Button) event.getTarget();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
}
