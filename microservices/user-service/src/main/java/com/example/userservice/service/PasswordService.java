package com.example.userservice.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PasswordService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordService.class);
    private final PasswordEncoder passwordEncoder;

    public PasswordService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Encrypts a plain text password using BCrypt
     */
    public String encryptPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        String encryptedPassword = passwordEncoder.encode(plainPassword);
        logger.info("Password encrypted successfully, length: {}", encryptedPassword.length());
        return encryptedPassword;
    }

    /**
     * Verifies a plain text password against an encrypted password
     */
    public boolean verifyPassword(String plainPassword, String encryptedPassword) {
        if (plainPassword == null || encryptedPassword == null) {
            logger.warn("Cannot verify password: plain or encrypted password is null");
            return false;
        }
        
        boolean matches = passwordEncoder.matches(plainPassword, encryptedPassword);
        logger.info("Password verification result: {}", matches);
        return matches;
    }
}
