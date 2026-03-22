package com.example.maintenancenotification.dto;

import com.example.maintenancenotification.model.MaintenanceNotification;
import java.time.LocalDateTime;

public class MaintenanceNotificationDto {
    private Long id;
    private String title;
    private String message;
    private MaintenanceNotification.Priority priority;
    private LocalDateTime createdAt;
    private boolean isActive;

    // Constructors
    public MaintenanceNotificationDto() {}

    public MaintenanceNotificationDto(Long id, String title, String message, 
                                    MaintenanceNotification.Priority priority, 
                                    LocalDateTime createdAt, boolean isActive) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.priority = priority;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }

    // Static factory method
    public static MaintenanceNotificationDto fromEntity(MaintenanceNotification notification) {
        return new MaintenanceNotificationDto(
            notification.getId(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getPriority(),
            notification.getCreatedAt(),
            notification.isActive()
        );
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MaintenanceNotification.Priority getPriority() {
        return priority;
    }

    public void setPriority(MaintenanceNotification.Priority priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "MaintenanceNotificationDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", priority=" + priority +
                ", createdAt=" + createdAt +
                ", isActive=" + isActive +
                '}';
    }
}
