package com.example.smartboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class SmartBoard extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        String fxmlLoginName = "login.fxml";
        String fxmlPrimaryName = "smartboard.fxml";
        FXMLLoader fxmlLoginLoader = new FXMLLoader(SmartBoard.class.getResource(fxmlLoginName));
        Scene loginScene = new Scene(fxmlLoginLoader.load());
        Stage loginStage = new Stage();
        loginStage.setTitle("Smart Board - Login");
        loginStage.setScene(loginScene);
        loginStage.setResizable(false);
        loginStage.show();

        createUserStage();

        FXMLLoader fxmlPrimaryLoader = new FXMLLoader(SmartBoard.class.getResource(fxmlPrimaryName));

        Rectangle2D rect = Screen.getPrimary().getBounds();
        double initSizeX = rect.getWidth() * 0.9;
        double initSizeY = rect.getHeight() * 0.8;

        Scene primaryScene = new Scene(fxmlPrimaryLoader.load(), initSizeX, initSizeY);
        primaryStage.setTitle("Smart Board");
        primaryStage.setScene(primaryScene);
        //primaryStage.show();
    }

    public void createUserStage() throws IOException {
        String fxmlCreateUserName = "newuser.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(SmartBoard.class.getResource(fxmlCreateUserName));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Smart Board - Create a New User Account");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}