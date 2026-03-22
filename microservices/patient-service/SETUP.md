# Patient Service - Quick Setup Guide

This guide helps you quickly set up and run the Patient Service microservice for development.

## Prerequisites

- **Java 11+** - [Download here](https://adoptium.net/)
- **Maven 3.6+** - [Download here](https://maven.apache.org/download.cgi)
- **MySQL 8.0+** - [Download here](https://dev.mysql.com/downloads/mysql/) or use Docker
- **Git** - [Download here](https://git-scm.com/downloads)

## Quick Start (5 minutes)

### 1. Database Setup

**Option A: Using Docker (Recommended)**
```bash
# Start MySQL container
docker run --name patient-mysql \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=patient_service_db \
  -p 3306:3306 \
  -d mysql:8.0
```

**Option B: Local MySQL Installation**
```sql
-- Connect to MySQL as root
mysql -u root -p

-- Create database
CREATE DATABASE patient_service_db;

-- Exit MySQL
exit;
```

### 2. Clone and Build

```bash
# Navigate to the patient-service directory
cd microservices/patient-service

# Build the project
mvn clean compile

# Run tests (optional)
mvn test
```

### 3. Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
# Update these lines with your database credentials
spring.datasource.url=jdbc:mysql://localhost:3306/patient_service_db
spring.datasource.username=root
spring.datasource.password=password
```

### 4. Run the Service

```bash
# Package and run
mvn spring-boot:run

# OR build JAR and run
mvn package
java -jar target/patient-service-1.0.0.jar
```

### 5. Test the Service

Open your browser or use curl to test:

```bash
# Health check
curl http://localhost:8080/actuator/health

# Get all patients (should return empty array initially)
curl http://localhost:8080/api/patients

# Create a test patient
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","name":"Test Patient"}'
```

## Development Workflow

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=PatientServiceTest

# Run tests with coverage
mvn test jacoco:report
```

### Hot Reload Development

```bash
# Run with Spring Boot DevTools for hot reload
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev"
```

### Database Management

```bash
# Reset database (drops and recreates tables)
# Set spring.jpa.hibernate.ddl-auto=create-drop in application.properties
# Then restart the application

# View database schema
mysql -u root -p patient_service_db
SHOW TABLES;
DESCRIBE patients;
DESCRIBE patient_health_data;
```

## API Testing

### Using curl

```bash
# Create patient
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{"email":"john.doe@example.com","name":"John Doe"}'

# Get patient by ID
curl http://localhost:8080/api/patients/1

# Update patient
curl -X PUT http://localhost:8080/api/patients/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"John Smith"}'

# Create health data
curl -X POST http://localhost:8080/api/health-data \
  -H "Content-Type: application/json" \
  -d '{"patientId":1,"weight":70.5,"height":175.0,"bloodPressure":"120/80"}'

# Get health data by patient
curl http://localhost:8080/api/health-data/patient/1
```

### Using Postman

1. Import the API collection (create one based on the endpoints in README.md)
2. Set base URL to `http://localhost:8080`
3. Test all CRUD operations

## IDE Setup

### IntelliJ IDEA

1. Open the `patient-service` folder as a Maven project
2. Configure Java SDK (11+)
3. Enable annotation processing
4. Install Spring Boot plugin
5. Set up run configuration:
   - Main class: `com.example.patientservice.PatientLambdaApplication`
   - VM options: `-Dspring.profiles.active=dev`

### VS Code

1. Install extensions:
   - Extension Pack for Java
   - Spring Boot Extension Pack
2. Open the `patient-service` folder
3. Configure Java path in settings
4. Use Command Palette: "Spring Boot: Run" to start the application

### Eclipse

1. Import as Maven project
2. Configure Java Build Path
3. Install Spring Tools 4
4. Right-click project → Run As → Spring Boot App

## Troubleshooting

### Common Issues

**1. Database Connection Failed**
```
Solution: Check if MySQL is running and credentials are correct
- Verify MySQL service is started
- Test connection: mysql -u root -p
- Check application.properties configuration
```

**2. Port 8080 Already in Use**
```
Solution: Change port in application.properties
server.port=8081
```

**3. Maven Build Fails**
```
Solution: Clean and rebuild
mvn clean install -U
```

**4. Tests Fail**
```
Solution: Check H2 dependency and test configuration
- Verify H2 is in test scope
- Check application-test.properties
```

### Debugging

**Enable Debug Logging:**
```properties
# Add to application.properties
logging.level.com.example.patientservice=DEBUG
logging.level.org.springframework.web=DEBUG
```

**Database Query Logging:**
```properties
# Add to application.properties
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
```

## Environment Profiles

### Development Profile
Create `application-dev.properties`:
```properties
# Development settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
logging.level.com.example.patientservice=DEBUG
```

Run with: `java -jar app.jar --spring.profiles.active=dev`

### Production Profile
Create `application-prod.properties`:
```properties
# Production settings
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.com.example.patientservice=WARN
```

## Next Steps

1. **Read the full documentation:** Check `README.md` for complete API reference
2. **Deploy to AWS:** Follow `DEPLOYMENT.md` for AWS Lambda deployment
3. **Integration:** Connect with other microservices
4. **Security:** Implement authentication and authorization
5. **Monitoring:** Set up logging and metrics

## Getting Help

- **Documentation:** Check `README.md` and `DEPLOYMENT.md`
- **Logs:** Check application logs for error details
- **Database:** Use MySQL Workbench or command line to inspect data
- **API Testing:** Use Postman or curl for endpoint testing

## Useful Commands

```bash
# Quick build and run
mvn clean spring-boot:run

# Package for deployment
mvn clean package

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Skip tests during build
mvn clean package -DskipTests

# Generate test coverage report
mvn clean test jacoco:report

# Check for dependency updates
mvn versions:display-dependency-updates
```

You're now ready to develop with the Patient Service! 🚀