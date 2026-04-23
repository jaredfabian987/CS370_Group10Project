package com.repit.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class loginController {

    @FXML
    private Button button;

    @FXML
    private Label label;

    @FXML
    private AnchorPane loginFrame;

    @FXML
    private VBox loginPane;

    @FXML
    private Hyperlink loginPaneLinker;

    @FXML
    private Text loginPaneTitleText;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    @FXML
    void createAccountLinkerClicked(ActionEvent event) {
        loadScene(event, "Fxml/signup.fxml");
    }

    private void loadScene(ActionEvent event, String s) {
    }

    @FXML
    void userLogin(ActionEvent event) {
        String userName = username.getText();
        String passWord = password.getText();

        //Prevents reading of empty fields in authentication stage
        if (userName.isEmpty() || passWord.isEmpty()) {
            label.setText("Please fill all the fields");
            return;
        }





        loadScene(event, "Fxml/Client/dashboard.fxml");
    }

}
