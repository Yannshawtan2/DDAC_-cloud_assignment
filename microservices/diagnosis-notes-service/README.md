# Diagnosis Notes Service

A microservice for managing diagnosis notes in the DDac Group 18 healthcare system.

## Overview

This service provides RESTful APIs for managing diagnosis notes associated with medical appointments. It follows the serverless architecture pattern and can be deployed as an AWS Lambda function.

## Features

- CRUD operations for diagnosis notes
- Appointment-based diagnosis note management
- Comprehensive validation and error handling
- RESTful API endpoints
- AWS Lambda ready

## API Endpoints

### Diagnosis Notes

- `GET /diagnosis-notes` - Get all diagnosis notes
- `GET /diagnosis-notes/{id}` - Get diagnosis note by ID
- `GET /diagnosis-notes/appointment/{appointmentId}` - Get diagnosis note by appointment ID
- `POST /diagnosis-notes` - Create a new diagnosis note
- `PUT /diagnosis-notes/{id}` - Update diagnosis note by ID
- `PUT /diagnosis-notes/appointment/{appointmentId}` - Update diagnosis note by appointment ID
- `DELETE /diagnosis-notes/{id}` - Delete diagnosis note by ID
- `DELETE /diagnosis-notes/appointment/{appointmentId}` - Delete diagnosis note by appointment ID

## Data Model

### DiagnosisNote
- `id` (Long) - Primary key
- `appointmentId` (Long) - Associated appointment ID
- `name` (String) - Patient name
- `gender` (String) - Patient gender
- `age` (Integer) - Patient age
- `appointmentDate` (LocalDate) - Appointment date
- `foreignId` (String) - Foreign ID
- `height` (Double) - Patient height
- `weight` (Double) - Patient weight
- `bloodSugarIndex` (Double) - Blood sugar index
- `diagnosisNote` (String) - Diagnosis note content

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

The Lambda JAR will be created as `diagnosis-notes-service-lambda.jar` in the `target` directory.

## AWS Lambda Deployment

1. Upload the JAR file to AWS Lambda
2. Set the handler to: `com.example.diagnosisnotes.lambda.DiagnosisNotesLambdaHandler::handleRequest`
3. Configure environment variables for database connection
4. Set up API Gateway for HTTP endpoints

## Environment Variables

- `DB_URL` - Database connection URL
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password

## Dependencies

- Spring Boot 3.5.3
- Spring Data JPA
- MySQL Connector
- AWS Lambda Java Core
- AWS Serverless Java Container
- Jackson for JSON processing 