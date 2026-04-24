package com.repit.Controllers.Client;

import com.repit.Model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class dashboardController implements Initializable {

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

    //User object
    private User loggedUser;

    //Dashboard Service Object
    //private DashboardService dashboardService;

    //Initialization methods:
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setStateLoading();
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
        setStateLoading();
    }

    //States:
    //Loading State, inserts placeholder text while service retrieves database items
    public void setStateLoading() {
        todaysWorkoutLabel.setText("Loading...");
        estimatedTimeLabel.setText("Estimated Time: Loading..");
        workoutCompletionLabel.setText("0 / 0 workouts");
        workoutCompletionProgressBar.setProgress(0);
        startWorkoutButton.setDisable(true);
        openPlannerButton.setDisable(true);
    }

    //Loaded State, inserts the data returned by the service dispatcher
    private void loadDashboardData() {
        //if no user is logged in, return
        if (loggedUser != null) {
            return;
        }

        //Insert service function to get data
        //

        //Insert
        //
    }




    //Buttons and Events:
    //Logs out loads, login fxml fil
    @FXML
    void logoutClicked(ActionEvent event) {

    }

    //opens Planner, loads fxml file
    @FXML
    void openPlannerClicked(ActionEvent event) {

    }

    //Starts workout, loads fxml file
    @FXML
    void startWorkoutClicked(ActionEvent event) {

    }
}

