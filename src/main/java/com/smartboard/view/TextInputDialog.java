/**
 * This class creates and controls a custom Text Input Dialogue Box
 */

package com.smartboard.view;

import com.smartboard.SmartBoard;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TextInputDialog {

    public static String show(String title, String prompt) {
        Stage dialogStage = new Stage(StageStyle.UTILITY);

        Label label = new Label("Please enter the name:");

        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setFont(new Font(12));
        textField.paddingProperty().setValue(new Insets(5));

        VBox vBox = new VBox(label, textField);
        vBox.setSpacing(5);
        vBox.paddingProperty().setValue(new Insets(10, 0, 20, 0));

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
        dialogStage.setOnShown(windowEvent -> textField.requestFocus());
        dialogStage.showAndWait();

        return textField.getText().strip();
    }
}
