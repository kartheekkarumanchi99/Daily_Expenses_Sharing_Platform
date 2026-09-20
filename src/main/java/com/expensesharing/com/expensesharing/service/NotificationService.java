package com.expensesharing.com.expensesharing.service;

import com.expensesharing.com.expensesharing.entity.Notification;
import com.expensesharing.com.expensesharing.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // Records an application event. Failures are swallowed so notifications never
    // break the primary operation (e.g. creating an expense).
    public void record(String type, String message) {
        try {
            notificationRepository.save(new Notification(type, message, LocalDateTime.now()));
        } catch (Exception ignored) {
            // best-effort; do not fail the caller
        }
    }

    public List<Notification> getRecentNotifications() {
        return notificationRepository.findTop50ByOrderByCreatedAtDesc();
    }
}
