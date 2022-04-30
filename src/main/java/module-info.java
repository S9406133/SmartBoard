module com.example.smartboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;


    opens com.example.smartboard to javafx.fxml;
    exports com.example.smartboard;
}