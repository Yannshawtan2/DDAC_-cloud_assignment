# Event Ads Service

A microservice for managing event advertisements in the DDac Group 18 healthcare system.

## Overview

This service provides RESTful APIs for managing event advertisements with image support. It follows the serverless architecture pattern and can be deployed as an AWS Lambda function.

## Features

- CRUD operations for event ads
- Image upload and storage support
- Base64 image encoding for web display
- Comprehensive validation and error handling
- RESTful API endpoints
- AWS Lambda ready

## API Endpoints

### Event Ads

- `GET /event-ads` - Get all event ads
- `GET /event-ads/{id}` - Get event ad by ID
- `POST /event-ads` - Create a new event ad
- `PUT /event-ads/{id}` - Update event ad by ID
- `DELETE /event-ads/{id}` - Delete event ad by ID

## Data Model

### EventAd
- `id` (Long) - Primary key
- `title` (String) - Event title
- `content` (String) - Event content/description
- `imageData` (byte[]) - Binary image data
- `image` (String) - Base64 encoded image for display (transient)

## Local Development

### Prerequisites
- Java 21
- Maven 3.6+
- MySQL 8.0+

### Setup
1. Clone the repository
2. Configure database connection in `application.properties`
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Build for AWS Lambda
```bash
mvn clean package
```

The Lambda JAR will be created as `event-ads-service-lambda.jar` in the `target` directory.

## AWS Lambda Deployment

1. Upload the JAR file to AWS Lambda
2. Set the handler to: `com.example.eventads.lambda.EventAdsLambdaHandler::handleRequest`
3. Configure environment variables for database connection
4. Set up API Gateway for HTTP endpoints

## Environment Variables

- `DB_URL` - Database connection URL
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password

## Image Handling

The service supports image upload and storage:
- Images are stored as binary data in the database
- Images are automatically converted to Base64 for web display
- The `image` field contains the Base64 data URI for direct use in HTML

## Dependencies

- Spring Boot 3.5.3
- Spring Data JPA
- MySQL Connector
- AWS Lambda Java Core
- AWS Serverless Java Container
- Jackson for JSON processing 