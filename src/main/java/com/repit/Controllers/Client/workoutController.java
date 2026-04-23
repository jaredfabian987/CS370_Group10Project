package com.repit.Controllers.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.layout.StackPane;

public class workoutController {

    @FXML
    private Label currentExerciseLabel;

    @FXML
    private StackPane exerciseMediaPane;

    @FXML
    private Button finishWorkoutButton;

    @FXML
    private Label nextExerciseAmtLabel;

    @FXML
    private Button nextExerciseButton;

    @FXML
    private Label nextExerciseLabel;

    @FXML
    private Spinner<?> repsDoneSpinner;

    @FXML
    private Spinner<?> setsDoneSpinner1;

    @FXML
    private Spinner<?> setsDoneSpinner2;

    @FXML
    private Label workoutAreaLabel;

    @FXML
    private Label workoutCompletionLabel;

    @FXML
    private Label workoutDurationLabel;

    @FXML
    private ProgressBar workoutProgressBar;

    @FXML
    void finishWorkoutClicked(ActionEvent event) {

    }

    @FXML
    void nextExerciseClicked(ActionEvent event) {

    }

}
