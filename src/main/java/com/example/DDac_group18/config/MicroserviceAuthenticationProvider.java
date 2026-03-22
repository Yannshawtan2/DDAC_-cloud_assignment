package com.example.DDac_group18.config;

import com.example.DDac_group18.clients.UserServiceClient;
import com.example.DDac_group18.model.data_schema.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@Component
public class MicroserviceAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(MicroserviceAuthenticationProvider.class);

    @Autowired
    private UserServiceClient userServiceClient;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        logger.info("Authenticating user: {}", email);

        try {
            // Use microservice to authenticate
            boolean isAuthenticated = userServiceClient.authenticateUser(email, password);
            
            if (isAuthenticated) {
                // Get user details for roles
                Users user = userServiceClient.getUserByEmail(email);
                if (user != null) {
                    String role = "ROLE_" + user.getRole().name();
                    logger.info("Authentication successful for user: {} with role: {}", email, role);
                    
                    return new UsernamePasswordAuthenticationToken(
                        email, 
                        password, 
                        Collections.singletonList(new SimpleGrantedAuthority(role))
                    );
                }
            }
            
            logger.warn("Authentication failed for user: {}", email);
            throw new BadCredentialsException("Invalid credentials");
            
        } catch (Exception e) {
            logger.error("Authentication error for user {}: {}", email, e.getMessage());
            throw new BadCredentialsException("Authentication failed");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
