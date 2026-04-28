package com.repit.Controllers.Client;

import com.repit.DAOs.FitnessProfileDAO;
import com.repit.Model.FitnessProfile;
import com.repit.Model.User;
import com.repit.Model.WorkoutLog;
import com.repit.Services.ServiceDispatcher;
import com.repit.Services.WorkoutService;
import com.repit.main.java.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.security.Provider;
import java.util.List;
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

    @FXML
    private GridPane weeklyPlannerGridPane;

    //User object
    private User loggedUser;

    //Dashboard Service Object
    private final ServiceDispatcher serviceDispatcher = new ServiceDispatcher();

    //Initialization methods:
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setStateLoading();
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
        loadDashboardData();
    }

    //States:
    //Loading State, inserts placeholder text while service retrieves database items
    public void setStateLoading() {
        todaysWorkoutLabel.setText("Loading...");
        estimatedTimeLabel.setText("Estimated Time: Loading..");
        workoutCompletionLabel.setText("0 / 0 workouts");
        workoutCompletionProgressBar.setProgress(0);
        //startWorkoutButton.setDisable(true);
        //openPlannerButton.setDisable(true);

        //TEMP
        updateWeeklyPlanner(List.of(
                "Upper Body",
                "Lower Body",
                "Recovery",
                "Push",
                "Recovery",
                "Core",
                "Rest"
        ));
    }

    //Loaded State, inserts the data returned by the service dispatcher
    private void loadDashboardData() {
        //if no user is logged in, return
        if (loggedUser == null) {
            return;
        }

        int loggedUserID = loggedUser.getUserId();

        FitnessProfile fitnessProfile = serviceDispatcher.handleGetFitnessProfileRequest(loggedUserID);

        int workoutCount = serviceDispatcher.handleGetWorkoutCountRequest(loggedUserID);


        todaysWorkoutLabel.setText("Loading...");
        estimatedTimeLabel.setText("Estimated Time: Loading..");
        workoutCompletionLabel.setText("0 / 0 workouts");
        workoutCompletionProgressBar.setProgress(0);

        //Insert service function to get data
        //serviceDispatcher.handle

        //Insert
        //
    }

    private void updateWeeklyPlanner(List<String> workoutPlanItems) {
        //Clear placed holder information
        weeklyPlannerGridPane.getChildren().clear();

        String [] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        for (int i = 0; i < 7; i++) {
            //Calculates where to insert the card
            int col = i % 4;
            int row = i / 4;

            //Set card
            VBox dayCard = new VBox(6);
            dayCard.getStyleClass().add("mini-card");

            //Set the card header to day
            Label dayName = new Label(dayNames[i]);
            dayCard.getStyleClass().add("planner-day");

            //Set the card subheader to status
            Label statusLabel = new Label (i < workoutPlanItems.size() ? workoutPlanItems.get(i) : "Rest");
            statusLabel.getStyleClass().add("planner-item");

            //Set the card details to... fill this out later
            Label detailLabel = new Label("");
            detailLabel.setWrapText(true);

            //Add all fields to current day card
            dayCard.getChildren().addAll(dayName, statusLabel, detailLabel);

            //Add card to PlannerGridPane
            weeklyPlannerGridPane.add(dayCard,col,row);
        }
    }




    //Buttons and Events:
    //Logs out loads, login fxml fil
    @FXML
    void logoutClicked(ActionEvent event) {
        //insert user service to clear data

        /* Comment out later
        loggedUser = null;
        Main.getViewFactory().switchScene("Fxml/login.fxml");
         */
        Main.getViewFactory().switchScene("Fxml/login.fxml");
    }

    //opens Planner, loads fxml file
    @FXML
    void openPlannerClicked(ActionEvent event) {
        //Insert is rest day logic here

        /* Comment in later
        plannerController controller = Main.getViewFactory().switchScene("Fxml/Client/planner.fxml");
        controller.setLoggedUser(loggedUser);
         */
        Main.getViewFactory().switchScene("Fxml/Client/planner.fxml");
    }

    //Starts workout, loads fxml file
    @FXML
    void startWorkoutClicked(ActionEvent event) {workoutController controller = Main.getViewFactory().switchScene("Fxml/Client/workout.fxml");
        /* Comment in later
        workoutController controller = Main.getViewFactory().switchScene("Fxml/Client/workout.fxml");
        controller.setLoggedUser(loggedUser);
        */
        Main.getViewFactory().switchScene("Fxml/Client/workout.fxml");
    }
}

