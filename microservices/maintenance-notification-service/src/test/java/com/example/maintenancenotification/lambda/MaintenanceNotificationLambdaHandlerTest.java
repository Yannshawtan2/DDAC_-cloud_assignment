package com.example.maintenancenotification.lambda;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class MaintenanceNotificationLambdaHandlerTest {
    
    private MaintenanceNotificationLambdaHandler handler;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        // Set up test environment variables
        System.setProperty("SPRING_DATASOURCE_URL", "jdbc:h2:mem:testdb");
        System.setProperty("SPRING_DATASOURCE_USERNAME", "sa");
        System.setProperty("SPRING_DATASOURCE_PASSWORD", "");
        
        handler = new MaintenanceNotificationLambdaHandler();
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void testGetAllNotifications() throws IOException {
        // Load test event
        String eventJson = loadTestEvent("get-all-notifications.json");
        AwsProxyRequest request = objectMapper.readValue(eventJson, AwsProxyRequest.class);
        
        // Execute request
        AwsProxyResponse response = handler.handleRequest(request, null);
        
        // Verify response
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey("Content-Type"));
    }
    
    private String loadTestEvent(String filename) throws IOException {
        Path path = Paths.get("test-events", filename);
        return Files.readString(path);
    }
}
