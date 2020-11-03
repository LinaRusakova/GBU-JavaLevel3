package com.gmail.xlinaris.network.server.chat.handler;

import com.gmail.xlinaris.network.clientserver.Command;
import com.gmail.xlinaris.network.clientserver.CommandType;
import com.gmail.xlinaris.network.clientserver.commands.AuthCommandData;
import com.gmail.xlinaris.network.clientserver.commands.PrivateMessageCommandData;
import com.gmail.xlinaris.network.clientserver.commands.PublicMessageCommandData;
import com.gmail.xlinaris.network.server.chat.MyServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

import static com.gmail.xlinaris.network.clientserver.Command.*;


public class ClientHandler {

    private final MyServer myServer;
    private final Socket clientSocket;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String username;

    public ClientHandler(MyServer myServer, Socket clientSocket) {
        this.myServer = myServer;
        this.clientSocket = clientSocket;
    }


    public void handle() throws IOException {
        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());

        Thread thread4ConnectiontToDB = new Thread(new Runnable() {
            public void run() //Этот метод будет выполняться в побочном потоке
            {
                try {
                    authentication();
                    readMessages();
                    System.out.println("Привет из побочного потока!");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        thread4ConnectiontToDB.setDaemon(true);
        thread4ConnectiontToDB.start();    //Запуск потока


        //// Создаем отдельный поток для авторизации пользователей




    }

    private void checkTimeout() throws IOException {
        out.writeObject(Command.timeOutCommand(true));
//        myServer.checkTimeOut(true);
    }


    private void readMessages() throws IOException {
        while (true) {

            Command command = readCommand();
            if (command == null) {
                continue;
            }
            switch (command.getType()) {
                case END: {
                    break;
                }
                case PRIVATE_MESSAGE: {
                    PrivateMessageCommandData data = (PrivateMessageCommandData) command.getData();
                    String recipient = data.getReceiver();
                    String privateMessage = data.getMessage();
                    myServer.sendPrivateMessage(recipient, messageInfoCommand(privateMessage, username));
                    break;
                }
                case PUBLIC_MESSAGE: {
                    PublicMessageCommandData data = (PublicMessageCommandData) command.getData();
                    String sender = data.getSender();
                    String message = data.getMessage();
                    myServer.broadcastMessage(this, messageInfoCommand(message, sender));
                    break;
                }
                default:
                    System.err.println("Unknown type of command:" + command.getType());
            }
        }
    }

    private Command readCommand() throws IOException {
        try {
            return (Command) in.readObject();

        } catch (ClassNotFoundException e) {
            String errorMessage = "Unknown type of object from client.";
            System.err.println(errorMessage);
            e.printStackTrace();
            sendMessage(errorCommand(errorMessage));
            return null;
        }
    }

    private void authentication() throws IOException, SQLException, ClassNotFoundException {

        while (true) {
            Command command = readCommand();
            if (command == null) {
                continue;
            }
            if (command.getType() == CommandType.AUTH) {
                boolean isSuccessAuth = processAuthCommand(command);
                if (isSuccessAuth) {
                    break;
                }
            } else {
                sendMessage(authErrorCommand("Auth command is required."));
            }
        }
    }


    private boolean processAuthCommand(Command command) throws IOException, SQLException, ClassNotFoundException {
        AuthCommandData cmdData = (AuthCommandData) command.getData();
        String login = cmdData.getLogin();
        String password = cmdData.getPassword();
        this.username = myServer.getAuthService().getUsernameByLoginAndPassword(login, password);
        if (username != null) {
            if (myServer.isNicknameAlreadyBusy(username)) {
                sendMessage(authErrorCommand("Login and password are already used!"));
                return false;
            }
            sendMessage(authOkCommand(username));

            String message = username + " joined to chat!";
            myServer.broadcastMessage(this, messageInfoCommand(message, null));
            myServer.subscribe(this);
//            myServer.checkTimeOut(username);

            return true;
        } else {
            sendMessage(authErrorCommand(" Login and/or password are invalid! Please, try again"));
            return false;
        }
    }

    private void closeConnection() throws IOException {
        myServer.unsubscribe(this);
        clientSocket.close();
    }


    public void sendMessage(Command command) throws IOException {
        out.writeObject(command);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}