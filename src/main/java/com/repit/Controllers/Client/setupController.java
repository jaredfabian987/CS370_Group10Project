package com.repit.Controllers.Client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class setupController implements Initializable {

    @FXML
    private RadioButton beginnerLevelRadio;

    @FXML
    private ToggleGroup fitnessGoalToggleGroup;

    @FXML
    private ToggleGroup fitnessLevelToggleGroup;

    @FXML
    private CheckBox fridayCheckBox;

    @FXML
    private ComboBox<String> fridayTimeComboBox;

    @FXML
    private RadioButton intermediateLevelRadio;

    @FXML
    private CheckBox mondayCheckBox;

    @FXML
    private ComboBox<String> mondayTimeComboBox;

    @FXML
    private RadioButton muscleGainGoalRadio;

    @FXML
    private RadioButton proficientLevelRadio;

    @FXML
    private CheckBox saturdayCheckBox;

    @FXML
    private ComboBox<String> saturdayTimeComboBox;

    @FXML
    private Button saveSetupButton;

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
    private RadioButton weightLossGoalRadio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bindDayToggle(mondayCheckBox, mondayTimeComboBox);
        bindDayToggle(tuesdayCheckBox, tuesdayTimeComboBox);
        bindDayToggle(wednesdayCheckBox, wednesdayTimeComboBox);
        bindDayToggle(thursdayCheckBox, thursdayTimeComboBox);
        bindDayToggle(fridayCheckBox, fridayTimeComboBox);
        bindDayToggle(saturdayCheckBox, saturdayTimeComboBox);
        bindDayToggle(sundayCheckBox, sundayTimeComboBox);
    }

    //Binds a Checkox to its respective ComboBox, toggling the Checkbox
    private void bindDayToggle(CheckBox dayCheckBox, ComboBox<String> timeComboBox) {
        timeComboBox.setDisable(!dayCheckBox.isSelected());

        dayCheckBox.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
            timeComboBox.setDisable(!isSelected);

            if (!isSelected) {
                timeComboBox.getSelectionModel().clearSelection();
            }
        });
    }

    @FXML
    private void saveSetupButtonAction(ActionEvent event) {

    }
}
