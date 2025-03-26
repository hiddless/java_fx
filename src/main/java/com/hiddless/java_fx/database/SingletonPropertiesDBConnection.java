package com.hiddless.java_fx.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class SingletonPropertiesDBConnection {

    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;

    private static SingletonPropertiesDBConnection instance;
    private Connection connection;

    private SingletonPropertiesDBConnection() {
        try {
            // JDBC Yükle
            loadDatabaseConfig(); // Konfigürasyonu oku
            Class.forName("org.h2.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connection successful");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Database connection failed!");
        }
    }

    private static void loadDatabaseConfig() {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            Properties properties = new Properties();
            properties.load(fis);
            URL = properties.getProperty("db.url", "jdbc:h2:./h2db/user_management");
            //URL = properties.getProperty("db.url", "jdbc:h2:~/h2db/user_management");
            USERNAME = properties.getProperty("db.username", "sa");
            PASSWORD = properties.getProperty("db.password", "");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load database configuration!");
        }
    }

    public static synchronized SingletonPropertiesDBConnection getInstance() {
        if (instance == null) {
            instance = new SingletonPropertiesDBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        if (instance != null && instance.connection != null) {
            try {
                instance.connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                throw new RuntimeException("An error occurred while closing the connection!", e);
            }
        }
    }


    public static void dataSet()  throws SQLException{
        SingletonPropertiesDBConnection dbInstance = SingletonPropertiesDBConnection.getInstance();
        Connection conn = dbInstance.getConnection();

        Statement stmt = conn.createStatement();

        String createTableSQL = "CREATE TABLE IF NOT EXISTS Users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255), "
                + "email VARCHAR(255))";
        stmt.execute(createTableSQL);
        System.out.println("Users table created!");

        String insertDataSQL = "INSERT INTO Users (name, email) VALUES "
                + "('Thomas William', 'tomwil@example.com'), "
                + "('Sophie', 'sophie@example.com')";
        stmt.executeUpdate(insertDataSQL);
        System.out.println("Data added!");

        String selectSQL = "SELECT * FROM Users";
        ResultSet rs = stmt.executeQuery(selectSQL);

        System.out.println("\nUsers Tablosu İçeriği:");
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("id") +
                    ", Name: " + rs.getString("name") +
                    ", Email: " + rs.getString("email"));
        }

        SingletonDBConnection.closeConnection();
    }
    public static void main(String[] args) throws SQLException {
    }
}

