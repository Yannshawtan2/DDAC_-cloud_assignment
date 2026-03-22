# Quiz Service Setup Guide

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- AWS CLI (for deployment)

## Local Development Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd DDac_group18/microservices/quiz-service
```

### 2. Database Setup

#### Option A: Local MySQL
1. Install MySQL 8.0+
2. Create database:
```sql
CREATE DATABASE ddac_quiz_dev;
```

3. Create application-dev.properties:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ddac_quiz_dev
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=true

server.port=8083
spring.application.name=quiz-service

logging.level.com.example.quiz=DEBUG
```

#### Option B: Docker MySQL
```bash
docker run --name quiz-mysql \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=ddac_quiz_dev \
  -p 3306:3306 \
  -d mysql:8.0
```

### 3. Build the Application
```bash
mvn clean compile
```

### 4. Run Tests
```bash
mvn test
```

### 5. Run the Application
```bash
# Set Spring profile
export SPRING_PROFILES_ACTIVE=dev

# Run the application
mvn spring-boot:run
```

The service will be available at `http://localhost:8083`

## API Documentation

### Base URL
- Local: `http://localhost:8083`
- Production: `https://your-api-gateway-url`

### Quiz Questions Endpoints

#### Get All Questions
```bash
GET /quiz/questions
GET /quiz/questions?section=Diet
GET /quiz/questions?active=true
```

#### Get Question by ID
```bash
GET /quiz/questions/{id}
```

#### Create Question
```bash
POST /quiz/questions
Content-Type: application/json

{
  "questionText": "How often do you monitor your carbohydrate intake?",
  "description": "Rate how consistently you track carbs in your daily meals",
  "section": "Diet",
  "createdBy": "dietician@example.com",
  "isActive": true
}
```

#### Update Question
```bash
PUT /quiz/questions/{id}
Content-Type: application/json

{
  "questionText": "Updated question text",
  "section": "Diet",
  "isActive": true
}
```

#### Delete Question
```bash
DELETE /quiz/questions/{id}
```

### Quiz Sections Endpoints

#### Get All Sections
```bash
GET /quiz/sections
GET /quiz/sections?active=true
```

#### Get Questions Grouped by Section
```bash
GET /quiz/questions/grouped
GET /quiz/questions/grouped?active=true
```

#### Get Quiz Statistics
```bash
GET /quiz/stats
```

### Quiz Responses Endpoints

#### Submit Quiz
```bash
POST /quiz/submit
Content-Type: application/json

{
  "patientId": "patient@example.com",
  "answers": {
    "1": 4,
    "2": 3,
    "3": 5
  }
}
```

#### Get All Quiz Responses
```bash
GET /quiz/responses
GET /quiz/responses?patientId=patient@example.com
GET /quiz/responses?recent=true
```

#### Get Quiz Response by ID
```bash
GET /quiz/responses/{id}
```

#### Get User Quiz Count
```bash
GET /quiz/patient/{patientId}/count
```

#### Get User Quiz History
```bash
GET /quiz/patient/{patientId}/history
```

## Testing with Sample Data

### 1. Create Sample Questions
```bash
# Create Diet questions
curl -X POST http://localhost:8083/quiz/questions \
  -H "Content-Type: application/json" \
  -d '{
    "questionText": "How often do you monitor your carbohydrate intake?",
    "description": "Rate how consistently you track carbs in your daily meals",
    "section": "Diet",
    "createdBy": "dietician@example.com"
  }'

curl -X POST http://localhost:8083/quiz/questions \
  -H "Content-Type: application/json" \
  -d '{
    "questionText": "How well do you follow portion control guidelines?",
    "description": "Rate how well you control your serving sizes",
    "section": "Diet",
    "createdBy": "dietician@example.com"
  }'
```

### 2. Submit Sample Quiz
```bash
curl -X POST http://localhost:8083/quiz/submit \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "patient@example.com",
    "answers": {
      "1": 4,
      "2": 3
    }
  }'
```

### 3. Get Quiz Results
```bash
curl http://localhost:8083/quiz/responses?patientId=patient@example.com
```

## Configuration

### Application Properties
Key configuration properties:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/ddac_quiz_dev
spring.datasource.username=root
spring.datasource.password=password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Server
server.port=8083
spring.application.name=quiz-service

# Logging
logging.level.com.example.quiz=DEBUG
```

### Environment Variables
For production deployment:

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_URL` | Database JDBC URL | `jdbc:mysql://localhost:3306/ddac_quiz_dev` |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | `password` |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `dev` |

## Development Workflow

### 1. Make Changes
- Modify code in your IDE
- Add tests for new functionality

### 2. Test Changes
```bash
mvn test
```

### 3. Test Locally
```bash
mvn spring-boot:run
```

### 4. Build for Deployment
```bash
mvn clean package
```

### 5. Deploy to AWS
Follow the DEPLOYMENT.md guide

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check MySQL is running
   - Verify connection details
   - Check firewall settings

2. **Port Already in Use**
   - Change server.port in application.properties
   - Kill existing process: `lsof -ti:8083 | xargs kill`

3. **Maven Build Fails**
   - Check Java version: `java -version`
   - Clear Maven cache: `mvn clean`
   - Update dependencies: `mvn dependency:resolve`

### Debug Mode
Enable debug logging:
```properties
logging.level.com.example.quiz=DEBUG
logging.level.org.springframework.web=DEBUG
```

## IDE Setup

### IntelliJ IDEA
1. Import as Maven project
2. Set Project SDK to Java 21
3. Enable annotation processing
4. Install Spring Boot plugin

### VS Code
1. Install Java Extension Pack
2. Install Spring Boot Extension Pack
3. Set java.home to Java 21 path

## Database Schema

The application uses JPA/Hibernate to automatically create tables:

- `quiz_questions`: Stores quiz questions
- `quiz_responses`: Stores user quiz submissions
- `quiz_answers`: Stores individual question answers

Schema is created automatically with `spring.jpa.hibernate.ddl-auto=update`