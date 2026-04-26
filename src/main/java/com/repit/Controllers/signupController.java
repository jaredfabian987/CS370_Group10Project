package com.repit.Controllers;

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

public class signupController {

    @FXML
    private PasswordField createPasswordField;

    @FXML
    private TextField createUsernameField;

    @FXML
    private Label errorLabel;

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

    @FXML
    void createAccountClicked(ActionEvent event) {
        //User input from text fields
        String username = createUsernameField.getText();
        String password = createPasswordField.getText();
        String confirmPassword = reenterPasswordField.getText();

        //Conditional Statements:
        if (createUsernameField.getText().isEmpty() || createPasswordField.getText().isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Please fill all the fields");
        }
        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match");
        }

        //add some user creation service later
        //add user authentication service later

        errorLabel.setText("");
        Main.getViewFactory().switchScene("Fxml/Client/setup.fxml");
    }

    @FXML
    void loginLinkerClicked(ActionEvent event) {
        Main.getViewFactory().switchScene("Fxml/login.fxml");
    }

}
