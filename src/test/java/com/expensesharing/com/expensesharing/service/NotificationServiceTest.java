package com.expensesharing.com.expensesharing.service;

import com.expensesharing.com.expensesharing.entity.Notification;
import com.expensesharing.com.expensesharing.repositories.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void record_persistsNotificationWithTypeAndMessage() {
        notificationService.record("EXPENSE_ADDED", "Expense 'Dinner' added");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification saved = captor.getValue();
        assertEquals("EXPENSE_ADDED", saved.getType());
        assertEquals("Expense 'Dinner' added", saved.getMessage());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void record_swallowsRepositoryErrors() {
        doThrow(new RuntimeException("db down")).when(notificationRepository).save(any());
        assertDoesNotThrow(() -> notificationService.record("X", "y"));
    }

    @Test
    void getRecentNotifications_delegatesToRepository() {
        List<Notification> expected = Arrays.asList(new Notification(), new Notification());
        when(notificationRepository.findTop50ByOrderByCreatedAtDesc()).thenReturn(expected);

        List<Notification> result = notificationService.getRecentNotifications();

        assertEquals(expected, result);
        verify(notificationRepository).findTop50ByOrderByCreatedAtDesc();
    }
}
