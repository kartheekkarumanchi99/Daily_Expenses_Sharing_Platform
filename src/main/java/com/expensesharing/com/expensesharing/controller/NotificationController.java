package com.expensesharing.com.expensesharing.controller;

import com.expensesharing.com.expensesharing.entity.Notification;
import com.expensesharing.com.expensesharing.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "Recent application activity feed")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get the 50 most recent notifications")
    public ResponseEntity<List<Notification>> getRecent() {
        return new ResponseEntity<>(notificationService.getRecentNotifications(), HttpStatus.OK);
    }
}
