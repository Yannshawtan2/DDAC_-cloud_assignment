package com.example.DDac_group18.services;

import com.example.DDac_group18.model.data_schema.MaintenanceNotification;
import com.example.DDac_group18.model.repository.MaintenanceNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceNotificationService {

    @Autowired
    private MaintenanceNotificationRepository maintenanceNotificationRepository;

    public MaintenanceNotification createNotification(String title, String message, MaintenanceNotification.Priority priority) {
        MaintenanceNotification notification = new MaintenanceNotification(title, message, priority);
        return maintenanceNotificationRepository.save(notification);
    }

    public List<MaintenanceNotification> getAllActiveNotifications() {
        return maintenanceNotificationRepository.findByIsActiveOrderByCreatedAtDesc(true);
    }

    public List<MaintenanceNotification> getAllNotifications() {
        return maintenanceNotificationRepository.findAll();
    }

    public Optional<MaintenanceNotification> getNotificationById(Long id) {
        return maintenanceNotificationRepository.findById(id);
    }

    public MaintenanceNotification updateNotification(Long id, String title, String message, MaintenanceNotification.Priority priority) {
        Optional<MaintenanceNotification> optionalNotification = maintenanceNotificationRepository.findById(id);
        if (optionalNotification.isPresent()) {
            MaintenanceNotification notification = optionalNotification.get();
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setPriority(priority);
            return maintenanceNotificationRepository.save(notification);
        }
        return null;
    }

    public boolean deactivateNotification(Long id) {
        Optional<MaintenanceNotification> optionalNotification = maintenanceNotificationRepository.findById(id);
        if (optionalNotification.isPresent()) {
            MaintenanceNotification notification = optionalNotification.get();
            notification.setActive(false);
            maintenanceNotificationRepository.save(notification);
            return true;
        }
        return false;
    }

    public boolean activateNotification(Long id) {
        Optional<MaintenanceNotification> optionalNotification = maintenanceNotificationRepository.findById(id);
        if (optionalNotification.isPresent()) {
            MaintenanceNotification notification = optionalNotification.get();
            notification.setActive(true);
            maintenanceNotificationRepository.save(notification);
            return true;
        }
        return false;
    }

    public boolean deleteNotification(Long id) {
        if (maintenanceNotificationRepository.existsById(id)) {
            maintenanceNotificationRepository.deleteById(id);
            return true;
        }
        return false;
    }
} 