package com.gmail.xlinaris.network.server.chat.auth;

import com.gmail.xlinaris.network.server.chat.User;

import java.sql.SQLException;
import java.util.List;

public class AuthWithDataBase implements AuthService {

    private static List<User> USERS = null;

    //    private static final List<User> USERS = List.of(
//            new User("login1", "pass1", "Oleg"),
//            new User("login2", "pass2", "Alexey"),
//            new User("login3", "pass3", "Peter")
//    );
    @Override
    public void start() {
        try {
            DataBase.Conn(); //если база не создана или попытка соединения не удалась, то создаем базу и вписываем начальные значения
            DataBase.CreateDB();
           // DataBase.WriteDB();
            USERS = DataBase.ReadDB(); // TO DO привести к списку LIST
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) throws SQLException, ClassNotFoundException {
        DataBase.ReadDB();

        for (User user : USERS) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                return user.getUsername();
            }
        }

        return null;
    }

    @Override
    public void stop() {

    }
}
