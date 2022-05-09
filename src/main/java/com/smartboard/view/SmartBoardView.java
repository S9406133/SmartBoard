package com.smartboard.view;

import com.smartboard.SmartBoard;
import com.smartboard.controller.SBController;
import com.smartboard.model.Column;
import com.smartboard.model.Project;
import com.smartboard.model.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class SmartBoardView {

    SBController sbController;

    public void createSmartBoardStage() throws IOException {
        String fxmlPrimaryName = "smartboard.fxml";
        FXMLLoader fxmlPrimaryLoader = new FXMLLoader(SmartBoard.class.getResource(fxmlPrimaryName));

        sbController = fxmlPrimaryLoader.getController();

        Rectangle2D rect = Screen.getPrimary().getBounds();
        double initSizeX = rect.getWidth() * 0.9;
        double initSizeY = rect.getHeight() * 0.8;

        Scene primaryScene = new Scene(fxmlPrimaryLoader.load(), initSizeX, initSizeY);

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Smart Board");
        primaryStage.getIcons().add(SmartBoard.icon);
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }
}