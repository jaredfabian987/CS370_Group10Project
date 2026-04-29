package com.repit.Controllers;

import com.repit.Controllers.Client.dashboardController;
import com.repit.Controllers.Client.setupController;
import com.repit.Model.User;
import com.repit.Services.ServiceDispatcher;
import com.repit.main.java.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.slf4j.ILoggerFactory;

public class signupController {

    @FXML
    private PasswordField createPasswordField;

    @FXML
    private TextField createUsernameField;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private Hyperlink loginLinker;

    @FXML
    private PasswordField reenterPasswordField;

    @FXML
    private Button signupButton;

    @FXML
    private ImageView signupImgArea;

    @FXML
    private VBox signupPane;

    @FXML
    private Text signupPaneTitleText;

    //Variables:
    //ServiceDispatcher
    private final ServiceDispatcher serviceDispatcher = new ServiceDispatcher();


    @FXML
    void createAccountClicked(ActionEvent event) {
        //User input from text fields:
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = createUsernameField.getText();
        String password = createPasswordField.getText();
        String confirmPassword = reenterPasswordField.getText();

        //UI error handling:
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Please fill all the fields");
            return;
        }
        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match");
            return;
        }

        //Database error handling:
        boolean registered = serviceDispatcher.handleRegisterRequest(
                username,
                password,
                firstName,
                lastName,
                "12"
        );

        if (!registered) {
            errorLabel.setText("Unable to create account");
            return;
        }

        User loggedUser = serviceDispatcher.handleLoginRequest(username, password);

        errorLabel.setText("");

        //If user is authenicated, load in next page and pass login credentials to next controller

        setupController setupController = Main.getViewFactory().switchScene("Fxml/Client/setup.fxml");
        setupController.setLoggedUser(loggedUser);

        //Main.getViewFactory().switchScene("Fxml/Client/setup.fxml");
    }

    @FXML
    void loginLinkerClicked(ActionEvent event) {
        Main.getViewFactory().switchScene("Fxml/login.fxml");
    }

}
