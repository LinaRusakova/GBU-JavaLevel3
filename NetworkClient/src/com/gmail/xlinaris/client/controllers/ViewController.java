package com.gmail.xlinaris.client.controllers;

import com.gmail.xlinaris.client.models.Network;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import com.gmail.xlinaris.client.NetworkChatClient;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;


public class ViewController {

    @FXML
    public ListView<String> usersList;
    public TextField newNickSet;
    public Button changeNickButton;

    @FXML
    private Button sendButton;
    @FXML
    private TextArea chatHistory;
    @FXML
    private TextField textField;
    @FXML
    private Label target;
    private Network network;

    private String selectedRecipient;
    private String privateMessage;

    @FXML
    public void initialize() {

//        usersList.setItems(FXCollections.observableArrayList(NetworkChatClient.USERS_TEST_DATA));
        usersList.setItems(FXCollections.observableArrayList(NetworkChatClient.USERS_TEST_DATA));
        sendButton.setOnAction(event -> sendMessage());
        textField.setOnAction(event -> sendMessage());
//        changeNickButton.setOnAction(event -> sendNick());
//        newNickSet.setOnAction(event -> sendNick());
//        usersList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//                selectedRecipient = newValue;
//        });

        usersList.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = usersList.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {

                usersList.requestFocus();

                if (!cell.isEmpty()) {

                    int index = cell.getIndex();
                    if (selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                        selectedRecipient = null;

                    } else {
                        selectionModel.select(index);
                        selectedRecipient = cell.getItem();

                        privateMessage = "Private message for @" + selectedRecipient + ": ";
                        target.setWrapText(true);
                        target.setText(privateMessage); //String into textfield when answer to user from list of users.
                        cell.setItem("New user");

                        selectionModel.clearSelection(index);
                    }
                    event.consume();
                }
            });
            return cell;
        });
    }


    private void sendMessage() {

        String message = textField.getText();
        if (!message.equals("")) {
            appendMessage("Я: " + message);
            textField.clear();
            target.setText("Message for all: ");

            try {
                if (selectedRecipient != null) {
                    network.sendPrivateMessage(message, selectedRecipient);
                } else {
                    network.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                String errorMessage = "Failed to send message";
                NetworkChatClient.showNetworkError(e.getMessage(), errorMessage);
            }
            selectedRecipient = null;
        }
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void appendMessage(String message) {
        String history4FileSave = "";
        String timestamp = DateFormat.getInstance().format(new Date());

        chatHistory.appendText(timestamp);
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(message);
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(System.lineSeparator());
        history4FileSave = history4FileSave.concat(timestamp + "\n" + message);

        // method writeChatToFileHistory(history4FileSave);
        writeChatToFileHistory(history4FileSave);
    }


//method readChatFromFileHistory(number of LastLinesOfHistoryChat)

    public void readChatFromFileHistory(int lastLines) {

        try {
            List<String> history = Files.readAllLines(Paths.get("ChatHistory.txt"));
            int startReadLine = Math.max(history.size(), lastLines) - lastLines;
            startReadLine = Math.max(0, startReadLine - 1);
            for (int j = startReadLine; j < history.size(); j++) {
                System.out.println(history.get(j));
                chatHistory.appendText(history.get(j));
                chatHistory.appendText(System.lineSeparator());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void writeChatToFileHistory(String data) {
        File file = null;

        file = new File("ChatHistory.txt");
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;

        String dataWithNewLine = data + System.getProperty("line.separator");
        try {
            fileWriter = new FileWriter(file, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(dataWithNewLine);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void showError(String title, String message) {
        NetworkChatClient.showNetworkError(message, title);
    }

    public void updateUsers(List<String> users) {
        usersList.setItems(FXCollections.observableArrayList(users));
    }

    public void changeNick(ActionEvent actionEvent) {
    }

    public void newNickSet(ActionEvent actionEvent) {
    }
}