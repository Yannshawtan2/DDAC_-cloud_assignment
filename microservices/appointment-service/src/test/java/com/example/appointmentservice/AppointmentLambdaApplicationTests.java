package com.example.appointmentservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AppointmentLambdaApplicationTests {

    @Test
    void contextLoads() {
        // Test that the Spring Boot context loads successfully
        // This is a basic smoke test for the Appointment Service microservice
    }

    @Test
    void applicationStarts() {
        // Test that the application starts without errors
        // Validates the appointment service configuration and dependencies
    }
}