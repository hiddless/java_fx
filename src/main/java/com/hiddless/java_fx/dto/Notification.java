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
    private NotificationType notificationType;

    private String message;

    private LocalDateTime timestamp;

    public void saveToFile(String message) {
        try (FileWriter fw = new FileWriter("notifications.txt", true)) {
            fw.write(message + " - " + LocalDateTime.now() + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("⚠️ Bildirim dosyasına yazılamadı: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
