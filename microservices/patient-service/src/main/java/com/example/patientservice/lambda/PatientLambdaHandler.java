package com.example.patientservice.lambda;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.patientservice.PatientLambdaApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AWS Lambda handler for Patient Service
 * 
 * This class handles AWS Lambda requests by proxying them to the Spring Boot application.
 * It initializes a SpringBootLambdaContainerHandler to process AwsProxyRequest
 * and return AwsProxyResponse.
 */
public class PatientLambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {
    private static final Logger logger = LoggerFactory.getLogger(PatientLambdaHandler.class);
    
    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
    
    static {
        try {
            // Set production profile for Lambda environment
            System.setProperty("spring.profiles.active", "production");
            
            // Validate required environment variables
            validateEnvironmentVariables();
            
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(PatientLambdaApplication.class);
            
            // If you are using HTTP APIs with the version 2.0 of the proxy model, use the getHttpApiV2ProxyHandler() method
            // handler = SpringBootLambdaContainerHandler.getHttpApiV2ProxyHandler(PatientLambdaApplication.class);
        } catch (ContainerInitializationException e) {
            // If we fail here, we re-throw the exception to force another cold start
            logger.error("Could not initialize Spring Boot application", e);
            throw new RuntimeException("Could not initialize Spring Boot application", e);
        }
    }
    
    private static void validateEnvironmentVariables() {
        String[] requiredVars = {"SPRING_DATASOURCE_URL", "SPRING_DATASOURCE_USERNAME", "SPRING_DATASOURCE_PASSWORD"};
        
        for (String var : requiredVars) {
            String value = System.getenv(var);
            if (value == null || value.trim().isEmpty()) {
                logger.error("Required environment variable {} is not set or empty", var);
                throw new RuntimeException("Missing required environment variable: " + var);
            }
        }
        
        logger.info("All required environment variables are present");
    }
    
    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest input, Context context) {
        logger.info("Processing request: {} {}", input.getHttpMethod(), input.getPath());
        return handler.proxy(input, context);
    }
}