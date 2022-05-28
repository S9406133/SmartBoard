/**
 * This is the view creating class for the Task Editor stage
 */

package com.smartboard.view;

import com.smartboard.SmartBoard;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class TaskEditorView {

    public static void createTaskEditorView(String title) throws IOException {
        String fxmlName = "taskeditor.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(SmartBoard.class.getResource(fxmlName));
        Scene taskScene = new Scene(fxmlLoader.load());
        Stage taskStage = new Stage(StageStyle.UTILITY);
        taskStage.initModality(Modality.APPLICATION_MODAL);
        taskStage.setTitle("Task editor - " + title + " task");
        taskStage.getIcons().add(SmartBoard.icon);
        taskStage.setScene(taskScene);
        taskStage.setResizable(false);
        taskStage.showAndWait();
    }
}
