package com.example.treatmentplan.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    public UserInfo getUserById(Long userId) {
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