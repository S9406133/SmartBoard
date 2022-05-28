package com.smartboard;

import com.smartboard.model.User_Utils;
import com.smartboard.view.LoginView;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class SmartBoard extends Application {

    public static final Image icon = new Image("sb_icon.png");

    @Override
    public void start(Stage stage) throws IOException {

        User_Utils.loadUsersFromDB();
        LoginView.createLoginView();
    }

    public static void main(String[] args) {
        launch();
    }
}