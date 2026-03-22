package com.example.quiz.lambda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.quiz.QuizLambdaApplication;

public class QuizLambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {
    private static final Logger logger = LoggerFactory.getLogger(QuizLambdaHandler.class);
    
    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
    
    static {
        try {
            // Log environment variables for debugging
            logger.info("SPRING_DATASOURCE_URL: {}", System.getenv("SPRING_DATASOURCE_URL"));
            logger.info("SPRING_DATASOURCE_USERNAME: {}", System.getenv("SPRING_DATASOURCE_USERNAME"));
            logger.info("SPRING_PROFILES_ACTIVE: {}", System.getenv("SPRING_PROFILES_ACTIVE"));
            
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(QuizLambdaApplication.class);
            logger.info("Spring Boot application initialized successfully");
        } catch (ContainerInitializationException e) {
            logger.error("Could not initialize Spring Boot application", e);
            logger.error("Environment variables - SPRING_DATASOURCE_URL: {}, SPRING_PROFILES_ACTIVE: {}", 
                System.getenv("SPRING_DATASOURCE_URL"), System.getenv("SPRING_PROFILES_ACTIVE"));
            throw new RuntimeException("Could not initialize Spring Boot application", e);
        }
    }

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest input, Context context) {
        try {
            logger.info("Processing request: {} {}", input.getHttpMethod(), input.getPath());
            return handler.proxy(input, context);
        } catch (Exception e) {
            logger.error("Error processing request: {} {}", input.getHttpMethod(), input.getPath(), e);
            throw e;
        }
    }
}