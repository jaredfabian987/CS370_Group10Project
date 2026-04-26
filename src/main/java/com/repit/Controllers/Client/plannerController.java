package com.repit.Controllers.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class plannerController {

    @FXML
    private Label availabiltiyLabel;

    @FXML
    private ToggleGroup fitnessGoalToggleGroup;

    @FXML
    private CheckBox fridayCheckBox;

    @FXML
    private ComboBox<?> fridayTimeComboBox;

    @FXML
    private Button generatePlanButton;

    @FXML
    private CheckBox mondayCheckBox;

    @FXML
    private ComboBox<?> mondayTimeComboBox;

    @FXML
    private RadioButton muscleGainRadialButton;

    @FXML
    private CheckBox saturdayCheckBox;

    @FXML
    private ComboBox<?> saturdayTimeComboBox;

    @FXML
    private Button savePlanButton;

    @FXML
    private CheckBox sundayCheckBox;

    @FXML
    private ComboBox<?> sundayTimeComboBox;

    @FXML
    private CheckBox thursdayCheckBox;

    @FXML
    private ComboBox<?> thursdayTimeComboBox;

    @FXML
    private CheckBox tuesdayCheckBox;

    @FXML
    private ComboBox<?> tuesdayTimeComboBox;

    @FXML
    private CheckBox wednesdayCheckBox;

    @FXML
    private ComboBox<?> wednesdayTimeComboBox;

    @FXML
    private RadioButton weightLossRadialButton;

    @FXML
    void generatePlanButtonClicked(ActionEvent event) {

    }

}
