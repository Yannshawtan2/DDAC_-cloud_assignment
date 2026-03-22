package com.example.patientservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Patient Lambda microservice.
 * 
 * This microservice manages patient data and health information
 * for the DDac healthcare system.
 */
@SpringBootApplication
public class PatientLambdaApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PatientLambdaApplication.class, args);
    }
}