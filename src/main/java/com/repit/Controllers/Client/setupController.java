package com.repit.Controllers.Client;

import com.repit.Model.Availability;
import com.repit.Model.FitnessProfile;
import com.repit.Model.User;
import com.repit.Services.PlannerService;
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

import java.net.URL;
import java.util.ResourceBundle;

public class setupController implements Initializable {

    @FXML
    private Label errorLabel1;
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

    //Variables:
    //Service dispatcher
    private final ServiceDispatcher serviceDispatcher = new ServiceDispatcher();
    private User loggedUser;
    private FitnessProfile fitnessProfile;


    //Set logged user
    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

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


    //Binds a Checkox to its respective ComboBox, toggling the Checkbox enables Combobox for selection
    private void bindDayToggle(CheckBox dayCheckBox, ComboBox<String> timeComboBox) {
        timeComboBox.setDisable(!dayCheckBox.isSelected());

        dayCheckBox.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
            timeComboBox.setDisable(!isSelected);

            if (!isSelected) {
                timeComboBox.getSelectionModel().clearSelection();
            }
        });
    }

    //Helper function that checks for valid inputs and updates errLabel with respective error message
    public boolean checkBoxTest1(CheckBox[] dayCheckBox, ComboBox[] timeComboBox) {
        boolean anyDaySelected = false;
        boolean allTimeSelected = true;
        String [] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        String errorMsg = "";

        for (int i  = 0; i < dayCheckBox.length; i++) {
            if (dayCheckBox[i].isSelected()) {
                anyDaySelected = true;
                if (timeComboBox[i].getSelectionModel().isEmpty()) {
                    errorMsg += days[i] + " ";
                    allTimeSelected = false;
                }
            }
        }
        if (!allTimeSelected) {
            errorMsg += "time(s) need to be selected";
            errorLabel1.setText(errorMsg);
            return false;
        }

        if (!anyDaySelected) {
            errorLabel1.setText("Select at least ONE day and time");
            return false;
        }
        return true;
    }

    /*
    public boolean checkBoxTest() {
        boolean anyDaySelected = false;
        boolean allTimeSelected = true;
        String errorMsg = "";

        if (mondayCheckBox.isSelected()) {
            anyDaySelected = true;
            if (mondayTimeComboBox.getSelectionModel().isEmpty()) {
                //errorLabel1.setText("Monday time cannot be empty");
                errorMsg += "Monday ";
                allTimeSelected = false;
            }
        }
        if (tuesdayCheckBox.isSelected()) {
            anyDaySelected = true;
            if (tuesdayTimeComboBox.getSelectionModel().isEmpty()) {
                errorMsg += "Tuesday ";
                allTimeSelected = false;            }
        }
        if (wednesdayCheckBox.isSelected()) {
            anyDaySelected = true;
            if (wednesdayTimeComboBox.getSelectionModel().isEmpty()) {
                errorMsg += "Wednesday ";
                allTimeSelected = false;            }
        }
        if (thursdayCheckBox.isSelected()) {
            anyDaySelected = true;
            if (thursdayTimeComboBox.getSelectionModel().isEmpty()) {
                errorMsg += "Thursday ";
                allTimeSelected = false;            }
        }
        if (fridayCheckBox.isSelected()) {
            anyDaySelected = true;
            if (fridayTimeComboBox.getSelectionModel().isEmpty()) {
                errorMsg += "Friday ";
                allTimeSelected = false;            }
        }
        if (saturdayCheckBox.isSelected()) {
            anyDaySelected = true;
            if (saturdayTimeComboBox.getSelectionModel().isEmpty()) {
                errorMsg += "Saturday ";
                allTimeSelected = false;            }
        }
        if (sundayCheckBox.isSelected()) {
            anyDaySelected = true;
            if (sundayTimeComboBox.getSelectionModel().isEmpty()) {
                errorMsg += "Sunday ";
                allTimeSelected = false;
            }
        }

        if (!allTimeSelected) {
            errorMsg += "time(s) need to be selected";
            errorLabel1.setText(errorMsg);
            return false;
        }

        if (!anyDaySelected) {
            errorLabel1.setText("Select at least ONE day and time");
            return false;
        }
        return true;
    }
    */

    private void saveSetupInformation(){

    }

    @FXML
    private void saveSetupButtonAction(ActionEvent event) {
        CheckBox [] dayCheckBox = {mondayCheckBox, tuesdayCheckBox, wednesdayCheckBox,  thursdayCheckBox, fridayCheckBox, saturdayCheckBox,  sundayCheckBox};
        ComboBox [] timeComboBox = {mondayTimeComboBox, tuesdayTimeComboBox, wednesdayTimeComboBox, thursdayTimeComboBox, fridayTimeComboBox, saturdayTimeComboBox,  sundayTimeComboBox};
        if (!checkBoxTest1(dayCheckBox, timeComboBox)) { return; }

        //FitnessProfile.FitnessLevel level =

        //FitnessProfile.FitnessLevel =


        errorLabel1.setText("");
        Main.getViewFactory().switchScene("Fxml/Client/dashboard.fxml");
    }
}
