module com.smartboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;

    opens com.smartboard to javafx.fxml;
    exports com.smartboard;
    exports com.smartboard.controller;
    opens com.smartboard.controller to javafx.fxml;
    exports com.smartboard.view;
    opens com.smartboard.view to javafx.fxml;
    exports com.smartboard.model;
}