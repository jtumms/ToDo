package com.tumms;

import org.h2.tools.Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter name:");
            String name = scanner.nextLine();
            System.out.println("Enter password:");
            String password = scanner.nextLine();

            User user = selectUser(conn, name);
            if (user == null) {
                insertUser(conn, name, password);
                user = selectUser(conn, name);
            }
            else if (!password.equals(user.password)) {
                System.out.println("wrong password!");
                continue;
            }


            boolean isLoggedIn = true;
            while (isLoggedIn) {
                System.out.println("1. Create to-do item");
                System.out.println("2. Check/uncheck to-do item");
                System.out.println("3. List all to-do items");
                System.out.println("4. Delete item");
                System.out.println("5. Logout");

                String option = scanner.nextLine();

                switch (option) {
                    case "1":
                        addTodo(conn, scanner, user);
                        break;
                    case "2":
                        toggleTodo(conn, scanner);
                        break;
                    case "3":
                        listTodos(conn, user);
                        break;
                    case "4":
                        deleteToDo(conn, scanner);
                        break;
                    case "5":
                        isLoggedIn = false;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            }
        }
    }

    public static void addTodo(Connection conn, Scanner scanner,User user) throws SQLException {
        System.out.println("Enter your to-do item:");
        String text = scanner.nextLine();


        PreparedStatement stmt = conn.prepareStatement("INSERT INTO to_dos VALUES (NULL, ?, ?, ?)");
        stmt.setString(1, text);
        stmt.setBoolean(2, false);
        stmt.setInt(3, user.id);
        stmt.execute();

    }

    public static void toggleTodo(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Which item do you want to toggle?");
        int i = Integer.valueOf(scanner.nextLine());

        PreparedStatement stmt = conn.prepareStatement("UPDATE to_dos SET is_done = NOT is_done WHERE id = ?");
        stmt.setInt(1, i);
        stmt.execute();
    }

    public static void listTodos(Connection conn, User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM to_dos INNER JOIN users ON to_dos.user_id = users.id");
        ResultSet results = stmt.executeQuery();
        ArrayList<Item> items = new ArrayList<>();
        while (results.next()){
            int id = results.getInt("to_dos.id");
            String text = results.getString("to_dos.text");
            boolean isDone = results.getBoolean("to_dos.is_done");
            String name = results.getString("users.name");
            Item item = new Item (id, text, isDone, name);
            items.add(item);

        }


        for (int j = 0; j < items.size(); j++) {
            Item item3 = items.get(j);
            int num = j + 1;
            String checkbox = "[ ]";
            if (item3.isDone) {
                checkbox = "[x]";
            }
            System.out.printf("%s %s. %s\n", checkbox, item3.id, item3.text, item3.author);
        }
    }
    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS to_dos (id IDENTITY, text VARCHAR, is_done BOOLEAN, user_id INT)");
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
    }
    public static void deleteToDo(Connection conn, Scanner scanner) throws SQLException{
        System.out.println("Which item do ou want to delete?");
        int i = Integer.valueOf(scanner.nextLine());

        PreparedStatement stmt = conn.prepareStatement("DELETE FROM to_dos WHERE id = ?" );
        stmt.setInt(1, i);
        stmt.execute();

    }
    public static User selectUser (Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String password = results.getString("password");
            return new User(id, name, password);
        }
        return null;

    }
    public static void insertUser (Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.execute();
    }
}