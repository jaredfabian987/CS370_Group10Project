package com.repit.Controllers.Client;

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
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
    ServiceDispatcher serviceDispatcher = new ServiceDispatcher();

    // logged-in user — set by dashboardController immediately after switchScene()
    private com.repit.Model.User loggedUser;

    public void setLoggedUser(com.repit.Model.User loggedUser) {
        this.loggedUser = loggedUser;
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
        workoutDurationLabel.setText("Estimated duration: -- minutes");
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

    public void setStateLoaded(){
        workoutDurationLabel.setText("Estimated duration: Loading...");
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

    }

}
