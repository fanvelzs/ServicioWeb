module com.example.servicioweb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;


    opens com.example.servicioweb to javafx.fxml;
    exports com.example.servicioweb;
}