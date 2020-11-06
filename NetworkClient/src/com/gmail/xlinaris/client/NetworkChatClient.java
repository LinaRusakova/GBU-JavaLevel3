package com.gmail.xlinaris.client;

import com.gmail.xlinaris.client.controllers.AuthDialogController;
import com.gmail.xlinaris.client.controllers.ViewController;
import com.gmail.xlinaris.client.models.Network;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;


public class NetworkChatClient extends Application {

    //    public static final List<String> USERS_TEST_DATA = List.of("Oleg", "Alexey", "Peter");
    public static final List<String> USERS_TEST_DATA = List.of();

    private Stage primaryStage;
    private Stage authDialogStage;
    private Network network;
    private ViewController viewController;


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        network = new Network();
        if (!network.connect()) {
            showNetworkError("", "Failed to connect to server");
            return;
        }

        openAuthDialog(primaryStage);
        AuthDialogController authController;
        createChatDialog(primaryStage);

    }

    private void createChatDialog(Stage primaryStage) throws java.io.IOException {
        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(NetworkChatClient.class.getResource("views/view.fxml"));

        Parent root = mainLoader.load();

        primaryStage.setTitle("Messenger");
        primaryStage.setScene(new Scene(root, 600, 400));

        viewController = mainLoader.getController();
        viewController.setNetwork(network);
        ViewController.writeChatToFileHistory("***Клиент подключен***"); //создаем файл истории чата на тот случай, если он был удален пользователем.
        viewController.readChatFromFileHistory(100); //загружаем в чат историю сообщений (100 записей)

        primaryStage.setOnCloseRequest(event -> network.close());
    }

    private void openAuthDialog(Stage primaryStage) throws java.io.IOException {
        FXMLLoader authLoader = new FXMLLoader();

        authLoader.setLocation(NetworkChatClient.class.getResource("views/authDialog.fxml"));
        Parent authDialogPanel = authLoader.load();
        authDialogStage = new Stage();

        authDialogStage.setTitle("Chat authorization");
        authDialogStage.initModality(Modality.WINDOW_MODAL);
        authDialogStage.initOwner(primaryStage);
        Scene scene = new Scene(authDialogPanel);
        authDialogStage.setScene(scene);
        authDialogStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        authDialogStage.show();
        AuthDialogController authController = authLoader.getController();
        authController.setNetwork(network);
        network.timeOffStart(authController, false);
        authController.setClientApp(this);


    }

    public static void showNetworkError(String errorDetails, String errorTitle) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Network Error");
        alert.setHeaderText(errorTitle);
        alert.setContentText(errorDetails);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void openChat() {
        authDialogStage.close();
        primaryStage.show();

        primaryStage.setTitle("Chat client:" + network.getUsername());
        network.waitMessages(viewController);
    }
}