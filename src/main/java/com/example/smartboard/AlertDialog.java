package com.example.smartboard;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AlertDialog {

    public static void show(String message) {
        Stage dialogStage = new Stage(StageStyle.UTILITY);

        Label content = new Label(message);
        content.setFont(new Font(16));
        content.paddingProperty().setValue(new Insets(10, 10, 10, 10));

        DialogPane dialogPane = new DialogPane();
        dialogPane.paddingProperty().setValue(new Insets(0, 20, 0, 20));
        dialogPane.setContent(content);
        dialogPane.getButtonTypes().add(ButtonType.OK);
        dialogPane.lookupButton(ButtonType.OK).setOnMouseClicked(
                mouseEvent -> dialogStage.close()
        );

        Scene dialogScene = new Scene(dialogPane);

        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Alert");
        dialogStage.setResizable(false);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }
}
