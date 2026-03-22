package com.example.quiz.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${user.service.url:https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user}")
    private String userServiceUrl;
    
    public UserServiceClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public UserInfo getUserById(String userId) {
        try {
            logger.info("Fetching user info for ID: {}", userId);
            
            String url = userServiceUrl + "/users/" + userId;
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null) {
                JsonNode userNode = objectMapper.readTree(response);
                UserInfo userInfo = new UserInfo();
                userInfo.setId(userNode.get("id").asLong());
                userInfo.setName(userNode.get("name").asText());
                userInfo.setEmail(userNode.get("email").asText());
                userInfo.setRole(userNode.get("role").asText());
                
                logger.info("Successfully fetched user info for ID: {}", userId);
                return userInfo;
            }
        } catch (Exception e) {
            logger.error("Failed to fetch user info for ID: {}, error: {}", userId, e.getMessage());
        }
        return null;
    }

    public UserInfo getUserByEmail(String email) {
        try {
            logger.info("Fetching user info for email: {}", email);
            
            String url = userServiceUrl + "/users/email/" + email;
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null) {
                JsonNode userNode = objectMapper.readTree(response);
                UserInfo userInfo = new UserInfo();
                userInfo.setId(userNode.get("id").asLong());
                userInfo.setName(userNode.get("name").asText());
                userInfo.setEmail(userNode.get("email").asText());
                userInfo.setRole(userNode.get("role").asText());
                
                logger.info("Successfully fetched user info for email: {}", email);
                return userInfo;
            }
        } catch (Exception e) {
            logger.error("Failed to fetch user info for email: {}, error: {}", email, e.getMessage());
        }
        return null;
    }

    public boolean validateUserRole(String userIdentifier, String requiredRole) {
        try {
            UserInfo user;
            
            // Check if the identifier is numeric (ID) or email
            if (userIdentifier.matches("\\d+")) {
                user = getUserById(userIdentifier);
            } else {
                user = getUserByEmail(userIdentifier);
            }
            
            if (user != null) {
                boolean hasRole = requiredRole.equalsIgnoreCase(user.getRole());
                logger.info("User {} has role {}, required role: {}, valid: {}", 
                    userIdentifier, user.getRole(), requiredRole, hasRole);
                return hasRole;
            } else {
                logger.warn("User not found for validation: {}", userIdentifier);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error validating user role for: {}, error: {}", userIdentifier, e.getMessage());
            return false;
        }
    }

    public boolean userExists(String userIdentifier) {
        try {
            UserInfo user;
            
            // Check if the identifier is numeric (ID) or email
            if (userIdentifier.matches("\\d+")) {
                user = getUserById(userIdentifier);
            } else {
                user = getUserByEmail(userIdentifier);
            }
            
            boolean exists = user != null;
            logger.info("User {} exists: {}", userIdentifier, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error checking if user exists: {}, error: {}", userIdentifier, e.getMessage());
            return false;
        }
    }

    public String getUserName(String userIdentifier) {
        try {
            UserInfo user;
            
            // Check if the identifier is numeric (ID) or email
            if (userIdentifier.matches("\\d+")) {
                user = getUserById(userIdentifier);
            } else {
                user = getUserByEmail(userIdentifier);
            }
            
            if (user != null) {
                logger.info("Found user name: {} for identifier: {}", user.getName(), userIdentifier);
                return user.getName();
            } else {
                logger.warn("User not found for name lookup: {}", userIdentifier);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error getting user name for: {}, error: {}", userIdentifier, e.getMessage());
            return null;
        }
    }
    
    public static class UserInfo {
        private Long id;
        private String name;
        private String email;
        private String role;
        
        public UserInfo() {}
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
        
        @Override
        public String toString() {
            return "UserInfo{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", role='" + role + '\'' +
                    '}';
        }
    }
}