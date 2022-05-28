/**
 * This is the view creating class for the Login stage
 */

package com.smartboard.view;

import com.smartboard.SmartBoard;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginView {

    public static void createLoginView() throws IOException {
        String fxmlLoginName = "login.fxml";
        FXMLLoader fxmlLoginLoader = new FXMLLoader(SmartBoard.class.getResource(fxmlLoginName));
        Scene loginScene = new Scene(fxmlLoginLoader.load());
        Stage loginStage = new Stage();
        loginStage.setTitle("Smart Board - Login");
        loginStage.getIcons().add(SmartBoard.icon);
        loginStage.setScene(loginScene);
        loginStage.setResizable(false);
        loginStage.show();
    }
}
