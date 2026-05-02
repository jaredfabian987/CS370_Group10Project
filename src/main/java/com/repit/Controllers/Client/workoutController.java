package com.repit.Controllers.Client;

import com.repit.Model.DayWorkoutPlan;
import com.repit.Model.Equipment;
import com.repit.Model.Exercise;
import com.repit.Model.PlannedExercise;
import com.repit.Model.User;
import com.repit.Model.WorkoutLog;
import com.repit.Services.ServiceDispatcher;
import com.repit.main.java.Main;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class workoutController implements Initializable {

    public Button playButton;

    public Button pauseButton;

    @FXML
    private Label coachingCueLabel;

    @FXML
    private Label currentExerciseLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private StackPane exerciseMediaPane;

    @FXML
    private Button finishWorkoutButton;

    @FXML
    private Button logExerciseButton;

    @FXML
    private Label logSet1Label;

    @FXML
    private Label logSet2Label;

    @FXML
    private Label logSet3Label;

    @FXML
    private Label logSet4Label;

    @FXML
    private Label nextExerciseAmtLabel;

    @FXML
    private Button nextExerciseButton;

    @FXML
    private Label nextExerciseLabel;

    @FXML
    private Label reqEquip1Label;

    @FXML
    private Label reqEquip2Label;

    @FXML
    private Label reqEquip3Label;

    @FXML
    private TextField workingSet3TextField;

    @FXML
    private TextField workingSet4TextField;

    @FXML
    private Label workoutAreaLabel;

    @FXML
    private Label workoutCompletionLabel;

    @FXML
    private Label workoutDurationLabel;

    @FXML
    private ProgressBar workoutProgressBar;

    @FXML
    private Button playPauseButton;

    @FXML
    private MediaView mediaView;

    private Media media;

    private MediaPlayer mediaPlayer;

    //Variable(s):
    private final ServiceDispatcher serviceDispatcher = Main.getServiceDispatcher();

    // Maps exercise names (as stored in DB) to their video file names in /Videos/
    private static final Map<String, String> VIDEO_MAP = new HashMap<>();
    static {
        VIDEO_MAP.put("Cable Bicep Curls",                    "cable-bicep-curl.mp4");
        VIDEO_MAP.put("Cable Hammer Curls",                   "cable-hammer-curl.mp4");
        VIDEO_MAP.put("Inclined Bicep Curls",                 "bicep-curl.mp4");
        VIDEO_MAP.put("Overhead Tricep Extensions",           "overhead-tricep-extension.mp4");
        VIDEO_MAP.put("Tricep Pushdowns",                     "tricep-pushdown.mp4");
        VIDEO_MAP.put("Cable Rows",                           "seated-cable-row.mp4");
        VIDEO_MAP.put("Lat Pulldowns (Wide Grip)",            "lat-pulldown.mp4");
        VIDEO_MAP.put("Lat Pulldowns (Close Grip)",           "close-grip-lat-pulldown.mp4");
        VIDEO_MAP.put("Smith Machine Row",                    "barbell-row.mp4");
        VIDEO_MAP.put("Cable Chest Flys (Lower Chest)",       "lower-cable-chest-fly.mp4");
        VIDEO_MAP.put("Cable Chest Flys (Upper Chest)",       "upper-cable-chest-fly.mp4");
        VIDEO_MAP.put("Flat Bench (Barbell Bench Press)",     "bench-press.mp4");
        VIDEO_MAP.put("Incline Smith Machine Bench Press",    "smith-machine-incline-bench-press.mp4");
        VIDEO_MAP.put("Flat Bench (Dumbbell Bench Press)",    "dumbbell-bench-press.mp4");
        VIDEO_MAP.put("Incline Bench (Dumbbell Bench Press)", "incline-bench-press.mp4");
        VIDEO_MAP.put("Dumbbell RDLs",                        "romanian-deadlift.mp4");
        VIDEO_MAP.put("Squat Free Weight",                    "barbell-squat.mp4");
        VIDEO_MAP.put("Leg Extensions",                       "leg-extension.mp4");
        VIDEO_MAP.put("Leg Curls Laying Down",                "leg-curl.mp4");
        VIDEO_MAP.put("Seated Calf Raises",                   "seated-calf-raise.mp4");
        VIDEO_MAP.put("Dumbbell Shoulder Press",              "overhead-press.mp4");
        VIDEO_MAP.put("Seated Dumbbell Lateral Raises",       "dumbbell-chest-fly.mp4");
        VIDEO_MAP.put("Seated Rear Delt Flys",                "rear-delt-fly.mp4");
    }

    // Debounce: tracks the last time a user triggered an action button.
    // Prevents spam-clicking log/next/finish from firing multiple times rapidly.
    private long lastActionTime = 0;
    private static final long ACTION_COOLDOWN_MS = 800;

    // logged-in user — set by dashboardController immediately after switchScene()
    private User loggedUser;
    private int currentExerciseIndex;
    private DayWorkoutPlan dayWorkoutPlan;
    private List<PlannedExercise> plannedExercises = List.of();

    // Tracks which exercises in this session the user has already logged.
    // Used by finishWorkoutClicked to know which exercises still need a 0-rep
    // placeholder entry (so the dashboard can correctly show the workout as
    // partially completed).
    private final Set<Integer> loggedExerciseIds = new HashSet<>();

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;

        //Load in User data...
        loadWorkout();
    }

    //Initialization:
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setStateLoading();
        //Set Media Player Bounds and bind
        mediaView.setPreserveRatio(true);
        mediaView.fitWidthProperty().bind(exerciseMediaPane.widthProperty());
        mediaView.fitHeightProperty().bind(exerciseMediaPane.heightProperty());
    }

    //State(s):
    public void setStateLoading() {
        //First VBox:
        workoutAreaLabel.setText("Loading...");
        workoutDurationLabel.setText("Estimated duration: -- minutes");
        workoutProgressBar.setProgress(0);
        workoutCompletionLabel.setText("0 of 0 exercises completed");

        nextExerciseLabel.setText("Loading...");
        nextExerciseAmtLabel.setText("0 set x 8 reps");

        //Second VBox:
        currentExerciseLabel.setText("Loading...");
        coachingCueLabel.setText("Loading...");

        //Third VBox:
        //Required Equipment
        reqEquip1Label.setText("Loading...");
        reqEquip2Label.setText("Loading...");
        reqEquip3Label.setText("Loading...");

        //Log Sets
        logSet1Label.setText("-- lbs");
        logSet2Label.setText("-- lbs");
        logSet3Label.setText("-- lbs");
        logSet4Label.setText("-- lbs");

        //TEMP
        finishWorkoutButton.setVisible(false);
    }

    private void loadWorkout() {
        if (loggedUser == null) {
            errorLabel.setText("Login Error");
            return;
        }

        dayWorkoutPlan = serviceDispatcher.handleGetTodaysPlanRequest(loggedUser.getUserId());

        if (dayWorkoutPlan == null || dayWorkoutPlan.isRestDay() || dayWorkoutPlan.getExercises() == null || dayWorkoutPlan.getExercises().isEmpty()) {
            workoutAreaLabel.setText("Rest Day");
            workoutDurationLabel.setText("Estimated duration: 0 minutes");
            workoutProgressBar.setProgress(0);
            workoutCompletionLabel.setText("0 of 0 exercises completed");

            nextExerciseLabel.setText("No workout scheduled");
            nextExerciseAmtLabel.setText("0 sets x 0 reps");

            currentExerciseLabel.setText("No exercise loaded");
            coachingCueLabel.setText("Enjoy your recovery day.");

            reqEquip1Label.setText("--");
            reqEquip2Label.setText("--");
            reqEquip3Label.setText("--");

            logSet1Label.setText("-- lbs");
            logSet2Label.setText("-- lbs");
            logSet3Label.setText("-- lbs");
            logSet4Label.setText("-- lbs");

            logExerciseButton.setDisable(true);
            nextExerciseButton.setDisable(true);
            errorLabel.setText("");
            return;
        }

        plannedExercises = dayWorkoutPlan.getExercises();
        currentExerciseIndex = 0;
        logExerciseButton.setDisable(false);
        nextExerciseButton.setDisable(true);
        errorLabel.setText("");

        renderCurrentExercise();
    }

    /*
    Populates workout page with current exercise data.
     */
    private void renderCurrentExercise() {
        //Errors (these should not happen):
        //Returns if workout fails to load
        if (dayWorkoutPlan == null || plannedExercises == null || plannedExercises.isEmpty()) {
            return;
        }
        //Returns if currentExercise is out of bounds
        if (currentExerciseIndex < 0 || currentExerciseIndex >= plannedExercises.size()) {
            return;
        }

        //Local variables:
        PlannedExercise currentPlannedExercise = plannedExercises.get(currentExerciseIndex);
        Exercise currentExercise = currentPlannedExercise.getExercise();

        //First Vbox:
        workoutAreaLabel.setText(dayWorkoutPlan.getWorkoutName());
        workoutDurationLabel.setText("Estimated duration: " + dayWorkoutPlan.getEstimatedDurationMinutes() + " minutes");

        //Calculates and updates the workoutProgressBar
        double currentWorkoutProgressVal = (double) currentExerciseIndex / (double) plannedExercises.size();
        workoutProgressBar.setProgress(currentWorkoutProgressVal);
        workoutCompletionLabel.setText(currentExerciseIndex + " of " + plannedExercises.size() + " exercises completed");

        if (currentExerciseIndex + 1 < plannedExercises.size()) {
            PlannedExercise nextPlannedExercise = plannedExercises.get(currentExerciseIndex + 1);
            nextExerciseLabel.setText(nextPlannedExercise.getExercise().getName());
            nextExerciseAmtLabel.setText(nextPlannedExercise.getWorkingSets() + " sets x " + nextPlannedExercise.getTargetReps() + " reps");
        } else {
            nextExerciseLabel.setText("Finish Workout");
            nextExerciseAmtLabel.setText("Last exercise");
        }

        //Second Vbox:
        currentExerciseLabel.setText(currentExercise.getName());
        coachingCueLabel.setText(currentExercise.getCoachingCue());
        errorLabel.setText("");
        loadMedia(currentExercise);

        //Checks if there is any required equipment, if found display equipment, else "--"
        List<Equipment> requiredEquipment = currentExercise.getRequiredEquipment();
        reqEquip1Label.setText(requiredEquipment.size() > 0 ? requiredEquipment.get(0).getName() : "--");
        reqEquip2Label.setText(requiredEquipment.size() > 1 ? requiredEquipment.get(1).getName() : "--");
        reqEquip3Label.setText(requiredEquipment.size() > 2 ? requiredEquipment.get(2).getName() : "--");

        //Third Vbox:
        double suggestedWeight = currentPlannedExercise.getSuggestWeight();
        int targetReps = currentPlannedExercise.getTargetReps();

        // Show weight if PlannerService populated one — either the user's last
        // logged weight or the seeded week-1 starting weight from starting_weights.
        // Fall back to "— / target reps" only for true bodyweight movements
        // (suggestedWeight == 0, e.g. pull-ups or unseeded custom exercises).
        // We can't rely on Exercise.isBodyweight() because it checks an empty
        // requiredEquipment list and the seeder doesn't populate equipment yet,
        // so every exercise would falsely look like a bodyweight movement.
        if (suggestedWeight > 0) {
            // Standard warmup ramp: 50% → 75% → 100% → 100% of working weight,
            // rounded to the nearest 5 lbs (gym plates increment in 2.5s/5s).
            int warmup1 = roundTo5(suggestedWeight * 0.5);
            int warmup2 = roundTo5(suggestedWeight * 0.75);
            int working = roundTo5(suggestedWeight);
            logSet1Label.setText(warmup1 + " lbs");
            logSet2Label.setText(warmup2 + " lbs");
            logSet3Label.setText(working + " lbs");
            logSet4Label.setText(working + " lbs");
        } else {
            // FXML already shows "warm-up" in the type column — don't repeat it
            // in the target column. Em dash for warmup rows; rep target for working sets.
            logSet1Label.setText("—");
            logSet2Label.setText("—");
            logSet3Label.setText(targetReps + " reps");
            logSet4Label.setText(targetReps + " reps");
        }

        workingSet3TextField.clear();
        workingSet4TextField.clear();
        nextExerciseButton.setDisable(true);
    }

    //MediaPlayer Functions:
    //Temporary tester for the media player
    @FXML
    void selectMedia(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(exerciseMediaPane.getScene().getWindow());

        if (selectedFile != null) {
            configureMediaPlayer(selectedFile.toURI().toString());
            errorLabel.setText("");
        }
    }

    //Loads the video of the current exercise
    void loadMedia(Exercise exercise) {
        URL mediaResource = getMediaResource(exercise);

        //Checks if video exists, if not, dispose of Media player, set error message and return
        if (mediaResource == null) {
            deleteMediaPlayer();
            mediaView.setMediaPlayer(null);
            errorLabel.setText("No demo video available for this exercise");
            return;
        }

        configureMediaPlayer(mediaResource.toExternalForm());
    }

    //Retrieve media source and return — looks up the exercise name in VIDEO_MAP
    private URL getMediaResource(Exercise exercise) {
        if (exercise == null || exercise.getName() == null || exercise.getName().isBlank()) {
            return null;
        }
        String fileName = VIDEO_MAP.get(exercise.getName());
        if (fileName == null) {
            return null; // no video registered for this exercise
        }
        return getClass().getResource("/Videos/" + fileName);
    }

    //Configures Media player
    private void configureMediaPlayer(String mediaUrl) {
        //Delete existing media player to save resources
        deleteMediaPlayer();

        //Error handling:
        try {
            media = new Media(mediaUrl);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setMute(true);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        } catch (RuntimeException e) {
            deleteMediaPlayer();
            mediaView.setMediaPlayer(null);
            errorLabel.setText("Video failed to load: " + e.getMessage());
        }
    }

    //Deletes media player object
    private void deleteMediaPlayer() {
        //Frees resources, sets up for next video to be loaded
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        media = null;
    }

    //Play MediaPlayer
    @FXML
    void playClicked(ActionEvent event) {
        //If no media is loaded into media player, set
        if (mediaPlayer == null) {
            errorLabel.setText("No exercise video is loaded");
            return;
        }
        //Reset errorLabel
        errorLabel.setText("");
        mediaPlayer.play();
    }
    //Pause MediaPlayer
    @FXML
    void pauseClicked(ActionEvent event) {
        if (mediaPlayer == null) {
            errorLabel.setText("No exercise video is loaded");
            return;
        }
        errorLabel.setText("");
        mediaPlayer.pause();
    }

    //Navigation Function(s):
    @FXML
    void finishWorkoutClicked(ActionEvent event) {
        if (isThrottled()) return;

        // "Finish Workout" acts as a stop-and-save button. Any exercise the user
        // didn't get to (didn't click Log on) gets a single 0-rep placeholder log
        // entry with isCompleted = false. This way:
        //   - completed exercises remain marked completed (existing logs untouched)
        //   - skipped exercises show up in WorkoutLogs as "attempted but 0 reps"
        //   - the dashboard's "n / m exercises" counter and weekly progress banner
        //     can correctly report partial completion vs full completion
        if (loggedUser != null && plannedExercises != null) {
            String today = LocalDate.now().toString();
            int userId = loggedUser.getUserId();
            for (PlannedExercise planned : plannedExercises) {
                int exerciseId = planned.getExercise().getExerciseId();
                if (!loggedExerciseIds.contains(exerciseId)) {
                    WorkoutLog skipped = new WorkoutLog(
                            0, userId, exerciseId, false, today, 0, 0.0);
                    serviceDispatcher.handleSaveLogRequest(skipped);
                }
            }
        }

        deleteMediaPlayer();
        dashboardController controller = Main.getViewFactory().switchScene("Fxml/Client/dashboard.fxml");
        if (controller != null) controller.setLoggedUser(loggedUser);
    }

    // Returns true if a button was clicked too recently — prevents spam clicks from
    // firing the same action multiple times before the UI has finished updating.
    private boolean isThrottled() {
        long now = System.currentTimeMillis();
        if (now - lastActionTime < ACTION_COOLDOWN_MS) return true;
        lastActionTime = now;
        return false;
    }

    // Rounds a weight to the nearest 5 lbs — every gym plate combo lands on a
    // multiple of 5 (or 2.5, but we keep it whole-number simple here).
    // Used to display clean warmup/working set targets in the log panel.
    private int roundTo5(double weight) {
        return (int) (Math.round(weight / 5.0) * 5);
    }

    //Saving Data Function(s)
    @FXML
    void logClicked(ActionEvent event) {
        if (isThrottled()) return;
        if (loggedUser == null || plannedExercises == null || currentExerciseIndex < 0 || currentExerciseIndex >= plannedExercises.size()) {
            errorLabel.setText("Error: no exercise is loaded");
            return;
        }

        //User input variable(s):
        String workingSet3 = workingSet3TextField.getText();
        String workingSet4 = workingSet4TextField.getText();

        //Regex variables to ensure that user input is valid
        Pattern integerPattern = Pattern.compile("^\\d+$");
        Matcher integerChecker1 = integerPattern.matcher(workingSet3);
        Matcher integerChecker2 = integerPattern.matcher(workingSet4);

        //Conditional Statements:
        //If either Text Field is empty, return
        if (workingSet3.isEmpty() || workingSet4.isEmpty()) {
            errorLabel.setText("Error: enter reps completed");
            return;
        }

        //If either Text Field is not of integer type, return
        if (!integerChecker1.matches() || !integerChecker2.matches()) {
            errorLabel.setText("Error: limit input to whole numbers");
            return;
        }

        //Conditional Statements Passed:
        //Clear error
        errorLabel.setText("");

        // Save the two working sets as separate WorkoutLog rows — keeping each
        // set as its own entry lets ProgressService analyze rep ranges per set
        // (e.g. "set 4 dropped to 5 reps → don't progress next week").
        PlannedExercise currentPlannedExercise = plannedExercises.get(currentExerciseIndex);
        Exercise currentExercise = currentPlannedExercise.getExercise();

        int set3Reps = Integer.parseInt(workingSet3);
        int set4Reps = Integer.parseInt(workingSet4);
        // Use the seeded/last-known suggested weight as the recorded working weight.
        // Bodyweight movements (suggestedWeight == 0) get logged as 0 lbs.
        double workingWeight = currentPlannedExercise.getSuggestWeight();
        String today = LocalDate.now().toString();
        int userId = loggedUser.getUserId();
        int exerciseId = currentExercise.getExerciseId();

        WorkoutLog set3Log = new WorkoutLog(0, userId, exerciseId, true, today, set3Reps, workingWeight);
        WorkoutLog set4Log = new WorkoutLog(0, userId, exerciseId, true, today, set4Reps, workingWeight);

        boolean savedSet3 = serviceDispatcher.handleSaveLogRequest(set3Log);
        boolean savedSet4 = serviceDispatcher.handleSaveLogRequest(set4Log);

        if (!savedSet3 || !savedSet4) {
            errorLabel.setText("Error: failed to save workout log");
            return;
        }

        // remember this exercise was completed — finishWorkoutClicked uses this
        // to know which exercises still need a 0-rep placeholder
        loggedExerciseIds.add(exerciseId);

        //Allow user to continue
        nextExerciseButton.setDisable(false);
        errorLabel.setText("Sets logged");
    }

    @FXML
    void nextExerciseClicked(ActionEvent event) {
        if (isThrottled()) return;

        currentExerciseIndex++;

        if (currentExerciseIndex == plannedExercises.size() - 1) {
            nextExerciseButton.setText("Finish");
        }

        if (currentExerciseIndex >= plannedExercises.size()) {
            finishWorkoutClicked(event);
            return;
        }

        renderCurrentExercise();
    }

}
