package com.example.maintenancenotification;

import com.example.maintenancenotification.service.MaintenanceNotificationService;
import com.example.maintenancenotification.dto.MaintenanceNotificationDto;
import com.example.maintenancenotification.dto.CreateMaintenanceNotificationRequest;
import com.example.maintenancenotification.dto.UpdateMaintenanceNotificationRequest;
import com.example.maintenancenotification.model.MaintenanceNotification;
import com.example.maintenancenotification.repository.MaintenanceNotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceNotificationServiceTest {

    @Mock
    private MaintenanceNotificationRepository repository;

    @InjectMocks
    private MaintenanceNotificationService service;

    private MaintenanceNotification testNotification;

    @BeforeEach
    void setUp() {
        testNotification = new MaintenanceNotification("Test Title", "Test Message", MaintenanceNotification.Priority.HIGH);
        testNotification.setId(1L);
        testNotification.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateNotification() {
        // Given
        CreateMaintenanceNotificationRequest request = new CreateMaintenanceNotificationRequest();
        request.setTitle("Test Title");
        request.setMessage("Test Message");
        request.setPriority(MaintenanceNotification.Priority.HIGH);

        when(repository.save(any(MaintenanceNotification.class))).thenReturn(testNotification);

        // When
        MaintenanceNotificationDto result = service.createNotification(request);

        // Then
        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Message", result.getMessage());
        assertEquals(MaintenanceNotification.Priority.HIGH, result.getPriority());
        verify(repository, times(1)).save(any(MaintenanceNotification.class));
    }

    @Test
    void testGetAllNotifications() {
        // Given
        when(repository.findAll()).thenReturn(Arrays.asList(testNotification));

        // When
        List<MaintenanceNotificationDto> result = service.getAllNotifications();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).getTitle());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetNotificationById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testNotification));

        // When
        Optional<MaintenanceNotificationDto> result = service.getNotificationById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Title", result.get().getTitle());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testActivateNotification() {
        // Given
        MaintenanceNotification activeNotification = new MaintenanceNotification("Active", "Active Message", MaintenanceNotification.Priority.LOW);
        activeNotification.setActive(true);
        
        when(repository.findAll()).thenReturn(Arrays.asList(activeNotification));
        when(repository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(repository.save(any(MaintenanceNotification.class))).thenReturn(testNotification);

        // When
        MaintenanceNotificationDto result = service.activateNotification(1L);

        // Then
        assertNotNull(result);
        verify(repository, times(2)).save(any(MaintenanceNotification.class)); // Once for deactivating, once for activating
    }

    @Test
    void testValidateCreateRequest_InvalidTitle() {
        // Given
        CreateMaintenanceNotificationRequest request = new CreateMaintenanceNotificationRequest();
        request.setTitle("");
        request.setMessage("Test Message");
        request.setPriority(MaintenanceNotification.Priority.HIGH);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> service.createNotification(request));
    }
}
