package com.repit.View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class viewFactory {
    //Class variable(s):
    //stage is passed in from main
    private final Stage stage;

    //Constructor:
    //Passes in stage from main and intializes "stage" variable
    public viewFactory(Stage stage){
        this.stage = stage;
    }

    //Method(s):
    //Take in String fxml and returns respective page
    public <T> T switchScene(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getResource(fxml));
            Parent root = loader.load();

            //Set scene to newly retrieved page/fxml file
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            //Return respective controller
            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load scene",e);
        }
    }

    //Helper methods(s):
    //Pass in String fxml and return absolute name of fxml
    public URL getResource(String fxml) {
        URL resource = viewFactory.class.getResource("/" + fxml);

        //if file does not exist... throw runtime error
        if (resource == null) {
            throw new RuntimeException("Failed to load resource " + fxml);
        }
        return viewFactory.class.getResource("/" + fxml);
    }
}
