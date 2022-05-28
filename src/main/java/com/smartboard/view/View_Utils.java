package com.smartboard.view;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class View_Utils {

    public static void errorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public static File displayImageFileChooser(Stage stage) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files", "*.jpg", "*.JPG", "*.png", "*.PNG", "*.gif", "*.GIF")
        );
        return fileChooser.showOpenDialog(stage);
    }
}
