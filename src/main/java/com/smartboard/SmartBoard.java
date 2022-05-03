package com.smartboard;

import com.smartboard.model.Data;
import com.smartboard.model.StringLengthException;
import com.smartboard.view.LoginView;
import com.smartboard.view.ProjectView;
import com.smartboard.view.SmartBoardView;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class SmartBoard extends Application {

    public static Image icon = new Image("icon.png");

    @Override
    public void start(Stage stage) throws IOException, StringLengthException {

        Data.createInitUser();

        LoginView.createLoginStage();

        //SmartBoardView.createSmartBoardStage();
    }

    public static void main(String[] args) {
        launch();
    }
}