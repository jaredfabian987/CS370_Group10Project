package com.repit.Controllers.Client;

import com.repit.Model.DayWorkoutPlan;
import com.repit.Model.FitnessProfile;
import com.repit.Model.User;
import com.repit.Services.ServiceDispatcher;
import com.repit.main.java.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;

public class plannerController implements Initializable {

    @FXML
    private Label availabiltiyLabel;

    @FXML
    private ToggleGroup fitnessGoalToggleGroup;

    @FXML
    private CheckBox fridayCheckBox;

    @FXML
    private ComboBox<String> fridayTimeComboBox;

    @FXML
    private Button generatePlanButton;

    @FXML
    private Button finishPlannerButton;

    @FXML
    private CheckBox mondayCheckBox;

    @FXML
    private ComboBox<String> mondayTimeComboBox;

    @FXML
    private RadioButton muscleGainRadialButton;

    @FXML
    private CheckBox saturdayCheckBox;

    @FXML
    private ComboBox<String> saturdayTimeComboBox;

    @FXML
    private Button savePlanButton;

    @FXML
    private CheckBox sundayCheckBox;

    @FXML
    private ComboBox<String> sundayTimeComboBox;

    @FXML
    private CheckBox thursdayCheckBox;

    @FXML
    private ComboBox<String> thursdayTimeComboBox;

    @FXML
    private CheckBox tuesdayCheckBox;

    @FXML
    private ComboBox<String> tuesdayTimeComboBox;

    @FXML
    private CheckBox wednesdayCheckBox;

    @FXML
    private ComboBox<String> wednesdayTimeComboBox;

    @FXML
    private RadioButton weightLossRadialButton;

    @FXML
    private Label errorLabel1;

    // the week-view grid on the right panel — populated after Generate Plan is clicked
    @FXML
    private GridPane weeklyPlannerGridPane;

    // ─ Variables

    private final ServiceDispatcher serviceDispatcher = Main.getServiceDispatcher();

