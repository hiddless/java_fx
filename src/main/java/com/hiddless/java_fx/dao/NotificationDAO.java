package com.hiddless.java_fx.dao;

import com.hiddless.java_fx.dto.Notification;
import com.hiddless.java_fx.utils.NotificationType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class NotificationDAO {

    public void addNotification(String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        notification.setTimestamp(LocalDateTime.now());
        notification.saveToFile(message);
        notifications.add(notification);

    }
    private List<Notification> notifications = new ArrayList<>();

    public List<Notification> getAllNotifications() {
        return notifications;
    }
}
