package com.example.DDac_group18.clients;

import com.example.DDac_group18.model.data_schema.MaintenanceNotification;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MaintenanceNotificationServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceNotificationServiceClient.class);

    @Value("${maintenance.service.url:https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/maintain}")
    private String maintenanceServiceUrl;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public MaintenanceNotificationServiceClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public List<MaintenanceNotification> getAllNotifications() {
        try {
            logger.info("Getting all maintenance notifications from: {}", maintenanceServiceUrl);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(maintenanceServiceUrl + "/maintenance-notifications"))
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                List<Map<String, Object>> responseList = objectMapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {});
                List<MaintenanceNotification> notifications = new ArrayList<>();
                
                for (Map<String, Object> item : responseList) {
                    notifications.add(mapToMaintenanceNotification(item));
                }
                
                logger.info("Successfully retrieved {} notifications", notifications.size());
                return notifications;
            } else {
                logger.error("Failed to get notifications. Status: {}, Body: {}", response.statusCode(), response.body());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            logger.error("Error getting all notifications: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<MaintenanceNotification> getAllActiveNotifications() {
        try {
            logger.info("Getting active maintenance notifications from: {}", maintenanceServiceUrl + "/active");
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(maintenanceServiceUrl + "/maintenance-notifications/active"))
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                List<Map<String, Object>> responseList = objectMapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {});
                List<MaintenanceNotification> notifications = new ArrayList<>();
                
                for (Map<String, Object> item : responseList) {
                    notifications.add(mapToMaintenanceNotification(item));
                }
                
                logger.info("Successfully retrieved {} active notifications", notifications.size());
                return notifications;
            } else {
                logger.error("Failed to get active notifications. Status: {}, Body: {}", response.statusCode(), response.body());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            logger.error("Error getting active notifications: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public MaintenanceNotification createNotification(String title, String message, MaintenanceNotification.Priority priority) {
        try {
            logger.info("Creating maintenance notification: {}", title);
            
            Map<String, Object> requestBody = Map.of(
                    "title", title,
                    "message", message,
                    "priority", priority.toString()
            );

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(maintenanceServiceUrl + "/maintenance-notifications"))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 201) {
                Map<String, Object> responseMap = objectMapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
                MaintenanceNotification notification = mapToMaintenanceNotification(responseMap);
                logger.info("Successfully created notification with ID: {}", notification.getId());
                return notification;
            } else {
                logger.error("Failed to create notification. Status: {}, Body: {}", response.statusCode(), response.body());
                return null;
            }
        } catch (Exception e) {
            logger.error("Error creating notification: {}", e.getMessage(), e);
            return null;
        }
    }

    public boolean activateNotification(Long id) {
        try {
            logger.info("Activating notification with ID: {}", id);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(maintenanceServiceUrl + "/maintenance-notifications/" + id + "/activate"))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{}"))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                logger.info("Successfully activated notification with ID: {}", id);
                return true;
            } else {
                logger.error("Failed to activate notification. Status: {}, Body: {}", response.statusCode(), response.body());
                return false;
            }
        } catch (Exception e) {
            logger.error("Error activating notification: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean deactivateNotification(Long id) {
        try {
            logger.info("Deactivating notification with ID: {}", id);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(maintenanceServiceUrl + "/maintenance-notifications/" + id + "/deactivate"))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{}"))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                logger.info("Successfully deactivated notification with ID: {}", id);
                return true;
            } else {
                logger.error("Failed to deactivate notification. Status: {}, Body: {}", response.statusCode(), response.body());
                return false;
            }
        } catch (Exception e) {
            logger.error("Error deactivating notification: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean deleteNotification(Long id) {
        try {
            logger.info("Deleting notification with ID: {}", id);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(maintenanceServiceUrl + "/maintenance-notifications/" + id))
                    .timeout(Duration.ofSeconds(30))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                logger.info("Successfully deleted notification with ID: {}", id);
                return true;
            } else {
                logger.error("Failed to delete notification. Status: {}, Body: {}", response.statusCode(), response.body());
                return false;
            }
        } catch (Exception e) {
            logger.error("Error deleting notification: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean updateNotification(Long id, String title, String message, MaintenanceNotification.Priority priority) {
        try {
            logger.info("Updating notification with ID: {}", id);
            
            Map<String, Object> requestBody = Map.of(
                    "title", title,
                    "message", message,
                    "priority", priority.toString()
            );

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(maintenanceServiceUrl + "/maintenance-notifications/" + id))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                logger.info("Successfully updated notification with ID: {}", id);
                return true;
            } else {
                logger.error("Failed to update notification. Status: {}, Body: {}", response.statusCode(), response.body());
                return false;
            }
        } catch (Exception e) {
            logger.error("Error updating notification: {}", e.getMessage(), e);
            return false;
        }
    }

    private MaintenanceNotification mapToMaintenanceNotification(Map<String, Object> data) {
        MaintenanceNotification notification = new MaintenanceNotification();
        
        if (data.get("id") != null) {
            notification.setId(Long.valueOf(data.get("id").toString()));
        }
        
        notification.setTitle((String) data.get("title"));
        notification.setMessage((String) data.get("message"));
        
        if (data.get("priority") != null) {
            notification.setPriority(MaintenanceNotification.Priority.valueOf(data.get("priority").toString()));
        }
        
        if (data.get("active") != null) {
            notification.setActive(Boolean.valueOf(data.get("active").toString()));
        }
        
        if (data.get("createdAt") != null) {
            String createdAtStr = data.get("createdAt").toString();
            try {
                notification.setCreatedAt(LocalDateTime.parse(createdAtStr.substring(0, 19)));
            } catch (Exception e) {
                logger.warn("Failed to parse createdAt date: {}", createdAtStr);
                notification.setCreatedAt(LocalDateTime.now());
            }
        }
        
        return notification;
    }
}
