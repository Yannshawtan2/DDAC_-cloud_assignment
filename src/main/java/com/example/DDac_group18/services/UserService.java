package com.example.DDac_group18.services;

import com.example.DDac_group18.clients.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.DDac_group18.model.data_schema.Users;
import com.example.DDac_group18.model.data_schema.Users.Role;
import java.util.List;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean registerdefaultUser(String email, String password, String name) {
        try {
            // Use the microservice client instead of direct database access
            return userServiceClient.registerDefaultUser(email, password, name);
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            return false;
        }
    }

    public List<Users> getAllUsers() {
        return userServiceClient.getAllUsers();
    }

    public Users getUserById(Long id) {
        return userServiceClient.getUserById(id);
    }

    public List<Users> getUsersByRole(Role role) {
        return userServiceClient.getUsersByRole(role);
    }

    public boolean createUser(String email, String password, String name, Role role) {
        try {
            return userServiceClient.createUser(email, password, name, role);
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage());
            return false;
        }
    }

    public boolean updateUser(Long id, String email, String password, String name, Role role) {
        try {
            return userServiceClient.updateUser(id, email, password, name, role);
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(Long id) {
        try {
            return userServiceClient.deleteUser(id);
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage());
            return false;
        }
    }

    // This method is still needed for Spring Security authentication
    public Users getUserByEmail(String email) {
        return userServiceClient.getUserByEmail(email);
    }

    // Method for testing authentication
    public boolean authenticateUser(String email, String password) {
        try {
            return userServiceClient.authenticateUser(email, password);
        } catch (Exception e) {
            logger.error("Error authenticating user: {}", e.getMessage());
            return false;
        }
    }
}