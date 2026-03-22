package com.example.appointmentservice.lambda;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.appointmentservice.AppointmentLambdaApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AWS Lambda handler for Appointment Service
 * 
 * This class handles AWS Lambda requests by proxying them to the Spring Boot
 * application.
 * It initializes a SpringBootLambdaContainerHandler to process AwsProxyRequest
 * and return AwsProxyResponse.
 */
public class AppointmentLambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentLambdaHandler.class);

    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            // Set production profile for Lambda environment
            System.setProperty("spring.profiles.active", "production");

            // Validate required environment variables
            validateEnvironmentVariables();

            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(AppointmentLambdaApplication.class);

            // Enable CORS
            handler.onStartup(servletContext -> {
                // Additional startup configuration if needed
            });

            logger.info("AppointmentLambdaHandler initialized successfully");
        } catch (ContainerInitializationException e) {
            // If we fail here, we re-throw the exception to force another cold start
            logger.error("Could not initialize Spring Boot application", e);
            throw new RuntimeException("Could not initialize Spring Boot application", e);
        }
    }

    private static void validateEnvironmentVariables() {
        String[] requiredVars = { "SPRING_DATASOURCE_URL", "SPRING_DATASOURCE_USERNAME", "SPRING_DATASOURCE_PASSWORD" };

        for (String var : requiredVars) {
            String value = System.getenv(var);
            if (value == null || value.trim().isEmpty()) {
                logger.error("Required environment variable {} is not set or empty", var);
                throw new RuntimeException("Required environment variable " + var + " is not set or empty");
            }
        }

        logger.info("Environment variables validated successfully");
    }

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest input, Context context) {
        logger.info("Processing request: {} {}", input.getHttpMethod(), input.getPath());

        try {
            AwsProxyResponse response = handler.proxy(input, context);
            logger.info("Request processed successfully with status: {}", response.getStatusCode());
            return response;
        } catch (Exception e) {
            logger.error("Error processing request: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing request", e);
        }
    }
}