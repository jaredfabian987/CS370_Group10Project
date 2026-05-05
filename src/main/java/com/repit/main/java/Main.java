package com.repit.main.java;

import com.repit.DAOs.DatabaseSeeder;
import com.repit.DAOs.DemoDataSeeder;
import com.repit.Services.ServiceDispatcher;
import com.repit.View.viewFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private static viewFactory viewFactory;
    // NOTE: ServiceDispatcher must NOT be a static field initializer.
    // Static fields run when the class loads — before start() — which means
    // ServiceDispatcher would create the exercises table (with its NOT NULL constraints)
    // before DatabaseSeeder.seed() can insert any rows, causing INSERT OR IGNORE to
    // silently skip every exercise row and leaving the exercises table empty.
    // Initializing here (after seed()) guarantees the table is pre-populated first.
    private static ServiceDispatcher serviceDispatcher;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // seed the exercise library on every launch — safe to call repeatedly
        DatabaseSeeder.seed();
        // seed a "demo" user (login: demo / demo123) — ADV lifter with last
        // week's training history pre-populated. Used to verify progressive-overload
        // behavior. Idempotent: skips if the demo user already exists.
        DemoDataSeeder.seedDemoUser();
        // create DAOs AFTER seeding so the schema is already correct
        serviceDispatcher = new ServiceDispatcher();

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
