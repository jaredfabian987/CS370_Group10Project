package com.repit.Controllers.Client;

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

import java.net.URL;
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
    void generatePlanButtonClicked(ActionEvent event) {

    }

    @FXML
    void finishPlannerButtonClicked(ActionEvent event) {
        Main.getViewFactory().switchScene("Fxml/Client/dashboard.fxml");
    }

}
