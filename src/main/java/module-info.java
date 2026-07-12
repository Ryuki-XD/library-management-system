module com.librarysystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.librarysystem to javafx.fxml;
    opens com.librarysystem.controller to javafx.fxml;
    opens com.librarysystem.model to javafx.base;

    exports com.librarysystem;
    exports com.librarysystem.controller;
    exports com.librarysystem.model;
}
