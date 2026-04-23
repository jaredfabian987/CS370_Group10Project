package com.repit.Controllers;

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

    }

    @FXML
    void loginLinkerClicked(ActionEvent event) {

    }

}
