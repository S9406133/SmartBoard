package com.smartboard.view;

import com.smartboard.model.Project;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProjectView {

    private VBox columnBox;
    private HBox hBox;
    private ScrollPane scrollPane;
    private Tab protectTab;

    public ProjectView(Project project){
        this.columnBox =new VBox();
        this.hBox = new HBox();
        this.scrollPane = new ScrollPane();
        this.protectTab =new Tab(project.getName(), this.scrollPane);
    }

}
