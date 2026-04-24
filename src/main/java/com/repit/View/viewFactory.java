package com.repit.View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class viewFactory {
    //Class variables:
    //stage is passed in from main
    private final Stage stage;

    //Constructor:
    //Passes in stage from main and intializes "stage" variable
    public viewFactory(Stage stage){
        this.stage = stage;
    }

    //Methods:
    //Take in String fxml and return
    public <T> T switchScene(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();



            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load scene",e);
        }
    }

    //Pass in String fxml and return absolute name of fxml
    public URL getResource(String fxml) {
        URL resource = viewFactory.class.getResource("/" + fxml);
        return resource;
    }
}
