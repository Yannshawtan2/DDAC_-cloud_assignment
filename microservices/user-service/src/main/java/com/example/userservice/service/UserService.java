package com.example.userservice.service;

import com.example.userservice.dto.UserCreateRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.dto.UserUpdateRequest;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordService passwordService;

    public UserResponse createUser(UserCreateRequest request) {
        logger.info("Creating user with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("User creation failed: Email already exists: {}", request.getEmail());
            throw new IllegalArgumentException("User with this email already exists");
        }

        try {
            User user = new User();
            user.setEmail(request.getEmail());
            // Encrypt password using BCrypt before storing
            String encryptedPassword = passwordService.encryptPassword(request.getPassword());
            user.setPassword(encryptedPassword);
            user.setRole(request.getRole());
            user.setName(request.getName());
            
            User savedUser = userRepository.save(user);
            logger.info("Successfully created user with ID: {} and encrypted password", savedUser.getId());
            
            return convertToResponse(savedUser);
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create user", e);
        }
    }

    public UserResponse getUserById(Long id) {
        logger.info("Retrieving user with ID: {}", id);
        
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return convertToResponse(user.get());
        } else {
            logger.warn("User not found with ID: {}", id);
            throw new IllegalArgumentException("User not found");
        }
    }

    public UserResponse getUserByEmail(String email) {
        logger.info("Retrieving user with email: {}", email);
        
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return convertToResponse(user.get());
        } else {
            logger.warn("User not found with email: {}", email);
            throw new IllegalArgumentException("User not found");
        }
    }

    public List<UserResponse> getAllUsers() {
        logger.info("Retrieving all users");
        
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByRole(User.Role role) {
        logger.info("Retrieving users with role: {}", role);
        
        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> searchUsers(String name, User.Role role) {
        logger.info("Searching users with name: {} and role: {}", name, role);
        
        List<User> users;
        if (role != null) {
            users = userRepository.findByRoleAndNameContainingIgnoreCase(role, name);
        } else {
            users = userRepository.findByNameContainingIgnoreCase(name);
        }
        
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        logger.info("Updating user with ID: {}", id);
        
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (!existingUserOpt.isPresent()) {
            logger.warn("User not found with ID: {}", id);
            throw new IllegalArgumentException("User not found");
        }

        User existingUser = existingUserOpt.get();

        // Check if email is being changed and if the new email already exists
        if (!existingUser.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                logger.warn("Email already exists: {}", request.getEmail());
                throw new IllegalArgumentException("Email already exists");
            }
        }

        try {
            // Update user information
            existingUser.setEmail(request.getEmail());
            existingUser.setName(request.getName());
            existingUser.setRole(request.getRole());

            // Only update password if a new one is provided
            if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
                // Encrypt the new password using BCrypt before storing
                String encryptedPassword = passwordService.encryptPassword(request.getPassword());
                existingUser.setPassword(encryptedPassword);
            }

            User updatedUser = userRepository.save(existingUser);
            logger.info("Successfully updated user with ID: {}", updatedUser.getId());
            
            return convertToResponse(updatedUser);
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            logger.warn("User not found with ID: {}", id);
            throw new IllegalArgumentException("User not found");
        }

        // Prevent deletion of admin users (optional safety measure)
        if (user.get().getRole() == User.Role.ADMIN) {
            logger.warn("Attempted to delete admin user with ID: {}", id);
            throw new IllegalArgumentException("Cannot delete admin users");
        }

        try {
            userRepository.deleteById(id);
            logger.info("Successfully deleted user with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    public UserResponse authenticateUser(String email, String password) {
        logger.info("Authenticating user with email: {}", email);
        
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            // Use BCrypt to verify the plain text password against the encrypted password
            boolean passwordMatches = passwordService.verifyPassword(password, user.get().getPassword());
            if (passwordMatches) {
                logger.info("Authentication successful for user: {}", email);
                return convertToResponse(user.get());
            } else {
                logger.warn("Authentication failed for user: {} - password mismatch", email);
            }
        } else {
            logger.warn("Authentication failed for user: {} - user not found", email);
        }
        return null; // Authentication failed
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole());
        return response;
    }
}
