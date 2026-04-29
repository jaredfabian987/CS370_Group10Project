package com.repit.main.java;

import com.repit.Services.ServiceDispatcher;
import com.repit.View.viewFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private static viewFactory viewFactory;
    private static final ServiceDispatcher serviceDispatcher = new ServiceDispatcher();

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
    //ServiceDispatcher will be used across all pages of the app
    public static ServiceDispatcher getServiceDispatcher() {return serviceDispatcher;}

    public static void main(String[] args) {
        launch(args);
    }

}
