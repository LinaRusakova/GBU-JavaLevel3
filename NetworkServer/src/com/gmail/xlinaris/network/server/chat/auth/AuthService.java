package com.gmail.xlinaris.network.server.chat.auth;

import java.sql.SQLException;

public interface AuthService {

    void start();
    String getUsernameByLoginAndPassword(String login, String password) throws SQLException, ClassNotFoundException;

    void stop();


}
