package com.example.smartboard;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SBController implements Closable{
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML @Override
    public void handleCloseButtonAction() {

    }
}