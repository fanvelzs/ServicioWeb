module com.example.servicioweb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;


    opens com.example.servicioweb to javafx.fxml;
    opens com.example.servicioweb.model to javafx.base;
    exports com.example.servicioweb;
}
