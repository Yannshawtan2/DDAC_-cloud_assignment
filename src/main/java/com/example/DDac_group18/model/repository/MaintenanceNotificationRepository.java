package com.example.DDac_group18.model.repository;

import com.example.DDac_group18.model.data_schema.MaintenanceNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceNotificationRepository extends JpaRepository<MaintenanceNotification, Long> {
    List<MaintenanceNotification> findByIsActiveOrderByCreatedAtDesc(boolean isActive);
    List<MaintenanceNotification> findByPriorityOrderByCreatedAtDesc(MaintenanceNotification.Priority priority);
} 