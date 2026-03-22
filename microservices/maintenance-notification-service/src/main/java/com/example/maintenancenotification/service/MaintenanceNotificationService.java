package com.example.maintenancenotification.service;

import com.example.maintenancenotification.model.MaintenanceNotification;
import com.example.maintenancenotification.repository.MaintenanceNotificationRepository;
import com.example.maintenancenotification.dto.MaintenanceNotificationDto;
import com.example.maintenancenotification.dto.CreateMaintenanceNotificationRequest;
import com.example.maintenancenotification.dto.UpdateMaintenanceNotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MaintenanceNotificationService {

    @Autowired
    private MaintenanceNotificationRepository repository;

    /**
     * Get all notifications
     */
    public List<MaintenanceNotificationDto> getAllNotifications() {
        return repository.findAll()
                .stream()
                .map(MaintenanceNotificationDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all active notifications
     */
    public List<MaintenanceNotificationDto> getAllActiveNotifications() {
        return repository.findByIsActiveOrderByCreatedAtDesc(true)
                .stream()
                .map(MaintenanceNotificationDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get notification by ID
     */
    public Optional<MaintenanceNotificationDto> getNotificationById(Long id) {
        return repository.findById(id)
                .map(MaintenanceNotificationDto::fromEntity);
    }

    /**
     * Create new notification
     */
    public MaintenanceNotificationDto createNotification(CreateMaintenanceNotificationRequest request) {
        validateCreateRequest(request);
        
        MaintenanceNotification notification = request.toEntity();
        MaintenanceNotification savedNotification = repository.save(notification);
        
        return MaintenanceNotificationDto.fromEntity(savedNotification);
    }

    /**
     * Update existing notification
     */
    public MaintenanceNotificationDto updateNotification(Long id, UpdateMaintenanceNotificationRequest request) {
        Optional<MaintenanceNotification> optionalNotification = repository.findById(id);
        if (optionalNotification.isPresent()) {
            MaintenanceNotification notification = optionalNotification.get();
            
            validateUpdateRequest(request);
            
            // Apply updates
            request.updateEntity(notification);
            
            MaintenanceNotification savedNotification = repository.save(notification);
            return MaintenanceNotificationDto.fromEntity(savedNotification);
        }
        throw new RuntimeException("Notification not found with id: " + id);
    }

    /**
     * Delete notification
     */
    public void deleteNotification(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Notification not found with id: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Activate notification (deactivate all others first)
     */
    public MaintenanceNotificationDto activateNotification(Long id) {
        // Deactivate all notifications first
        List<MaintenanceNotification> allNotifications = repository.findAll();
        for (MaintenanceNotification notification : allNotifications) {
            if (notification.isActive()) {
                notification.setActive(false);
                repository.save(notification);
            }
        }
        
        // Activate the specified notification
        Optional<MaintenanceNotification> optionalNotification = repository.findById(id);
        if (optionalNotification.isPresent()) {
            MaintenanceNotification notification = optionalNotification.get();
            notification.setActive(true);
            MaintenanceNotification savedNotification = repository.save(notification);
            return MaintenanceNotificationDto.fromEntity(savedNotification);
        }
        throw new RuntimeException("Notification not found with id: " + id);
    }

    /**
     * Deactivate notification
     */
    public MaintenanceNotificationDto deactivateNotification(Long id) {
        Optional<MaintenanceNotification> optionalNotification = repository.findById(id);
        if (optionalNotification.isPresent()) {
            MaintenanceNotification notification = optionalNotification.get();
            notification.setActive(false);
            MaintenanceNotification savedNotification = repository.save(notification);
            return MaintenanceNotificationDto.fromEntity(savedNotification);
        }
        throw new RuntimeException("Notification not found with id: " + id);
    }

    /**
     * Get notifications by priority
     */
    public List<MaintenanceNotificationDto> getNotificationsByPriority(MaintenanceNotification.Priority priority) {
        return repository.findByPriorityOrderByCreatedAtDesc(priority)
                .stream()
                .map(MaintenanceNotificationDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Check if notification exists
     */
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    // Private validation methods
    private void validateCreateRequest(CreateMaintenanceNotificationRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Message is required");
        }
        
        if (request.getPriority() == null) {
            throw new IllegalArgumentException("Priority is required");
        }
    }

    private void validateUpdateRequest(UpdateMaintenanceNotificationRequest request) {
        if (request.getTitle() != null && request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        
        if (request.getMessage() != null && request.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
    }
}
