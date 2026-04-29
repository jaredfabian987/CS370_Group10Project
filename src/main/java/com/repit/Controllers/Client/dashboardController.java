package com.repit.Controllers.Client;

import com.repit.Model.DayWorkoutPlan;
import com.repit.Model.User;
import com.repit.Services.ServiceDispatcher;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;

public class dashboardController implements Initializable {

    // displays the name of today's workout (e.g. "PUSH" or "Rest Day")
    @FXML
    private Label todaysWorkoutLabel;

    // displays the estimated time to complete today's workout
    @FXML
    private Label estimatedTimeLabel;

    // displays "n / n workouts" weekly progress text
    @FXML
    private Text workoutCompletionLabel;

    // shows weekly progress as a 0.0 - 1.0 filled bar
    @FXML
    private ProgressBar workoutCompletionProgressBar;

    // the 4-column grid that holds the 7 day cards
    @FXML
    private GridPane weeklyPlannerGridPane;

    @FXML
    private Button logoutButton;

    @FXML
    private Button openPlannerButton;

    @FXML
    private Button startWorkoutButton;

    // logged-in user — set by the previous screen via setLoggedUser()
    private User loggedUser;

    private final ServiceDispatcher serviceDispatcher = new ServiceDispatcher();

    // ─────────────────────────────────────────────────────────────────────────
    // Initialization
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // show placeholder text until setLoggedUser() is called
        setStateLoading();
    }

    // called by the previous screen immediately after switchScene()
    // passing the user here triggers the real data load
    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
        loadDashboardData();
        System.out.println("loggedUser: " + loggedUser.getUsername());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // States
    // ─────────────────────────────────────────────────────────────────────────

    // placeholder state — shown while the screen initializes before the user is set
    private void setStateLoading() {
        todaysWorkoutLabel.setText("Loading...");
        estimatedTimeLabel.setText("Estimated time: --");
        workoutCompletionLabel.setText("0 / 0 workouts");
        workoutCompletionProgressBar.setProgress(0);
    }

    // loaded state — called once loggedUser is available, fills every UI element with real data
    private void loadDashboardData() {
        if (loggedUser == null) return;

        int userId = loggedUser.getUserId();

        // ── Today's workout card ──────────────────────────────────────────────
        // getTodaysPlanRequest returns null if today is a rest day or no profile exists
        DayWorkoutPlan todaysPlan = serviceDispatcher.handleGetTodaysPlanRequest(userId);

        if (todaysPlan != null && !todaysPlan.isRestDay()) {
            // show the workout type name (PUSH, PULL, LEGS, etc.)
            todaysWorkoutLabel.setText(todaysPlan.getWorkoutName());
            // each exercise takes 10 minutes — multiply by count for total time
            int minutes = todaysPlan.getExercises().size() * 10;
            estimatedTimeLabel.setText("Estimated time: " + minutes + " minutes");
        } else {
            todaysWorkoutLabel.setText("Rest Day");
            estimatedTimeLabel.setText("No workout today — enjoy your rest");
        }

        // ── Weekly progress banner ────────────────────────────────────────────
        // completed = days this week where the user logged at least one set
        // planned = daysPerWeek from the user's fitness profile
        int completed = serviceDispatcher.handleGetWeeklyWorkoutsCompletedRequest(userId);
        int planned   = serviceDispatcher.handleGetWeeklyWorkoutsPlannedRequest(userId);

        workoutCompletionLabel.setText(completed + " / " + planned + " workouts");

        // progress bar takes a value between 0.0 and 1.0
        // guard against divide-by-zero if profile has no days set
        double progress = planned > 0 ? (double) completed / planned : 0.0;
        workoutCompletionProgressBar.setProgress(progress);

        // ── Weekly planner grid ───────────────────────────────────────────────
        // returns a Map<DayOfWeek, DayWorkoutPlan> for every day Mon-Sun
        // null means the user has no profile yet
        Map<DayOfWeek, DayWorkoutPlan> weeklyPlan =
                serviceDispatcher.handleGetWeeklyPlanRequest(userId);

        if (weeklyPlan != null) {
            updateWeeklyPlanner(weeklyPlan);
        }
    }


    // Weekly planner grid builder

    // builds the 7 day cards from the real weekly plan
    // grid layout: 4 columns × 2 rows
    //   row 0: Mon | Tue | Wed | Thu
    //   row 1: Fri | Sat | Sun | (empty)
    private void updateWeeklyPlanner(Map<DayOfWeek, DayWorkoutPlan> weeklyPlan) {
        weeklyPlannerGridPane.getChildren().clear();

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        DayOfWeek[] days     = DayOfWeek.values(); // MONDAY through SUNDAY
        String[]    dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        for (int i = 0; i < 7; i++) {
            DayOfWeek     day  = days[i];
            DayWorkoutPlan plan = weeklyPlan.get(day);

            int col = i % 4;
            int row = i / 4;

            // ── build the card ────────────────────────────────────────────────
            VBox dayCard = new VBox(6);
            dayCard.getStyleClass().add("mini-card");

            // highlight today's card with an extra CSS class
            if (day == today) {
                dayCard.getStyleClass().add("mini-card-active");
            }

            // day name label (Mon, Tue, etc.)
            Label dayNameLabel = new Label(dayNames[i]);
            dayNameLabel.getStyleClass().add("planner-day");

            // ── determine status and detail text
            String status;
            String detail;

            if (plan == null || plan.isRestDay()) {
                status = "Rest";
                detail = "Rest Day";
            } else if (plan.isCompleted()) {
                status = "Completed";
                detail = plan.getWorkoutName();
            } else if (day == today) {
                status = "Today";
                detail = plan.getWorkoutName() + " · "
                        + (plan.getExercises().size() * 10) + " min";
            } else {
                status = "Scheduled";
                detail = plan.getWorkoutName() + " · "
                        + (plan.getExercises().size() * 10) + " min";
            }

            Label statusLabel = new Label(status);
            statusLabel.getStyleClass().add("planner-item");

            Label detailLabel = new Label(detail);
            detailLabel.setWrapText(true);

            dayCard.getChildren().addAll(dayNameLabel, statusLabel, detailLabel);
            weeklyPlannerGridPane.add(dayCard, col, row);
        }
    }

    // Button handlers

    @FXML
    void logoutClicked(ActionEvent event) {
        // clear the session and return to login
        loggedUser = null;
        Main.getViewFactory().switchScene("Fxml/login.fxml");
    }

    @FXML
    void openPlannerClicked(ActionEvent event) {
        plannerController controller =
                Main.getViewFactory().switchScene("Fxml/Client/planner.fxml");
        controller.setLoggedUser(loggedUser);
    }

    @FXML
    void startWorkoutClicked(ActionEvent event) {
        workoutController controller =
                Main.getViewFactory().switchScene("Fxml/Client/workout.fxml");
        controller.setLoggedUser(loggedUser);
    }
}
