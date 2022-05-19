package com.smartboard.view;

import com.smartboard.SmartBoard;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TextInputDialog {

    public static String show(String title, String prompt) {
        String returnVal = "";
        Stage dialogStage = new Stage(StageStyle.UTILITY);

        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setFont(new Font(12));
        textField.paddingProperty().setValue(new Insets(5));

        VBox vBox = new VBox(textField);
        vBox.paddingProperty().setValue(new Insets(20, 0, 20, 0));

        DialogPane dialogPane = new DialogPane();
        dialogPane.paddingProperty().setValue(new Insets(10, 20, 0, 20));
        dialogPane.setContent(vBox);
        dialogPane.getButtonTypes().add(ButtonType.OK);
        dialogPane.getButtonTypes().add(ButtonType.CANCEL);

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setOnAction(actionEvent -> {
            if (!textField.getText().isBlank()) {
                dialogStage.close();
            }
        });

        dialogPane.lookupButton(ButtonType.CANCEL).setOnMouseClicked(
                mouseEvent -> {
                    textField.setText("");
                    dialogStage.close();
                });

        Scene dialogScene = new Scene(dialogPane);

        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(title);
        dialogStage.getIcons().add(SmartBoard.icon);
        dialogStage.setResizable(false);
        dialogStage.setScene(dialogScene);
        dialogStage.setOnCloseRequest(windowEvent -> textField.setText(""));
        dialogStage.showAndWait();

        textField.requestFocus(); // ??

        returnVal = textField.getText().strip();

        return returnVal;
    }
}
