package com.example.maintenancenotification.repository;

import com.example.maintenancenotification.model.MaintenanceNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceNotificationRepository extends JpaRepository<MaintenanceNotification, Long> {
    List<MaintenanceNotification> findByIsActiveOrderByCreatedAtDesc(boolean isActive);
    List<MaintenanceNotification> findByPriorityOrderByCreatedAtDesc(MaintenanceNotification.Priority priority);
}
