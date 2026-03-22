# Quiz Service Microservice

A Spring Boot microservice for managing diabetes diet quiz questions, responses, and analytics.

## Overview

The Quiz Service provides comprehensive functionality for:
- Creating and managing quiz questions by dieticians
- Submitting quiz responses by patients
- Generating personalized recommendations based on quiz results
- Section-based scoring and analytics
- Integration with User Service for authentication and authorization

## Features

### Quiz Question Management
- Create, read, update, and delete quiz questions
- Section-based organization (Diet, Physical Activity, Sleep Schedule, etc.)
- Active/inactive question status
- Dietician-only question creation with validation

### Quiz Response Handling
- Patient quiz submission with validation
- Section-based scoring and recommendations
- Comprehensive analytics and reporting
- Historical tracking of patient responses

### User Integration
- Integration with User Service API Gateway
- Role-based authorization (DIETICIAN, PATIENT)
- User validation for all operations

## API Endpoints

### Quiz Questions
- `GET /quiz/questions` - Get all questions (with optional filtering)
- `GET /quiz/questions/{id}` - Get specific question
- `POST /quiz/questions` - Create new question (DIETICIAN only)
- `PUT /quiz/questions/{id}` - Update question (DIETICIAN only)
- `DELETE /quiz/questions/{id}` - Delete question (DIETICIAN only)

### Quiz Sections
- `GET /quiz/sections` - Get all sections
- `GET /quiz/questions/grouped` - Get questions grouped by section
- `GET /quiz/stats` - Get quiz statistics

### Quiz Responses
- `POST /quiz/submit` - Submit quiz response (PATIENT only)
- `GET /quiz/responses` - Get all quiz responses
- `GET /quiz/responses/{id}` - Get specific quiz response
- `GET /quiz/patient/{patientId}/history` - Get patient quiz history
- `GET /quiz/patient/{patientId}/count` - Get patient quiz count

### Health Check
- `GET /quiz/health` - Service health check

## Configuration

### Environment Variables

```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://your-db-host:3306/ddac_quiz_db
SPRING_DATASOURCE_USERNAME=your-db-username
SPRING_DATASOURCE_PASSWORD=your-db-password

# User Service Integration
USER_SERVICE_URL=https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user

# Spring Profile
SPRING_PROFILES_ACTIVE=production
```

### Database Schema

The service uses the following tables:
- `quiz_questions` - Quiz question storage
- `quiz_responses` - Patient quiz responses
- `quiz_answers` - Individual question answers

## Deployment

### Prerequisites
- Java 17+
- Maven 3.6+
- AWS CLI configured
- MySQL database

### Local Development

```bash
# Install dependencies
mvn clean install

# Run locally
mvn spring-boot:run

# Run tests
mvn test
```

### AWS Lambda Deployment

#### Using Shell Script (Linux/Mac)
```bash
./deploy.sh
```

#### Using Batch Script (Windows)
```batch
deploy.bat
```

#### Manual Deployment
```bash
# Build JAR
mvn clean package

# Deploy to Lambda
aws lambda update-function-code \
  --function-name ddac-quiz-service \
  --zip-file fileb://target/quiz-service-lambda.jar
```

## Testing

### Test Events

Use the provided test events in the `test-events/` directory:

```bash
# Test question creation
aws lambda invoke \
  --function-name ddac-quiz-service \
  --payload file://test-events/create-question-diet.json \
  response.json

# Test quiz submission
aws lambda invoke \
  --function-name ddac-quiz-service \
  --payload file://test-events/submit-quiz-patient.json \
  response.json
```

### Sample Test Data

#### Create Question Request
```json
{
  "questionText": "How often do you monitor your carbohydrate intake?",
  "description": "Rate consistency of carb tracking",
  "section": "Diet",
  "createdBy": "Diet@email.com",
  "isActive": true
}
```

#### Submit Quiz Request
```json
{
  "patientId": "patient@email.com",
  "answers": {
    "1": 4,
    "2": 3,
    "3": 5,
    "4": 2,
    "5": 4
  }
}
```

## Architecture

### Service Dependencies
- **User Service**: Authentication and user management
- **Database**: MySQL for data persistence
- **AWS Lambda**: Serverless execution environment

### Key Components
- **Controller Layer**: REST API endpoints
- **Service Layer**: Business logic and user integration
- **Repository Layer**: Data access
- **DTO Layer**: Data transfer objects
- **Lambda Handler**: AWS Lambda integration

### Security
- Role-based access control
- User validation via User Service API
- Input validation and sanitization

## Monitoring and Logging

### Health Checks
- `/quiz/health` endpoint for service status
- Database connectivity validation

### Logging
- Comprehensive logging with SLF4J
- Request/response logging
- Error tracking and debugging

### Metrics
- Quiz submission rates
- Section-based analytics
- User engagement tracking

## Development Guidelines

### Adding New Sections
1. Questions support any section name
2. Recommendations are automatically generated
3. Update `generateRecommendationForSection()` for custom section logic

### Adding New Endpoints
1. Add endpoint to `QuizController`
2. Implement business logic in `QuizService`
3. Add corresponding test events
4. Update this README

### Error Handling
- Use proper HTTP status codes
- Return structured error responses
- Log all errors with context

## Support

For issues and questions:
1. Check the logs for detailed error information
2. Verify User Service connectivity
3. Ensure database connectivity
4. Review environment variable configuration

## Version History

- **v1.0.0** - Initial release with basic quiz functionality
- **v1.1.0** - Added User Service integration and authorization
- **v1.2.0** - Enhanced section-based scoring and recommendations