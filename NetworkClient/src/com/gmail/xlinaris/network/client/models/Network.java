package com.gmail.xlinaris.network.client.models;

import com.gmail.xlinaris.network.client.controllers.AuthDialogController;
import com.gmail.xlinaris.network.clientserver.Command;
import com.gmail.xlinaris.network.clientserver.CommandType;
import com.gmail.xlinaris.network.clientserver.commands.*;
import javafx.application.Platform;
import com.gmail.xlinaris.network.client.controllers.ViewController;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static javafx.application.Platform.*;

public class Network {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8189;

    private final String host;
    private final int port;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket socket;
    private String username;

    public Network() {
        this(SERVER_ADDRESS, SERVER_PORT);
    }

    public Network(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean connect() {
        try {
            socket = new Socket(host, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            return true;
        } catch (IOException e) {
            System.err.println("Соединение не было установлено!");
            e.printStackTrace();
            return false;

        }
    }

    volatile boolean flag = false; //flag for cancel timer
    volatile boolean stopTimerFlag = false; //flag for cancel timer
    volatile boolean flagStop = false; //flag for close client window
    public void timeOffStart(AuthDialogController authDialog, boolean startflag) throws IOException, InterruptedException {
    flag = startflag;
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        int t = 120; //Time for chat authorization (sec).
        @Override
        public void run() {

            if (t >= 0) {
                Platform.runLater(authDialog::timeShow);
                t--;
            } else if (!flag) {
                timer.cancel();
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                timer.cancel();
                flagStop=true;
            }
        }
    };
        timer.schedule(task, new Date(), 1000);

    }

    public String sendAuthCommand(String login, String password) {
        try {
            Command authCommand = Command.authCommand(login, password);
            outputStream.writeObject(authCommand);
            Command command = readCommand();
            if (command == null) {
                return "Failed to read command from server";
            }
            switch (command.getType()) {
                case AUTH_OK: {
                    AuthOkCommandData data = (AuthOkCommandData) command.getData();
                    this.username = data.getUsername();

                    return null;
                }
                case AUTH_ERROR: {
                    AuthErrorCommandData data = (AuthErrorCommandData) command.getData();
                    return data.getErrorMessage();
                }
                default:
                    return "Unknown type of command from server:" + command.getType();
            }
        } catch (
                IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public void sendMessage(String message) throws IOException {
        Command command = Command.publicMessageCommand(username, message);
        sendCommand(command);
    }

    private void sendCommand(Command command) throws IOException {
        outputStream.writeObject(command);
    }

    public void sendPrivateMessage(String message, String recipient) throws IOException {
        Command command = Command.privateMessageCommand(recipient, message);
        sendCommand(command);
    }

    public void waitMessages(ViewController viewController) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {

                        Command command = readCommand();
                        if (command == null) {
                            viewController.showError("Server error", "Invalid command from server!");
                            continue;
                        }
                        switch (command.getType()) {
                            case INFO_MESSAGE: {
                                MessageInfoCommandData data = (MessageInfoCommandData) command.getData();
                                String message = data.getMessage();
                                String sender = data.getSender();
                                String formattedMsg = sender != null ? String.format("%s: %s", sender, message) : message;
                                runLater(() -> {
                                    viewController.appendMessage(formattedMsg);
                                });
                                break;
                            }
                            case ERROR: {
                                ErrorCommandData data = (ErrorCommandData) command.getData();
                                String errorMessage = data.getErrorMessage();
                                runLater(() -> {
                                    viewController.showError("Server error", errorMessage);
                                });
                                break;
                            }
                            case UPDATE_USERS_LIST: {
                                UpdateUsersListCommandData data = (UpdateUsersListCommandData) command.getData();
                                runLater(() -> {
                                    viewController.updateUsers(data.getUsers());
                                });
                                break;
                            }
                            case TIMEOUT: {
                                timeOutCommandData data = (timeOutCommandData) command.getData();
                                flag = true;
                                if (flagStop) {Platform.exit();
                                    socket.close();}
                                break;
                            }
                            default:
                                runLater(() -> {
                                    viewController.showError("Server error", command.getType().toString());
                                });
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Соединение было потеряно!");
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    private Command readCommand() throws IOException {
        try {
            return (Command) inputStream.readObject();

        } catch (ClassNotFoundException e) {
            String errorMessage = "Unknown type of object from client.";
            System.err.println(errorMessage);
            e.printStackTrace();
            return null;
        }
    }
}