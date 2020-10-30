package com.gmail.xlinaris.network.client.controllers;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.gmail.xlinaris.network.client.NetworkChatClient;
import com.gmail.xlinaris.network.client.models.Network;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AuthDialogController {

    private @FXML
    Label timerOff;
    private @FXML
    TextField loginField;
    private @FXML
    PasswordField passwordField;
    private @FXML
    Button authButton;

    private Network network;
    private NetworkChatClient clientApp;


    @FXML
    public void executeAuth(ActionEvent actionEvent) {
        String login = loginField.getText();
        String password = passwordField.getText();
        if (login == null || login.isBlank() || password == null || password.isBlank()) {
            NetworkChatClient.showNetworkError("Username and password should be not empty!", "Auth error");
            return;
        }

        String authError = network.sendAuthCommand(login, password);
        if (authError == null) {
            clientApp.openChat();
        } else {
            NetworkChatClient.showNetworkError(authError, "Auth error");
        }
    }


    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setClientApp(NetworkChatClient clientApp) {
        this.clientApp = clientApp;
    }


    private int t = 120;
    public void timeShow() {
        if (t == 0) {
            timerOff.setText("Time out. \nServer closed connection.");
        } else {
            timerOff.setText(t + "s.");
            timerOff.setWrapText(true);
            t--;
        }

    }

}