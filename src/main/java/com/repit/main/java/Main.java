package com.repit.main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL loginView = Main.class.getResource("/Fxml/login.fxml");
        if (loginView == null) {
            throw new IllegalStateException("Missing resource: /Fxml/login.fxml");
        }

        FXMLLoader loader = new FXMLLoader(loginView);
        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("Rep-It");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
