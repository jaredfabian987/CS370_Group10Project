package com.repit.main.java;

import com.repit.View.viewFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    private static viewFactory viewFactory;

    @Override
    public void start(Stage primaryStage) throws Exception {
        viewFactory = new viewFactory(primaryStage);
        viewFactory.switchScene("Fxml/login.fxml");
        primaryStage.setTitle("Rep-It: Login");
    }

    //Allows this instance of viewFactory to be used across all controllers
    public static viewFactory getViewFactory() {
        return viewFactory;
    }
    /*
    public static void main(String[] args) {
        launch(args);
    }
    */
}
