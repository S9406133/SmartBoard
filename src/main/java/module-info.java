module com.example.smartboard {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.smartboard to javafx.fxml;
    exports com.example.smartboard;
}