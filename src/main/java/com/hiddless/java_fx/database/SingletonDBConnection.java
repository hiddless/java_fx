package com.hiddless.java_fx.database;

import com.hiddless.java_fx.utils.SpecialColor;

import java.sql.*;

public class SingletonDBConnection {

    private static final String URL = "jdbc:h2:./h2db/user_management";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private static SingletonDBConnection instance;
    private Connection connection;

    private SingletonDBConnection() {
        try {
            Class.forName("org.h2.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println(SpecialColor.GREEN + "Database connection successful" + SpecialColor.RESET);
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(SpecialColor.RED + "Database connection failed" + SpecialColor.RESET);
            throw new RuntimeException("Database connection failed");
        }
    }

    public static synchronized SingletonDBConnection getInstance() {
        if (instance == null) {
            instance = new SingletonDBConnection();
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
                System.out.println(SpecialColor.RED + "Database connection closed" + SpecialColor.RESET);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to close database connection", e);
            }
        }
    }
    public static void dataSet() throws SQLException {

        SingletonDBConnection dbInstance = SingletonDBConnection.getInstance();
        Connection conn = dbInstance.getConnection();

        try (Statement stmt = conn.createStatement()) {

            String createTableSQL = "CREATE TABLE IF NOT EXISTS Users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(255), "
                    + "email VARCHAR(255))";
            stmt.execute(createTableSQL);
            System.out.println("Users table created!");

            String insertDataSQL = "INSERT INTO Users (name, email) VALUES "
                    + "('Thomas William', 'thomas@example.com'), "
                    + "('Sophie', 'sophie@example.com')";
            stmt.executeUpdate(insertDataSQL);
            System.out.println("Data inserted!");

            String selectSQL = "SELECT * FROM Users";
            try (ResultSet rs = stmt.executeQuery(selectSQL)) {

                System.out.println("\nUsers Table Contents:");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") +
                            ", Name: " + rs.getString("name") +
                            ", Email: " + rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error executing database operations: " + e.getMessage());
            e.printStackTrace();
        } finally {
            SingletonDBConnection.closeConnection();
        }
    }

    public static void main(String[] args) throws SQLException {
        dataSet();
    }
}
