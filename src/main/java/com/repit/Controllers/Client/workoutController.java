package com.repit.Controllers.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.StackPane;

import java.util.function.UnaryOperator;

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
    private Spinner<Integer> repsDoneSpinner;

    @FXML
    private Spinner<Integer> setsDoneSpinner1;

    @FXML
    private Spinner<Integer> setsDoneSpinner2;

    @FXML
    private Label workoutAreaLabel;

    @FXML
    private Label workoutCompletionLabel;

    @FXML
    private Label workoutDurationLabel;

    @FXML
    private ProgressBar workoutProgressBar;

    @FXML
    public void initialize() {
    }

    @FXML
    void finishWorkoutClicked(ActionEvent event) {

    }

    @FXML
    void nextExerciseClicked(ActionEvent event) {

    }
}