    // logged-in user — set by dashboardController immediately after switchScene()
    private User loggedUser;

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    // ── Initialization

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // disable each time combobox until its checkbox is ticked
        bindDayToggle(mondayCheckBox, mondayTimeComboBox);
        bindDayToggle(tuesdayCheckBox,tuesdayTimeComboBox);
        bindDayToggle(wednesdayCheckBox,wednesdayTimeComboBox);
        bindDayToggle(thursdayCheckBox, thursdayTimeComboBox);
        bindDayToggle(fridayCheckBox,fridayTimeComboBox);
        bindDayToggle(saturdayCheckBox,saturdayTimeComboBox);
        bindDayToggle(sundayCheckBox, sundayTimeComboBox);
    }

    // Disables the time combobox unless the day checkbox is checked.
    // Clears the selection when the checkbox is unchecked.
    private void bindDayToggle(CheckBox dayCheckBox, ComboBox<String> timeComboBox) {
        timeComboBox.setDisable(!dayCheckBox.isSelected());

        dayCheckBox.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
            timeComboBox.setDisable(!isSelected);

            if (!isSelected) {
                timeComboBox.getSelectionModel().clearSelection();
            }
        });
    }

    // Validation

    // Returns true only if at least one day is checked AND every checked day
    // has a time selected. Sets errorLabel1 with a specific message on failure.
    public boolean checkBoxTest() {
        boolean anyDaySelected = false;
        boolean allTimeSelected = true;
        String errorMsg = "";

        CheckBox[] days = {mondayCheckBox, tuesdayCheckBox, wednesdayCheckBox,
        thursdayCheckBox, fridayCheckBox, saturdayCheckBox, sundayCheckBox};
        ComboBox<String>[] times = new ComboBox[]{mondayTimeComboBox, tuesdayTimeComboBox, wednesdayTimeComboBox,
        thursdayTimeComboBox, fridayTimeComboBox, saturdayTimeComboBox, sundayTimeComboBox};
        String[] names = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        for (int i = 0; i < days.length; i++) {
            if (days[i].isSelected()) {
                anyDaySelected = true;
                if (times[i].getSelectionModel().isEmpty()) {
                    errorMsg += names[i] + " ";
                    allTimeSelected = false;
                }
            }
        }

        if (!allTimeSelected) {
            errorLabel1.setText(errorMsg + "time(s) need to be selected");
            return false;
        }
        if (!anyDaySelected) {
            errorLabel1.setText("Select at least ONE day and time");
            return false;
        }
        return true;
    }



    // Generate Plan:
    //   1. Validate the form
    //   2. Read goal from the radio buttons
    //   3. Count checked days and find the highest minute value across them
    //   4. Preserve the user's existing fitness level (planner doesn't change it)
    //   5. Build a FitnessProfile and save/update it in the database
    //   6. Ask PlannerService for the full weekly plan and display it
    @FXML
    void generatePlanButtonClicked(ActionEvent event) {

        // guard against reaching this screen without a logged-in user
        if (loggedUser == null) {
            errorLabel1.setText("Session error — please log in again.");
            return;
        }

        // Step 1: validate
        if (!checkBoxTest()) { return; }

        // Step 2: read goal
        FitnessProfile.FitnessGoal goal = muscleGainRadialButton.isSelected()
                ? FitnessProfile.FitnessGoal.MUSCLE
                : FitnessProfile.FitnessGoal.MAINTAIN;

        // Step 3: count days and find max minutes
        int daysPerWeek = 0;
        int maxMinutes  = 0;

        CheckBox[] dayBoxes  = {mondayCheckBox, tuesdayCheckBox, wednesdayCheckBox,
                thursdayCheckBox, fridayCheckBox, saturdayCheckBox, sundayCheckBox};
        ComboBox<String>[] timeBoxes = new ComboBox[]{mondayTimeComboBox, tuesdayTimeComboBox,
                wednesdayTimeComboBox, thursdayTimeComboBox, fridayTimeComboBox, saturdayTimeComboBox, sundayTimeComboBox};

        for (int i = 0; i < dayBoxes.length; i++) {
            if (dayBoxes[i].isSelected()) {
                daysPerWeek++;
                int mins = parseMinutes(timeBoxes[i].getValue());
                if (mins > maxMinutes) maxMinutes = mins;
            }
        }

        // Step 4: preserve the user's existing fitness level if they already have a profile,
        //         otherwise default to beginner
        FitnessProfile.FitnessLevel level = FitnessProfile.FitnessLevel.BEG;
        FitnessProfile existing = serviceDispatcher.handleGetFitnessProfileRequest(loggedUser.getUserId());
        if (existing != null) {
            level = existing.getLevel();
        }

        // Step 5: build and save the profile
        FitnessProfile profile = new FitnessProfile(
                loggedUser.getUserId(),
                0.0,
                0.0,
                daysPerWeek,
                maxMinutes,
                level,
                goal
        );

        boolean saved;
        if (existing != null) {
            saved = serviceDispatcher.handleUpdateProfileRequest(profile);
        } else {
            saved = serviceDispatcher.handleSaveProfileRequest(profile);
        }

        if (!saved) {
            errorLabel1.setText("Error saving plan — please try again.");
            return;
        }

        // Step 6: get the generated weekly plan and show it in the week view
        Map<DayOfWeek, DayWorkoutPlan> weeklyPlan =
                serviceDispatcher.handleGetWeeklyPlanRequest(loggedUser.getUserId());

        if (weeklyPlan != null) {
            errorLabel1.setText("");
            updateWeeklyPlanner(weeklyPlan);
        } else {
            errorLabel1.setText("Could not generate plan — try again.");
        }
    }

    // Save Plan: the profile is already saved when Generate Plan runs.
    // This button confirms and returns the user to the dashboard.
    @FXML
    void savePlanButtonClicked(ActionEvent event) {
        dashboardController controller =
                Main.getViewFactory().switchScene("Fxml/Client/dashboard.fxml");
        controller.setLoggedUser(loggedUser);
    }

    // Finish without saving — just go back to the dashboard
    @FXML
    void finishPlannerButtonClicked(ActionEvent event) {
        dashboardController controller =
                Main.getViewFactory().switchScene("Fxml/Client/dashboard.fxml");
        controller.setLoggedUser(loggedUser);
    }

    // Week view builder

    // Clears the static placeholder cards in the FXML and replaces them with
    // real cards built from the generated weekly plan.
    // Layout: 4 columns × 2 rows — Mon/Tue/Wed/Thu on row 0, Fri/Sat/Sun on row 1.
    private void updateWeeklyPlanner(Map<DayOfWeek, DayWorkoutPlan> weeklyPlan) {
        weeklyPlannerGridPane.getChildren().clear();

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        DayOfWeek[] days = DayOfWeek.values(); // MONDAY … SUNDAY
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        for (int i = 0; i < 7; i++) {
            DayOfWeek day  = days[i];
            DayWorkoutPlan plan = weeklyPlan.get(day);

            int col = i % 4;
            int row = i / 4;

            // build the card
            VBox card = new VBox(6);
            card.getStyleClass().add("day-card");

            // highlight today
            if (day == today) {
                card.getStyleClass().add("day-card-active");
            }

            Label dayLabel = new Label(dayNames[i]);
            dayLabel.getStyleClass().add("planner-day");

            String workoutText;
            String detailText;

            if (plan == null || plan.isRestDay()) {
                workoutText = "Rest";
                detailText  = "Off day";
            } else {
                int minutes = plan.getExercises().size() * 10;
                workoutText = plan.getWorkoutName();
                detailText  = minutes + " min";
            }

            Label workoutLabel = new Label(workoutText);
            workoutLabel.getStyleClass().add("planner-item");

            Label detailLabel = new Label(detailText);

            card.getChildren().addAll(dayLabel, workoutLabel, detailLabel);
            weeklyPlannerGridPane.add(card, col, row);
        }
    }

    // Helpers

    // Parses "30 minutes" → 30. Returns 30 as a safe default if parsing fails.
    private int parseMinutes(String timeString) {
        try {
            return Integer.parseInt(timeString.split(" ")[0]);
        } catch (Exception e) {
            return 30;
        }
    }
}
