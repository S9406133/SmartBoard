package com.example.smartboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class SmartBoard extends Application {

    private static Image icon = new Image("icon.png");

    @Override
    public void start(Stage stage) throws IOException {

        createLoginStage();

    }

    protected static void createPrimaryStage() throws IOException {
        String fxmlPrimaryName = "smartboard.fxml";
        FXMLLoader fxmlPrimaryLoader = new FXMLLoader(SmartBoard.class.getResource(fxmlPrimaryName));

        Rectangle2D rect = Screen.getPrimary().getBounds();
        double initSizeX = rect.getWidth() * 0.9;
        double initSizeY = rect.getHeight() * 0.8;

        Scene primaryScene = new Scene(fxmlPrimaryLoader.load(), initSizeX, initSizeY);
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Smart Board");
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }
    protected static void createLoginStage() throws IOException {
        String fxmlLoginName = "login.fxml";
        FXMLLoader fxmlLoginLoader = new FXMLLoader(SmartBoard.class.getResource(fxmlLoginName));
        Scene loginScene = new Scene(fxmlLoginLoader.load());
        Stage loginStage = new Stage();
        loginStage.setTitle("Smart Board - Login");
        loginStage.getIcons().add(icon);
        loginStage.setScene(loginScene);
        loginStage.setResizable(false);
        loginStage.show();
    }

    protected static void createNewUserStage() throws IOException {
        String fxmlCreateUserName = "newuser.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(SmartBoard.class.getResource(fxmlCreateUserName));
        Scene scene = new Scene(fxmlLoader.load());
        Stage createUserStage = new Stage();
        createUserStage.setTitle("Smart Board - Create a New User Account");
        createUserStage.getIcons().add(icon);
        createUserStage.setScene(scene);
        createUserStage.setResizable(false);
        createUserStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}