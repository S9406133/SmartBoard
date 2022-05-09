package com.smartboard.view;

import com.smartboard.SmartBoard;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NewUserView {

    public static void createNewUserView() throws IOException {
        String fxmlCreateUserName = "newuser.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(SmartBoard.class.getResource(fxmlCreateUserName));
        Scene scene = new Scene(fxmlLoader.load());
        Stage createUserStage = new Stage();
        createUserStage.setTitle("Smart Board - Create a New User Account");
        createUserStage.getIcons().add(SmartBoard.icon);
        createUserStage.setScene(scene);
        createUserStage.setResizable(false);
        createUserStage.show();
    }
}
