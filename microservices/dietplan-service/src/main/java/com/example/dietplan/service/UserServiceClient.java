package com.example.dietplan.service;

import com.example.dietplan.dto.UserDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);
    
    @Value("${user.service.url:https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user/users}")
    private String userServiceUrl;
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public UserServiceClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Get user by ID from user-service
     */
    public Optional<UserDto> getUserById(Long userId) {
        try {
            logger.info("Fetching user with ID: {} from user-service", userId);
            
            // Get all users and find by ID (since the API returns all users)
            List<UserDto> users = getAllUsers();
            return users.stream()
                    .filter(user -> user.getId().equals(userId))
                    .findFirst();
                    
        } catch (Exception e) {
            logger.error("Error fetching user with ID {}: {}", userId, e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    /**
     * Get all users from user-service
     */
    public List<UserDto> getAllUsers() {
        try {
            logger.info("Fetching all users from user-service: {}", userServiceUrl);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(userServiceUrl))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                List<UserDto> users = objectMapper.readValue(response.body(), new TypeReference<List<UserDto>>() {});
                logger.info("Successfully fetched {} users from user-service", users.size());
                return users;
            } else {
                logger.error("Failed to fetch users from user-service. Status: {}, Body: {}", 
                           response.statusCode(), response.body());
                throw new RuntimeException("Failed to fetch users from user-service");
            }
            
        } catch (IOException | InterruptedException e) {
            logger.error("Error fetching users from user-service: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to communicate with user-service", e);
        }
    }
    
    /**
     * Get users by role
     */
    public List<UserDto> getUsersByRole(String role) {
        try {
            logger.info("Fetching users with role: {} from user-service", role);
            
            List<UserDto> users = getAllUsers();
            return users.stream()
                    .filter(user -> role.equals(user.getRole()))
                    .toList();
                    
        } catch (Exception e) {
            logger.error("Error fetching users with role {}: {}", role, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch users by role", e);
        }
    }
    
    /**
     * Validate if user exists and has correct role
     */
    public boolean validateUser(Long userId, String expectedRole) {
        try {
            Optional<UserDto> userOpt = getUserById(userId);
            if (userOpt.isEmpty()) {
                logger.warn("User with ID {} not found", userId);
                return false;
            }
            
            UserDto user = userOpt.get();
            boolean hasCorrectRole = expectedRole.equals(user.getRole());
            
            if (!hasCorrectRole) {
                logger.warn("User with ID {} has role {} but expected {}", userId, user.getRole(), expectedRole);
            }
            
            return hasCorrectRole;
            
        } catch (Exception e) {
            logger.error("Error validating user {} with role {}: {}", userId, expectedRole, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Check if user exists
     */
    public boolean userExists(Long userId) {
        return getUserById(userId).isPresent();
    }
}