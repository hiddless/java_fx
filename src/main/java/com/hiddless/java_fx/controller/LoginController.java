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

import java.awt.event.ActionEvent;
import java.util.Optional;

public class LoginController {

    private UserDAO userDAO;

    public LoginController() {
        userDAO = new UserDAO();
    }

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);

        alert.setTitle(title);

        alert.setContentText(message);

        alert.showAndWait();
    }

    @FXML
    private void specialOnEnterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            login();
        }
    }

    @FXML
    public void login() {
        String username, password;
        username = usernameField.getText();
        password = passwordField.getText();

        Optional<UserDTO> optionalLoginUserDTO = userDAO.loginUser(username, password);

        if (optionalLoginUserDTO.isPresent()) {
            UserDTO userDTO = optionalLoginUserDTO.get();

            showAlert("Succesful" , "Succesfully logged in", Alert.AlertType.INFORMATION);

            openAdminPanel();
        }else {
            showAlert("Error" , "Error while logging in" , Alert.AlertType.ERROR);
        }
    }

    private void openAdminPanel() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("com/hiddless/java_fx/view/admin.fxml"));
            Parent parent = fxmlLoader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(parent));

            stage.show();
        }catch (Exception e) {
            System.out.println(SpecialColor.RED + "Failed Redirecting Admin Panel" + SpecialColor.RESET);
            e.printStackTrace();
            showAlert("Error" , "Admin Panel could not be loaded" , Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void switchToRegister(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("com/hiddless/java_fx/view/register.fxml"));
            Parent parent = fxmlLoader.load();

            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(parent));

            stage.setTitle("Register");

            stage.show();
        }catch (Exception e) {
            System.out.println(SpecialColor.RED + "Failed to redirect Register page" + SpecialColor.RESET);
            e.printStackTrace();
            showAlert("Error" , "Register page could not be loaded" , Alert.AlertType.ERROR);
        }
    }
}
