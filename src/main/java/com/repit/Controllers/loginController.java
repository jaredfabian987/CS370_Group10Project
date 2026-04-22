package com.repit.Controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;

public class loginController {
    public  loginController(){
    }
    @FXML
    private Button button;
    @FXML
    private Label label;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    public void userLogin(ActionEvent event) throws IOException {
        checkLogin();
    }

    private void checkLogin() throws IOException {
        //Insert Password Checker here ##############################################################
        //placeholder function
        if(username.getText().equals("") && password.getText().equals("")){
            //
        }
        //If Either Username OR Password Fields are empty
        else if (username.getText().isEmpty() || password.getText().equals("")){

        }
    }
}
