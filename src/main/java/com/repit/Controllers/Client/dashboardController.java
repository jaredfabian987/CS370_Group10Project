package com.repit.Controllers.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

public class dashboardController {

    //displays estimated time to complete the workout for the day
    @FXML
    private Label estimatedTimeLabel;

    //logs the user out
    @FXML
    private Button logoutButton;

    //navigates to the planner page
    @FXML
    private Button openPlannerButton;

    //navigates to the workout page
    @FXML
    private Button startWorkoutButton;

    //displays the workout of the day
    @FXML
    private Label todaysWorkoutLabel;

    //displays the fraction of workouts completed during the week
    @FXML
    private Text workoutCompletionLabel;

    //displays the fraction of workout completed in a graphic format
    @FXML
    private ProgressBar workoutCompletionProgressBar;

    @FXML
    void logoutClicked(ActionEvent event) {

    }

    @FXML
    void openPlannerClicked(ActionEvent event) {

    }

    @FXML
    void startWorkoutClicked(ActionEvent event) {

    }
}

