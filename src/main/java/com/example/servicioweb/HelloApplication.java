package com.example.servicioweb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("usuario-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Consulta de usuarios");
        stage.setMinWidth(800);
        stage.setMinHeight(460);
        stage.setScene(scene);
        stage.show();
    }
}
