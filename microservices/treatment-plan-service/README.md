# Treatment Plan Service

A microservice for managing treatment plans in the DDac healthcare application.

## Overview

The Treatment Plan Service provides REST API endpoints for creating, reading, updating, and deleting treatment plans. It integrates with the User Service to fetch patient and doctor information and stores treatment plan data in a MySQL database.

## Features

- **CRUD Operations**: Create, read, update, and delete treatment plans
- **User Integration**: Fetches patient and doctor information from User Service
- **Status Management**: Track treatment plan status (ACTIVE, COMPLETED, PAUSED, CANCELLED)
- **Search Functionality**: Search treatment plans by title
- **Statistics**: Get treatment plan counts and statistics by doctor
- **Ownership Validation**: Verify treatment plan ownership by doctors
- **AWS Lambda Support**: Deployed as serverless functions

## API Endpoints

### Treatment Plans
- `POST /treatment-plans` - Create a new treatment plan
- `GET /treatment-plans` - Get all treatment plans
- `GET /treatment-plans/{id}` - Get treatment plan by ID
- `PUT /treatment-plans/{id}` - Update treatment plan
- `DELETE /treatment-plans/{id}` - Delete treatment plan

### By Doctor
- `GET /treatment-plans/doctor/{doctorId}` - Get treatment plans by doctor
- `GET /treatment-plans/doctor/{doctorId}/stats` - Get doctor's treatment plan statistics
- `GET /treatment-plans/doctor/{doctorId}/recent` - Get recent treatment plans by doctor
- `GET /treatment-plans/doctor/{doctorId}/status/{status}` - Get treatment plans by doctor and status

### By Patient
- `GET /treatment-plans/patient/{patientId}` - Get treatment plans by patient

### Search and Filter
- `GET /treatment-plans/search?keyword={keyword}&doctorId={doctorId}` - Search treatment plans
- `GET /treatment-plans/status/{status}` - Get treatment plans by status

### Status Management
- `PUT /treatment-plans/{id}/status` - Update treatment plan status

### Utility
- `GET /treatment-plans/{id}/ownership/{doctorId}` - Check treatment plan ownership
- `GET /treatment-plans/health` - Health check endpoint

## Data Model

### TreatmentPlan Entity
```java
{
  "id": "Long",
  "title": "String",
  "description": "String", 
  "patientId": "Long",
  "doctorId": "Long",
  "medication": "String",
  "dosage": "String",
  "frequency": "String",
  "instructions": "String",
  "startDate": "LocalDateTime",
  "endDate": "LocalDateTime",
  "status": "Status", // ACTIVE, COMPLETED, PAUSED, CANCELLED
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime",
  "notes": "String"
}
```

### TreatmentPlanDto Response
The response DTOs include additional user information fetched from the User Service:
```java
{
  // ... all TreatmentPlan fields plus:
  "patientName": "String",
  "patientEmail": "String", 
  "doctorName": "String",
  "doctorEmail": "String"
}
```

## User Service Integration

This service integrates with the User Service API at:
- Base URL: `https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user`
- Endpoint: `/users/{id}` to fetch user information

The service validates that:
- Patient IDs correspond to users with role "PATIENT"
- Doctor IDs correspond to users with role "DOCTOR"
- User information is enriched in response DTOs

## Environment Configuration

### Database
```properties
spring.datasource.url=jdbc:mysql://your-rds-endpoint:3306/ddac_group18
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

### User Service Integration
```properties
user.service.url=https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user
```

## Building and Deployment

### Build
```bash
mvn clean package
```

### Local Development
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Lambda Deployment
The service is configured for AWS Lambda deployment with the Maven Shade plugin. The generated JAR includes all dependencies and can be deployed to AWS Lambda.

## Testing

Test events are provided in the `test-events/` directory for testing various endpoints:

- `create-treatment-plan.json` - Create a new treatment plan
- `get-all-treatment-plans.json` - Get all treatment plans
- `get-by-doctor.json` - Get treatment plans by doctor
- `get-by-patient.json` - Get treatment plans by patient  
- `update-treatment-plan.json` - Update treatment plan
- `delete-treatment-plan.json` - Delete treatment plan
- `get-doctor-stats.json` - Get doctor statistics
- `search-treatment-plans.json` - Search treatment plans
- `health-check.json` - Health check

## Dependencies

- Spring Boot 3.5.3
- Spring Data JPA
- Spring Boot Web
- Spring Boot WebFlux (for User Service integration)
- MySQL Connector
- AWS Lambda Java Core
- AWS Serverless Java Container
- Jackson (JSON processing)
- SLF4J (Logging)

## Architecture

The service follows a layered architecture:

```
Controller Layer (REST endpoints)
    ↓
Service Layer (Business logic)
    ↓
Repository Layer (Data access)
    ↓
Database (MySQL)

External Integration:
Service Layer → UserServiceClient → User Service API
```

## Error Handling

The service includes comprehensive error handling:
- Input validation with proper error messages
- User existence validation via User Service
- Role validation (patient/doctor)
- Ownership validation for security
- Graceful handling of external service failures

## Logging

Structured logging is implemented throughout the service using SLF4J with configurable log levels for different packages.

## Security Considerations

- Treatment plan ownership validation
- User role validation through User Service
- Input validation and sanitization
- Error message sanitization to prevent information leakage