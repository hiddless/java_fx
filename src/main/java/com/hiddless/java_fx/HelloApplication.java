package com.hiddless.java_fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;

public class HelloApplication extends Application {

    private static final String DB_URL = "jdbc:h2:./database;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    @Override
    public void start(Stage stage) {
        try {
            initializeDatabase();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/hiddless/java_fx/view/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading FXML: " + e.getMessage());
        }
    }

    private void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(50) NOT NULL UNIQUE,"
                    + "password VARCHAR(255) NOT NULL,"
                    + "email VARCHAR(100) NOT NULL UNIQUE);";

            statement.execute(createUsersTable);

            String createReceiptsTable = "CREATE TABLE IF NOT EXISTS receipts ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "user_id INT NOT NULL,"
                    + "amount DECIMAL(10,2) NOT NULL,"
                    + "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (user_id) REFERENCES users(id));";

            statement.execute(createReceiptsTable);

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
