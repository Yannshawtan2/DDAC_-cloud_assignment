package com.example.DDac_group18.services;

import com.example.DDac_group18.clients.UserServiceClient;
import com.example.DDac_group18.model.data_schema.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Attempting to load user with email: {}", email);

        Users user = userServiceClient.getUserByEmail(email);
        if (user == null) {
            logger.error("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found");
        }

        // Spring Security expects roles to be prefixed with 'ROLE_'
        String role = "ROLE_" + user.getRole().name();
        logger.info("User found with role: {}", role);

        // Since the microservice stores plain text passwords, we need to encode them
        // for Spring Security. We'll use a placeholder password and handle authentication
        // through our custom authentication provider
        String encodedPassword = passwordEncoder.encode("placeholder");

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                encodedPassword,
                Collections.singletonList(new SimpleGrantedAuthority(role)));
    }
}