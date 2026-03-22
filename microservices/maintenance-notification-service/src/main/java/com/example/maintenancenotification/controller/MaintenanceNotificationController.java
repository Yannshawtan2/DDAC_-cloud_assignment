package com.example.maintenancenotification.controller;

import com.example.maintenancenotification.dto.CreateMaintenanceNotificationRequest;
import com.example.maintenancenotification.dto.MaintenanceNotificationDto;
import com.example.maintenancenotification.dto.UpdateMaintenanceNotificationRequest;
import com.example.maintenancenotification.model.MaintenanceNotification;
import com.example.maintenancenotification.service.MaintenanceNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("maintain/maintenance-notifications")
@CrossOrigin(origins = "*")
public class MaintenanceNotificationController {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceNotificationController.class);

    @Autowired
    private MaintenanceNotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getAllNotifications(@RequestParam(value = "active", required = false) Boolean active) {
        try {
            logger.info("Getting notifications with active filter: {}", active);
            List<MaintenanceNotificationDto> notifications;
            
            if (active != null && active) {
                notifications = notificationService.getAllActiveNotifications();
            } else {
                notifications = notificationService.getAllNotifications();
            }
            
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            logger.error("Error getting notifications: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve notifications"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNotificationById(@PathVariable Long id) {
        try {
            logger.info("Getting notification with id: {}", id);
            Optional<MaintenanceNotificationDto> notification = notificationService.getNotificationById(id);
            
            if (notification.isPresent()) {
                return ResponseEntity.ok(notification.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting notification {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve notification"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createNotification(@Valid @RequestBody CreateMaintenanceNotificationRequest request) {
        try {
            logger.info("Creating notification with title: {}", request.getTitle());
            MaintenanceNotificationDto notification = notificationService.createNotification(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(notification);
        } catch (IllegalArgumentException e) {
            logger.warn("Notification creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during notification creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable Long id, 
                                              @Valid @RequestBody UpdateMaintenanceNotificationRequest request) {
        try {
            logger.info("Updating notification with id: {}", id);
            MaintenanceNotificationDto notification = notificationService.updateNotification(id, request);
            return ResponseEntity.ok(notification);
        } catch (IllegalArgumentException e) {
            logger.warn("Notification update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            logger.error("Error updating notification {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update notification"));
        } catch (Exception e) {
            logger.error("Unexpected error during notification update: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        try {
            logger.info("Deleting notification with id: {}", id);
            notificationService.deleteNotification(id);
            return ResponseEntity.ok(Map.of("message", "Notification deleted successfully", "id", id));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            logger.error("Error deleting notification {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete notification"));
        } catch (Exception e) {
            logger.error("Unexpected error during notification deletion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activateNotification(@PathVariable Long id) {
        try {
            logger.info("Activating notification with id: {}", id);
            MaintenanceNotificationDto notification = notificationService.activateNotification(id);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            logger.error("Error activating notification {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to activate notification"));
        } catch (Exception e) {
            logger.error("Unexpected error during notification activation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateNotification(@PathVariable Long id) {
        try {
            logger.info("Deactivating notification with id: {}", id);
            MaintenanceNotificationDto notification = notificationService.deactivateNotification(id);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            logger.error("Error deactivating notification {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to deactivate notification"));
        } catch (Exception e) {
            logger.error("Unexpected error during notification deactivation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<?> getNotificationsByPriority(@PathVariable String priority) {
        try {
            logger.info("Getting notifications with priority: {}", priority);
            MaintenanceNotification.Priority priorityEnum = MaintenanceNotification.Priority.valueOf(priority.toUpperCase());
            List<MaintenanceNotificationDto> notifications = notificationService.getNotificationsByPriority(priorityEnum);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid priority: {}", priority);
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid priority: " + priority));
        } catch (Exception e) {
            logger.error("Error getting notifications by priority {}: {}", priority, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve notifications"));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveNotifications() {
        try {
            logger.info("Getting active notifications");
            List<MaintenanceNotificationDto> notifications = notificationService.getAllActiveNotifications();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            logger.error("Error getting active notifications: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve active notifications"));
        }
    }
}
