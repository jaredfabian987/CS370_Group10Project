package com.repit.Controllers.Client;

import com.repit.Model.DayWorkoutPlan;
import com.repit.Model.Equipment;
import com.repit.Model.Exercise;
import com.repit.Model.PlannedExercise;
import com.repit.Model.User;
import com.repit.Model.WorkoutLog;
import com.repit.Services.ServiceDispatcher;
import com.repit.main.java.Main;
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
import java.util.List;
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

    // logged-in user — set by dashboardController immediately after switchScene()
    private User loggedUser;
    private int currentExerciseIndex;
    private DayWorkoutPlan dayWorkoutPlan;
    private List<PlannedExercise> plannedExercises = List.of();

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

        //If currentExercise is a body weight exercise
        if (currentExercise.isBodyweight()) {
            logSet1Label.setText("Warm-up");
            logSet2Label.setText("Warm-up");
            logSet3Label.setText(targetReps + " reps");
            logSet4Label.setText(targetReps + " reps");
        } else {
            String weightLabel = ((int) suggestedWeight) + " lbs";
            logSet1Label.setText(weightLabel);
            logSet2Label.setText(weightLabel);
            logSet3Label.setText(weightLabel);
            logSet4Label.setText(weightLabel);
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

    //Retrieve media source and return
    private URL getMediaResource(Exercise exercise) {
        //If exercise is not loaded, return null
        if (exercise == null || exercise.getName() == null || exercise.getName().isBlank()) {
            return null;
        }
        //Regex to normalize the name, replaces spaces with dashes and makes string all lowercase
        String normalizedName = exercise.getName().trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-+|-+$)", "");

        //Store the string of the media path
        String mediaPath = "/Videos/" + normalizedName + ".mp4";

        //Store the URL of the video for the exercis, if not found, return null
        URL mediaResource = getClass().getResource(mediaPath);
        if (mediaResource != null) {
            return mediaResource;
        }
        return null;
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
        //service function that handles
        deleteMediaPlayer();
        Main.getViewFactory().switchScene("Fxml/Client/dashboard.fxml");
    }


    //Saving Data Function(s)
    @FXML
    void logClicked(ActionEvent event) {
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

        //Comment back in later:
        /*
        PlannedExercise currentPlannedExercise = plannedExercises.get(currentExerciseIndex);
        Exercise currentExercise = currentPlannedExercise.getExercise();

        int set3Reps = Integer.parseInt(workingSet3);
        int set4Reps = Integer.parseInt(workingSet4);
        double workingWeight = currentExercise.isBodyweight() ? 0 : currentPlannedExercise.getSuggestWeight();
        String today = LocalDate.now().toString();

        WorkoutLog set3Log = new WorkoutLog(0, loggedUser.getUserId(), currentExercise.getExerciseId(), true, today, set3Reps, workingWeight);
        WorkoutLog set4Log = new WorkoutLog(0, loggedUser.getUserId(), currentExercise.getExerciseId(), true, today, set4Reps, workingWeight);

        boolean savedSet3 = serviceDispatcher.handleSaveLogRequest(set3Log);
        boolean savedSet4 = serviceDispatcher.handleSaveLogRequest(set4Log);

        if (!savedSet3 || !savedSet4) {
            errorLabel.setText("Error: failed to save workout log");
            return;
        }
        */

        //Allow user to continue
        nextExerciseButton.setDisable(false);
        errorLabel.setText("Sets logged");
    }

    @FXML
    void nextExerciseClicked(ActionEvent event) {
        //insert updater function here

        currentExerciseIndex++;

        if (currentExerciseIndex >= plannedExercises.size()) {
            finishWorkoutClicked(event);
            return;
        }

        renderCurrentExercise();
    }

}
