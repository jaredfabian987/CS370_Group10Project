package com.repit.Controllers.Client;

import com.repit.Model.DayWorkoutPlan;
import com.repit.Model.Equipment;
import com.repit.Model.Exercise;
import com.repit.Model.PlannedExercise;
import com.repit.Model.User;
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
        //Set Media Player Bounds
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

        //Checks if there is any required equipment, if found display equipment, else "--"
        List<Equipment> requiredEquipment = currentExercise.getRequiredEquipment();
        reqEquip1Label.setText(requiredEquipment.size() > 0 ? requiredEquipment.get(0).getName() : "--");
        reqEquip2Label.setText(requiredEquipment.size() > 1 ? requiredEquipment.get(1).getName() : "--");
        reqEquip3Label.setText(requiredEquipment.size() > 2 ? requiredEquipment.get(2).getName() : "--");

        //Third Vbox:
        double suggestedWeight = currentPlannedExercise.getSuggestWeight();
        int targetReps = currentPlannedExercise.getTargetReps();

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
        errorLabel.setText("");
    }

    //MediaPlayer Functions:
    //Replace FileChooser with passed in string that gets file location from resources
    @FXML
    void selectMedia(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(exerciseMediaPane.getScene().getWindow());

        if (selectedFile != null) {
            String mediaUrl = selectedFile.toURI().toString();
            media = new Media(mediaUrl);
            mediaPlayer = new MediaPlayer(new Media(mediaUrl));

            mediaView.setMediaPlayer(mediaPlayer);

            //Media player behaviors
            mediaPlayer.setAutoPlay(false);
            mediaPlayer.setMute(true);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        }
    }

    void loadMedia(Exercise exercise) {

    }

    //Play MediaPlayer
    @FXML
    void playClicked(ActionEvent event) {
        mediaPlayer.play();
    }
    //Pause MediaPlayer
    @FXML
    void pauseClicked(ActionEvent event) {
        mediaPlayer.pause();
    }

    //Navigation Function(s):
    @FXML
    void finishWorkoutClicked(ActionEvent event) {
        //service function that handles

        Main.getViewFactory().switchScene("Fxml/Client/dashboard.fxml");
    }

    private URL getMediaResource(String exercise) {
        // TODO: partner to complete — map exercise name to its video resource URL
        return null;
    }

    //Saving Data Function(s)
    @FXML
    void logClicked(ActionEvent event) {
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

        //Service call:
        //insert service function here

        //Allow user to continue
        nextExerciseButton.setDisable(false);
    }

    @FXML
    void nextExerciseClicked(ActionEvent event) {
        //insert updater function here

        currentExerciseIndex++;
    }

}
