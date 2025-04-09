package com.hiddless.java_fx.dto;

import com.hiddless.java_fx.utils.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Setter
@Getter
public class Notification {
    private String message;
    private NotificationType notificationType;
    private LocalDateTime timestamp;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setNotificationType(NotificationType type) {
        this.notificationType = type;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void saveToFile(String message) {
        try (FileWriter fw = new FileWriter("bildirimler.txt", true)) {
            fw.write(message + " - " + LocalDateTime.now() + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("⚠️ Bildirim dosyasına yazılamadı: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
