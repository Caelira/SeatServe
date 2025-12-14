module org.example.angelsburgerpos {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;
    requires io.javalin;
    requires com.google.gson;

    opens org.example.angelsburgerpos.model to javafx.base;



    opens org.example.angelsburgerpos to javafx.fxml;
    exports org.example.angelsburgerpos;
    exports org.example.angelsburgerpos.controller;
    opens org.example.angelsburgerpos.controller to javafx.fxml;
}