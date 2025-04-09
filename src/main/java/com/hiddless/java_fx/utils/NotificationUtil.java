package com.hiddless.java_fx.utils;

import com.hiddless.java_fx.dao.NotificationDAO;
import com.hiddless.java_fx.dto.Notification;
import javafx.scene.control.Alert;

import java.util.List;

public class NotificationUtil {
    private static final NotificationDAO notificationDAO;

    static {
        notificationDAO = new NotificationDAO();
    }

    public static void showNotification(String message, NotificationType type) {
        notificationDAO.addNotification(message, type);

        Alert alert;
        switch (type) {
            case SUCCESS -> alert = new Alert(Alert.AlertType.INFORMATION);
            case ERROR -> alert = new Alert(Alert.AlertType.ERROR);
            case WARNING -> alert = new Alert(Alert.AlertType.WARNING);
            default -> alert = new Alert(Alert.AlertType.NONE);
        }

        alert.setTitle("Bildirim");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static List<Notification> getAll() {
        return notificationDAO.getAllNotifications();
    }
}
