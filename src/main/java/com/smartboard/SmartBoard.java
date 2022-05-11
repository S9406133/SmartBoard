package com.smartboard;

import com.smartboard.model.Data;
import com.smartboard.model.StringLengthException;
import com.smartboard.view.LoginView;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class SmartBoard extends Application {

    public static Image icon = new Image("icon.png");

    @Override
    public void start(Stage stage) throws IOException, StringLengthException {

        Data.createInitUser();

        LoginView.createLoginView();
    }

    public static void main(String[] args) {
        launch();
    }
}