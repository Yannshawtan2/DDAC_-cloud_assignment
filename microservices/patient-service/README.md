# Patient Service

This microservice manages patient data and health information for the DDac healthcare system.

## Overview

The Patient Service provides a standardized API for managing patient profiles, health data, and patient-related operations. It follows the microservices architecture pattern with AWS Lambda integration.

## Features

- ✅ Spring Boot 3.5.3 with Java 21
- ✅ AWS Lambda integration
- ✅ JPA with MySQL database
- ✅ RESTful API endpoints
- ✅ DTO pattern for API communication
- ✅ Service layer for business logic
- ✅ Repository layer for data access
- ✅ Error handling and validation
- ✅ CORS configuration
- ✅ Patient health data management
- ✅ Integration with existing user system

## API Endpoints

```
GET    /api/patients           - Get all patients
GET    /api/patients/{id}      - Get patient by ID
GET    /api/patients?name={n}  - Search patients by name
GET    /api/patients?email={e} - Get patient by email
POST   /api/patients           - Create new patient
PUT    /api/patients/{id}      - Update patient
DELETE /api/patients/{id}      - Delete patient

GET    /api/patients/{id}/health-data     - Get patient health data
POST   /api/patients/{id}/health-data     - Create/Update health data
PUT    /api/patients/{id}/health-data     - Update health data
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/patientservice/
│   │   ├── PatientLambdaApplication.java     # Main application class
│   │   ├── dto/                              # Data Transfer Objects
│   │   │   ├── PatientDto.java
│   │   │   ├── CreatePatientRequest.java
│   │   │   ├── UpdatePatientRequest.java
│   │   │   ├── PatientHealthDataDto.java
│   │   │   ├── CreateHealthDataRequest.java
│   │   │   └── UpdateHealthDataRequest.java
│   │   ├── lambda/                           # Lambda handlers
│   │   │   └── PatientLambdaHandler.java
│   │   ├── model/                            # JPA entities
│   │   │   ├── Patient.java
│   │   │   └── PatientHealthData.java
│   │   ├── repository/                       # Data access layer
│   │   │   ├── PatientRepository.java
│   │   │   └── PatientHealthDataRepository.java
│   │   ├── service/                          # Business logic layer
│   │   │   ├── PatientService.java
│   │   │   └── PatientHealthDataService.java
│   │   └── controller/                       # REST controllers
│   │       ├── PatientController.java
│   │       └── PatientHealthDataController.java
│   └── resources/
│       └── application.properties            # Configuration
└── test/
    └── java/com/example/patientservice/
        └── PatientServiceTest.java           # Integration tests
```

## Database Schema

```sql
CREATE TABLE patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

CREATE TABLE patient_health_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    weight DOUBLE,
    weight_updated_at DATETIME,
    height DOUBLE,
    height_updated_at DATETIME,
    waist_circumference DOUBLE,
    waist_circumference_updated_at DATETIME,
    blood_pressure VARCHAR(50),
    blood_pressure_updated_at DATETIME,
    blood_glucose_level DOUBLE,
    blood_glucose_level_updated_at DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (patient_id) REFERENCES patients(id)
);
```

## Configuration

### Environment Variables (AWS Lambda)
```
DB_HOST=your-rds-endpoint
DB_PORT=3306
DB_NAME=patient_db
DB_USERNAME=patient_user
DB_PASSWORD=secure_password
```

### Local Development
Update `src/main/resources/application.properties` with your local database settings.

## Building and Deployment

### Build
```bash
mvn clean package
```

### Deploy to AWS Lambda
```bash
aws lambda create-function \
    --function-name patient-service \
    --runtime java21 \
    --role arn:aws:iam::your-account:role/lambda-execution-role \
    --handler com.example.patientservice.lambda.PatientLambdaHandler \
    --zip-file fileb://target/patient-service-1.0.0-aws.jar \
    --timeout 30 \
    --memory-size 512
```

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

## Migration from Monolith

This service extracts patient management functionality from the main DDac application:

- **From**: `PatientService.java` in main application
- **From**: `PatientController.java` patient-related endpoints
- **From**: `Users` entity (PATIENT role only)
- **From**: `Data` entity (patient health data)

## License

This service is part of the DDac healthcare system project.