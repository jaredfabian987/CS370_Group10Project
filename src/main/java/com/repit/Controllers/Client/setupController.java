package com.repit.Controllers.Client;

import com.repit.Model.FitnessProfile;
import com.repit.Model.User;
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
        //setLoggedUser(loggedUser);
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

    @FXML
    private void saveSetupButtonAction(ActionEvent event) {

        // guard — should never happen but prevents a NullPointerException if
        // setLoggedUser() was never called before this screen loaded
        if (loggedUser == null) {
            errorLabel1.setText("Session error — please log in again.");
            return;
        }

        // Step 1: validate — make sure at least one day is checked and all checked days have a time
        if (!checkBoxTest()) { return; }

        // Step 2: read fitness level from the radio buttons
        // beginnerLevelRadio is selected by default in the FXML so BEG is the fallback
        FitnessProfile.FitnessLevel level;
        if (intermediateLevelRadio.isSelected()) {
            level = FitnessProfile.FitnessLevel.INT;
        } else if (proficientLevelRadio.isSelected()) {
            level = FitnessProfile.FitnessLevel.ADV;
        } else {
            level = FitnessProfile.FitnessLevel.BEG;
        }

        // Step 3: read fitness goal from the radio buttons
        // weightLossGoalRadio is selected by default in the FXML so MAINTAIN is the fallback
        FitnessProfile.FitnessGoal goal;
        if (muscleGainGoalRadio.isSelected()) {
            goal = FitnessProfile.FitnessGoal.MUSCLE;
        } else {
            goal = FitnessProfile.FitnessGoal.MAINTAIN;
        }

        // Step 4: read the checked days and their selected times
        // daysPerWeek = count of checked days
        // minsAvailablePerWorkout = the highest minute value across all checked days
        // we use the max so that the exercise queue never gets cut short on the user's
        // longest available day
        int daysPerWeek = 0;
        int maxMinutes = 0;

        if (mondayCheckBox.isSelected()) {
            daysPerWeek++;
            int mins = parseMinutes(mondayTimeComboBox.getValue());
            if (mins > maxMinutes) maxMinutes = mins;
        }
        if (tuesdayCheckBox.isSelected()) {
            daysPerWeek++;
            int mins = parseMinutes(tuesdayTimeComboBox.getValue());
            if (mins > maxMinutes) maxMinutes = mins;
        }
        if (wednesdayCheckBox.isSelected()) {
            daysPerWeek++;
            int mins = parseMinutes(wednesdayTimeComboBox.getValue());
            if (mins > maxMinutes) maxMinutes = mins;
        }
        if (thursdayCheckBox.isSelected()) {
            daysPerWeek++;
            int mins = parseMinutes(thursdayTimeComboBox.getValue());
            if (mins > maxMinutes) maxMinutes = mins;
        }
        if (fridayCheckBox.isSelected()) {
            daysPerWeek++;
            int mins = parseMinutes(fridayTimeComboBox.getValue());
            if (mins > maxMinutes) maxMinutes = mins;
        }
        if (saturdayCheckBox.isSelected()) {
            daysPerWeek++;
            int mins = parseMinutes(saturdayTimeComboBox.getValue());
            if (mins > maxMinutes) maxMinutes = mins;
        }
        if (sundayCheckBox.isSelected()) {
            daysPerWeek++;
            int mins = parseMinutes(sundayTimeComboBox.getValue());
            if (mins > maxMinutes) maxMinutes = mins;
        }

        // Step 5: build the FitnessProfile
        // weight and height are 0.0 — the setup form does not collect them
        FitnessProfile profile = new FitnessProfile(
                loggedUser.getUserId(),
                0.0,
                0.0,
                daysPerWeek,
                maxMinutes,
                level,
                goal
        );

        // Step 6: save via the service dispatcher
        // if the user already has a profile (re-doing setup), update it instead of inserting
        // a plain INSERT would fail with a UNIQUE constraint error on userId
        boolean saved;
        if (serviceDispatcher.handleGetFitnessProfileRequest(loggedUser.getUserId()) != null) {
            saved = serviceDispatcher.handleUpdateProfileRequest(profile);
        } else {
            saved = serviceDispatcher.handleSaveProfileRequest(profile);
        }
        if (!saved) {
            errorLabel1.setText("Error saving setup — please try again.");
            return;
        }

        // Step 7: switch to the dashboard and pass the logged-in user
        // capturing the returned controller lets us call setLoggedUser() so the
        // dashboard knows whose data to load
        errorLabel1.setText("");
        dashboardController controller = Main.getViewFactory().switchScene("Fxml/Client/dashboard.fxml");
        controller.setLoggedUser(loggedUser);
    }

    // Parses a time string from the combo box into an integer number of minutes.
    // The combo box values look like "30 minutes" — we split on the space and
    // parse just the number. Returns 30 as a safe default if parsing fails.
    private int parseMinutes(String timeString) {
        try {
            return Integer.parseInt(timeString.split(" ")[0]);
        } catch (Exception e) {
            return 30;
        }
    }
}
