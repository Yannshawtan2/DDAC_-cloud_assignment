package com.example.DDac_group18.clients;

import com.example.DDac_group18.model.data_schema.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class UserServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${user.service.url:https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user/users}")
    private String userServiceUrl;

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public boolean authenticateUser(String email, String password) {
        try {
            logger.info("Attempting to authenticate user: {} with password length: {}", email, password.length());
            
            Map<String, String> request = new HashMap<>();
            request.put("email", email);
            request.put("password", password);
            
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, createHeaders());
            ResponseEntity<Map> response = restTemplate.postForEntity(
                userServiceUrl + "/authenticate", entity, Map.class);
            
            logger.info("Lambda authentication response status: {}", response.getStatusCode());
            logger.info("Lambda authentication response body: {}", response.getBody());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Object authenticated = body.get("authenticated");
                boolean result = Boolean.TRUE.equals(authenticated);
                logger.info("Authentication result: {}", result);
                return result;
            }
            logger.warn("Authentication failed - invalid response");
            return false;
        } catch (Exception e) {
            logger.error("Error authenticating user: {}", e.getMessage(), e);
            return false;
        }
    }

    public Users getUserByEmail(String email) {
        try {
            logger.info("Fetching user by email: {}", email);
            ResponseEntity<Map> response = restTemplate.getForEntity(
                userServiceUrl + "/email/" + email, Map.class);
            
            logger.info("Get user by email response status: {}", response.getStatusCode());
            logger.info("Get user by email response body: {}", response.getBody());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Users user = mapToUser(response.getBody());
                logger.info("Found user: {} with role: {}", user.getEmail(), user.getRole());
                return user;
            }
            logger.warn("User not found: {}", email);
            return null;
        } catch (Exception e) {
            logger.error("Error getting user by email: {}", e.getMessage(), e);
            return null;
        }
    }

    public Users getUserById(Long id) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                userServiceUrl + "/" + id, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return mapToUser(response.getBody());
            }
            return null;
        } catch (Exception e) {
            logger.error("Error getting user by ID: {}", e.getMessage());
            return null;
        }
    }

    public List<Users> getAllUsers() {
        try {
            ResponseEntity<Map[]> response = restTemplate.getForEntity(
                userServiceUrl, Map[].class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Arrays.stream(response.getBody())
                    .map(this::mapToUser)
                    .toList();
            }
            return List.of();
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage());
            return List.of();
        }
    }

    public List<Users> getUsersByRole(Users.Role role) {
        try {
            ResponseEntity<Map[]> response = restTemplate.getForEntity(
                userServiceUrl + "/role/" + role.name(), Map[].class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Arrays.stream(response.getBody())
                    .map(this::mapToUser)
                    .toList();
            }
            return List.of();
        } catch (Exception e) {
            logger.error("Error getting users by role: {}", e.getMessage());
            return List.of();
        }
    }

    public boolean createUser(String email, String password, String name, Users.Role role) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("email", email);
            request.put("password", password);
            request.put("name", name);
            request.put("role", role.name());
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, createHeaders());
            ResponseEntity<Map> response = restTemplate.postForEntity(
                userServiceUrl, entity, Map.class);
            
            return response.getStatusCode() == HttpStatus.CREATED;
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage());
            return false;
        }
    }

    public boolean registerDefaultUser(String email, String password, String name) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("email", email);
            request.put("password", password);
            request.put("name", name);
            
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, createHeaders());
            ResponseEntity<Map> response = restTemplate.postForEntity(
                userServiceUrl + "/register", entity, Map.class);
            
            return response.getStatusCode() == HttpStatus.CREATED;
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            return false;
        }
    }

    public boolean updateUser(Long id, String email, String password, String name, Users.Role role) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("email", email);
            if (password != null && !password.trim().isEmpty()) {
                request.put("password", password);
            }
            request.put("name", name);
            request.put("role", role.name());
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, createHeaders());
            ResponseEntity<Map> response = restTemplate.exchange(
                userServiceUrl + "/" + id, HttpMethod.PUT, entity, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(Long id) {
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                userServiceUrl + "/" + id, HttpMethod.DELETE, null, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage());
            return false;
        }
    }

    private Users mapToUser(Map<String, Object> userMap) {
        Users user = new Users();
        user.setId(((Number) userMap.get("id")).longValue());
        user.setEmail((String) userMap.get("email"));
        user.setName((String) userMap.get("name"));
        user.setRole(Users.Role.valueOf((String) userMap.get("role")));
        // Note: password is not returned by the microservice for security
        return user;
    }
}
