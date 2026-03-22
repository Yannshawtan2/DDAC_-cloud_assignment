package com.example.maintenancenotification.dto;

import com.example.maintenancenotification.model.MaintenanceNotification;

public class UpdateMaintenanceNotificationRequest {
    private String title;
    private String message;
    private MaintenanceNotification.Priority priority;

    // Constructors
    public UpdateMaintenanceNotificationRequest() {}

    public UpdateMaintenanceNotificationRequest(String title, String message, MaintenanceNotification.Priority priority) {
        this.title = title;
        this.message = message;
        this.priority = priority;
    }

    // Update entity with non-null fields
    public void updateEntity(MaintenanceNotification notification) {
        if (title != null) {
            notification.setTitle(title);
        }
        if (message != null) {
            notification.setMessage(message);
        }
        if (priority != null) {
            notification.setPriority(priority);
        }
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
        return "UpdateMaintenanceNotificationRequest{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", priority=" + priority +
                '}';
    }
}
