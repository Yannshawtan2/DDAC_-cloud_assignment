package com.example.maintenancenotification.dto;

import com.example.maintenancenotification.model.MaintenanceNotification;

public class CreateMaintenanceNotificationRequest {
    private String title;
    private String message;
    private MaintenanceNotification.Priority priority;

    // Constructors
    public CreateMaintenanceNotificationRequest() {}

    public CreateMaintenanceNotificationRequest(String title, String message, MaintenanceNotification.Priority priority) {
        this.title = title;
        this.message = message;
        this.priority = priority;
    }

    // Convert to entity
    public MaintenanceNotification toEntity() {
        return new MaintenanceNotification(title, message, priority);
    }

    // Getters and setters
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

    @Override
    public String toString() {
        return "CreateMaintenanceNotificationRequest{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", priority=" + priority +
                '}';
    }
}
