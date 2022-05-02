package com.smartboard.controller;

import javafx.scene.control.Alert;

public class Utility {

    public static void errorAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
