/**
 * Interface to hold the view utility methods
 */

package com.smartboard.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public interface View_Utils {

    /**
     * Displays an Error alert dialogue
     */
    static void errorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    /**
     * Displays a Warning alert dialogue when deleting an item
     */
    static ButtonType deleteAlert(String name, String type) {
        Alert deleteAlert = new Alert(Alert.AlertType.WARNING);
        deleteAlert.getButtonTypes().add(ButtonType.CANCEL);
        deleteAlert.setTitle("Delete " + type);
        deleteAlert.setHeaderText("You're about to delete the " + type +
                "\n- " + name + " -\nand all associated data!");
        deleteAlert.setContentText("Press OK to delete, or cancel to return to Smart Board");

        return deleteAlert.showAndWait().get();
    }

    /**
     * Displays a FileChooser for selecting image files
     */
    static File displayImageFileChooser(Stage stage) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files", "*.jpg", "*.JPG", "*.png", "*.PNG", "*.gif", "*.GIF")
        );
        return fileChooser.showOpenDialog(stage);
    }
}
