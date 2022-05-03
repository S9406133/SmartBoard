package com.smartboard.view;

import com.smartboard.SmartBoard;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class SmartBoardView {

    public static void createSmartBoardStage() throws IOException {
        String fxmlPrimaryName = "smartboard.fxml";
        FXMLLoader fxmlPrimaryLoader = new FXMLLoader(SmartBoard.class.getResource(fxmlPrimaryName));

        Rectangle2D rect = Screen.getPrimary().getBounds();
        double initSizeX = rect.getWidth() * 0.9;
        double initSizeY = rect.getHeight() * 0.8;

        Scene primaryScene = new Scene(fxmlPrimaryLoader.load(), initSizeX, initSizeY);

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Smart Board");
        primaryStage.getIcons().add(SmartBoard.icon);
//        primaryStage.setMaximized(true);
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }
}
