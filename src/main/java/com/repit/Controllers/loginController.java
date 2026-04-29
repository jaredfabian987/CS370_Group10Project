package com.repit.Controllers;

import com.repit.Controllers.Client.dashboardController;
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

    ServiceDispatcher serviceDispatcher =  new ServiceDispatcher();

    //Loads signup page
    @FXML
    void createAccountLinkerClicked(ActionEvent event) {
        Main.getViewFactory().switchScene("Fxml/signup.fxml");
    }

    //Loads
    @FXML
    void userLogin(ActionEvent event) {
        //User input:
        String userName = username.getText();
        String passWord = password.getText();

        //Prevents reading of empty fields in authentication stage
        if (userName.isEmpty() || passWord.isEmpty()) {
            label.setText("Please fill all the fields");
            return;
        }

        //User authentication:
        User loggedUser = serviceDispatcher.handleLoginRequest(userName, passWord);

        //Prevents user from progressing if authentication fails
        //Comment in later
        if (loggedUser == null){
            label.setText("Wrong username or password");
            return;
        }


        //If user is authenicated, load in next page and pass login credentials to next controller
        // Comment back in later

        dashboardController dashboardController = Main.getViewFactory().switchScene("Fxml/Client/dashboard.fxml");
        dashboardController.setLoggedUser(loggedUser);

        //Main.getViewFactory().switchScene("Fxml/Client/dashboard.fxml");
    }

}
