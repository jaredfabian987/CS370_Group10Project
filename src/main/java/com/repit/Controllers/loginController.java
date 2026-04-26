package com.repit.Controllers;

import com.repit.main.java.Main;
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


    //Loads signup page
    @FXML
    void createAccountLinkerClicked(ActionEvent event) {
        Main.getViewFactory().switchScene("Fxml/signup.fxml");
    }

    //Loads
    @FXML
    void userLogin(ActionEvent event) {
        //User input from username and password Text fields
        String userName = username.getText();
        String passWord = password.getText();

        //Prevents reading of empty fields in authentication stage
        if (userName.isEmpty() || passWord.isEmpty()) {
            label.setText("Please fill all the fields");
            return;
        }

        //If user is authenicated, load dashboard page
        Main.getViewFactory().switchScene("Fxml/Client/dashboard.fxml");
    }

}
