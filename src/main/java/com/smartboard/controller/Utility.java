package com.smartboard.controller;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Utility {

    public static void errorAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public static File displayFileChooser(Stage stage){
        FileChooser fileChooser = new FileChooser();
        return fileChooser.showOpenDialog(stage);
    }
}
