package com.hiddless.java_fx.controller;

import com.hiddless.java_fx.dao.UserDAO;
import com.hiddless.java_fx.dto.UserDTO;
import com.hiddless.java_fx.utils.NotificationType;
import com.hiddless.java_fx.utils.NotificationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Optional;

public class ProfileController {

    private final UserDAO userDAO = new UserDAO();
    private UserDTO currentUser;

    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private Label username;
    @FXML
    private Label email;
    @FXML
    private Label role;

    public void setUser(UserDTO user) {
        this.currentUser = user;
        System.out.println("ProfileController#setUser gelen user: " + user);

        Optional<UserDTO> dbUser = getUserProfile(user.getId());

        if (dbUser.isPresent()) {
            UserDTO fresh = dbUser.get();
            username.setText(fresh.getUsername());
            email.setText(fresh.getEmail());
            role.setText(fresh.getRole().toString());
            this.currentUser = fresh;
        } else {
            username.setText(user.getUsername());
            email.setText(user.getEmail());
            role.setText(user.getRole().toString());
        }
    }

    public Optional<UserDTO> getUserProfile(int requestedUserId) {
        return userDAO.findById(requestedUserId);
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) username.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void changePassword() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            NotificationUtils.showNotification("Şifre boş olamaz", NotificationType.WARNING);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            NotificationUtils.showNotification("Yeni şifreler uyuşmuyor!", NotificationType.WARNING);
            return;
        }

        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        currentUser.setPassword(hashedPassword);

        try {
            userDAO.updatePassword(currentUser);
            NotificationUtils.showNotification("Şifre başarıyla değiştirildi!", NotificationType.SUCCESS);
            closeWindow();
        } catch (SQLException e) {
            NotificationUtils.showNotification("Şifre güncellenemedi!", NotificationType.ERROR);
            e.printStackTrace();
        }
    }
}