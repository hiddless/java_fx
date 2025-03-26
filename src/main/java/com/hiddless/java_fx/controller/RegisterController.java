package com.hiddless.java_fx.controller;

import com.hiddless.java_fx.dao.UserDAO;
import com.hiddless.java_fx.dto.UserDTO;
import com.hiddless.java_fx.utils.SpecialColor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.Optional;

public class RegisterController {
    private UserDAO userDAO;

    public RegisterController() {
        userDAO = new UserDAO();
    }


    /// /////////////////////////////////////////////////////////////////////////////
    ///  FXML Field
    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField emailField;


    ////////////////////////////////////////////////////////////////////////////////////////
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);

        alert.setTitle(title);

        alert.setContentText(message);

        alert.showAndWait();
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    @FXML
    private void specialOnEnterPressed(KeyEvent keyEvent) {

        if (keyEvent.getCode() == KeyCode.ENTER) {
            register();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////

    @FXML
    public void register() {
        String username, password,email;
        username = usernameField.getText();
        password = passwordField.getText();
        email = emailField.getText();


        Optional<UserDTO> optionalRegisterUserDTO = Optional.ofNullable(UserDTO.builder()
                .id(0) // Create
                .username(username)
                .password(password)
                .email(email)
                .build());

        if (optionalRegisterUserDTO.isPresent()) {
            UserDTO userDTO = optionalRegisterUserDTO.get();

            showAlert("Successful", "Registration Successful", Alert.AlertType.INFORMATION);

            switchToLoginPane();

        } else {
            showAlert("Successful", "Login Successful", Alert.AlertType.ERROR);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    @FXML
    private void switchToLoginPane() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("com/hiddless/java_fx/view/login.fxml"));
            Parent parent = fxmlLoader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(parent));

            stage.setTitle("Login");

            stage.show();
        } catch (Exception e) {
            System.out.println(SpecialColor.RED + "Not redirected on Login Page" + SpecialColor.RESET);
            e.printStackTrace();
            showAlert("Error", "Login Screen Failed to Load", Alert.AlertType.ERROR);
        }
    }
}