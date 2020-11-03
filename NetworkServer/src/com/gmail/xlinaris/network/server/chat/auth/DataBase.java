package com.gmail.xlinaris.network.server.chat.auth;

import com.gmail.xlinaris.network.server.chat.User;

import java.sql.*;
import java.util.List;

public class DataBase {
    public static Connection conn;
    public static Statement statement;
    public static ResultSet resSet;

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public static void Conn() throws ClassNotFoundException, SQLException {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:Users.s3db");
        System.out.println("База Подключена!");
    }

    // --------Создание таблицы--------
    public static void CreateDB() throws SQLException {
        statement = conn.createStatement();
        statement.execute("CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' text, 'login' text, 'password' text, 'text_message' message_text );");
        System.out.println("Таблица создана или уже существует.");
        WriteDB();
//        resSet = statmt.executeQuery("SELECT * FROM 'users'");
//        while (!resSet.next()) {
//            int id = resSet.getInt("id");
//            System.out.println(id);
//            if (id <= 3) {
//                WriteDB();
//            } ;
//        }
    }

    // --------Заполнение таблицы--------
    public static void WriteDB() throws SQLException {
//
//        statmt.execute("INSERT INTO 'users' ('name', 'login','password') VALUES ('Petya','login1','pass1'); ");
//        statmt.execute("INSERT INTO 'users' ('name', 'login','password') VALUES ('Vasya', 'login2','pass2'); ");
//        statmt.execute("INSERT INTO 'users' ('name', 'login','password') VALUES ('Masha', 'login3','pass3'); ");


        System.out.println("Таблица заполнена");
    }

    // -------- Вывод таблицы--------
    public static List<User> ReadDB() throws SQLException {
        resSet = statement.executeQuery("SELECT * FROM users");
        List<User> USERS = null;
        int i = 0;
        while (resSet.next()) {
            int id = resSet.getInt("id");
            String name = resSet.getString("name");
            String login = resSet.getString("login");
            String password = resSet.getString("password");
            USERS.add(i, new User(login, password, name));
            System.out.println("ID = " + id);
            System.out.println("name = " + name);
            System.out.println("login = " + login);
            System.out.println("password = " + password);
            System.out.println();
            i++;
        }

        System.out.println("Таблица выведена");

        return USERS;
//        return null;
    }

    // --------Закрытие--------
    public static void CloseDB() throws ClassNotFoundException, SQLException {
        statement.close();
        resSet.close();
        conn.close();


        System.out.println("Соединения закрыты");
    }

}

