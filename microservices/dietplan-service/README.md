# Diet Plan Service

A microservice for managing diet plans in the DDac healthcare application. This service handles CRUD operations for diet plans, including creation, retrieval, updates, and deletion of diet plans assigned by dietitians to patients.

## Features

- **Diet Plan Management**: Create, read, update, and delete diet plans
- **Patient Management**: Manage diet plans by patient ID
- **Dietitian Management**: Manage diet plans by dietitian ID
- **Status Management**: Track diet plan status (ACTIVE, COMPLETED, SUSPENDED)
- **Statistics**: Get statistics for dietitians and patients
- **RESTful API**: Full REST API with comprehensive endpoints
- **AWS Lambda Support**: Deployable as AWS Lambda function

## API Endpoints

### Diet Plan Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/diet-plans` | Create a new diet plan |
| GET | `/diet-plans` | Get all diet plans |
| GET | `/diet-plans/{id}` | Get diet plan by ID |
| PUT | `/diet-plans/{id}` | Update diet plan |
| DELETE | `/diet-plans/{id}` | Delete diet plan |

### Patient-specific Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/diet-plans/patient/{patientId}` | Get diet plans for a patient |
| GET | `/diet-plans/patient/{patientId}/status/{status}` | Get diet plans for a patient by status |
| GET | `/diet-plans/stats/patient/{patientId}` | Get diet plan statistics for a patient |

### Dietitian-specific Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/diet-plans/dietitian/{dietitianId}` | Get diet plans for a dietitian |
| GET | `/diet-plans/dietitian/{dietitianId}/status/{status}` | Get diet plans for a dietitian by status |
| GET | `/diet-plans/stats/dietitian/{dietitianId}` | Get diet plan statistics for a dietitian |

### Status Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/diet-plans/status/{status}` | Get diet plans by status |
| PUT | `/diet-plans/{id}/status` | Update diet plan status |

### Health Check

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/diet-plans/health` | Health check endpoint |

## Data Models

### DietPlan
- `id`: Unique identifier
- `title`: Diet plan title
- `description`: Detailed description
- `patientId`: ID of the patient
- `dietitianId`: ID of the dietitian
- `startDate`: Plan start date
- `endDate`: Plan end date
- `breakfast`: Breakfast recommendations
- `lunch`: Lunch recommendations
- `dinner`: Dinner recommendations
- `snacks`: Snack recommendations
- `dailyCalories`: Target daily calories
- `specialInstructions`: Special instructions
- `dietaryRestrictions`: Dietary restrictions
- `status`: Plan status (ACTIVE, COMPLETED, SUSPENDED)
- `createdAt`: Creation timestamp
- `updatedAt`: Last update timestamp

### User Integration
The service integrates with the existing User Service API at:
`https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user/users`

User validation is performed by calling this API to ensure:
- User exists
- User has the correct role (PATIENT for patients, DIETICIAN for dietitians)

## Request/Response Examples

### Create Diet Plan
```json
POST /diet-plans
{
  "title": "Mediterranean Diet Plan",
  "description": "A healthy Mediterranean-style diet plan",
  "patientId": 1,
  "dietitianId": 2,
  "startDate": "2024-01-15",
  "endDate": "2024-04-15",
  "breakfast": "Greek yogurt with berries",
  "lunch": "Grilled chicken salad",
  "dinner": "Baked salmon with quinoa",
  "snacks": "Almonds, fresh fruits",
  "dailyCalories": 1800,
  "specialInstructions": "Drink plenty of water",
  "dietaryRestrictions": "No dairy allergies",
  "status": "ACTIVE"
}
```

### Update Diet Plan Status
```json
PUT /diet-plans/1/status
{
  "status": "COMPLETED"
}
```

## Setup and Deployment

### Local Development

1. **Prerequisites**
   - Java 17+
   - Maven 3.6+
   - MySQL 8.0+

2. **Database Setup**
   ```sql
   CREATE DATABASE ddac_group18;
   ```

3. **Configuration**
   Update `application-production.properties` with your database credentials and user service URL:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ddac_group18
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   user.service.url=https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user/users
   ```

4. **Run Application**
   ```bash
   mvn spring-boot:run
   ```

### AWS Lambda Deployment

1. **Build the application**
   ```bash
   mvn clean package
   ```

2. **Deploy to AWS Lambda**
   - Upload the generated `dietplan-service-lambda.jar` to AWS Lambda
   - Set handler to: `com.example.dietplan.lambda.DietPlanLambdaHandler`
   - Configure environment variables for database connection

## Testing

Test events are available in the `test-events/` directory:

- `create-diet-plan.json` - Test diet plan creation
- `get-all-diet-plans.json` - Test retrieving all diet plans
- `get-by-patient.json` - Test patient-specific retrieval
- `get-by-dietitian.json` - Test dietitian-specific retrieval
- `update-diet-plan.json` - Test diet plan updates
- `update-status.json` - Test status updates
- `delete-diet-plan.json` - Test diet plan deletion
- `get-dietitian-stats.json` - Test statistics retrieval

## Architecture

This microservice follows the standard Spring Boot architecture:

- **Controller Layer**: REST endpoints (`DietPlanController`)
- **Service Layer**: Business logic (`DietPlanService`)
- **Repository Layer**: Data access (`DietPlanRepository`, `UserRepository`)
- **Model Layer**: Data entities (`DietPlan`, `User`)
- **DTO Layer**: Data transfer objects for API communication

## Dependencies

- Spring Boot 3.5.3
- Spring Data JPA
- Spring Web
- Spring WebFlux (for HTTP client)
- MySQL Connector
- AWS Lambda Java Core
- AWS Serverless Java Container
- Jakarta Validation

## External Service Dependencies

- **User Service**: `https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user/users`
  - Used for user validation and role checking
  - Must be accessible from the diet plan service

## Error Handling

The service includes comprehensive error handling:

- **400 Bad Request**: Invalid input data or parameters
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Unexpected server errors

All errors return JSON responses with descriptive error messages.

## Logging

The service uses SLF4J for logging with different levels:
- **INFO**: General operation information
- **WARN**: Warning conditions
- **ERROR**: Error conditions

## Security Considerations

- Input validation using Jakarta Validation
- SQL injection prevention through JPA/Hibernate
- Cross-origin resource sharing (CORS) enabled for web integration