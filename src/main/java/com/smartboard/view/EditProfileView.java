package com.smartboard.view;

import com.smartboard.SmartBoard;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class EditProfileView {

    public static void createEditProfileStage() throws IOException {
        String fxmlName = "editprofile.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(SmartBoard.class.getResource(fxmlName));
        Scene editScene = new Scene(fxmlLoader.load());
        Stage editStage = new Stage(StageStyle.UTILITY);
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.setTitle("Edit user profile");
        editStage.getIcons().add(SmartBoard.icon);
        editStage.setScene(editScene);
        editStage.setResizable(false);
        editStage.showAndWait();
    }
}
