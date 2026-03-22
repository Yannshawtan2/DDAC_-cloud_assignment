# Diet Plan Service Setup Guide

This guide provides step-by-step instructions for setting up and running the Diet Plan Service.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17 or higher**
- **Maven 3.6 or higher**
- **MySQL 8.0 or higher**
- **AWS CLI** (for AWS deployment)

## Local Development Setup

### 1. Database Configuration

1. **Create the database:**
   ```sql
   CREATE DATABASE ddac_group18;
   USE ddac_group18;
   ```

2. **Create a database user (optional but recommended):**
   ```sql
   CREATE USER 'dietplan_user'@'localhost' IDENTIFIED BY 'dietplan_password';
   GRANT ALL PRIVILEGES ON ddac_group18.* TO 'dietplan_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

### 2. Application Configuration

1. **Navigate to the project directory:**
   ```bash
   cd microservices/dietplan-service
   ```

2. **Update database configuration in `src/main/resources/application-production.properties`:**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ddac_group18
   spring.datasource.username=dietplan_user
   spring.datasource.password=dietplan_password
   ```

### 3. Build and Run

1. **Install dependencies and build:**
   ```bash
   mvn clean install
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=production
   ```

3. **Verify the service is running:**
   ```bash
   curl http://localhost:8080/diet-plans/health
   ```

   Expected response:
   ```json
   {
     "status": "UP",
     "service": "dietplan-service"
   }
   ```

## Testing the Service

### 1. Using Test Events

The service includes pre-configured test events in the `test-events/` directory. You can use these with tools like Postman or curl.

### 2. Sample API Calls

1. **Create a diet plan:**
   ```bash
   curl -X POST http://localhost:8080/diet-plans \
     -H "Content-Type: application/json" \
     -d '{
       "title": "Sample Diet Plan",
       "description": "A sample diet plan for testing",
       "patientId": 1,
       "dietitianId": 2,
       "dailyCalories": 2000,
       "status": "ACTIVE"
     }'
   ```

2. **Get all diet plans:**
   ```bash
   curl http://localhost:8080/diet-plans
   ```

3. **Get diet plans by dietitian:**
   ```bash
   curl http://localhost:8080/diet-plans/dietitian/2
   ```

## AWS Lambda Deployment

### 1. Build for Lambda

1. **Create the Lambda deployment package:**
   ```bash
   mvn clean package
   ```

   This creates `target/dietplan-service-lambda.jar`

### 2. AWS Lambda Configuration

1. **Create a new Lambda function:**
   - Runtime: Java 17
   - Handler: `com.example.dietplan.lambda.DietPlanLambdaHandler`
   - Upload the JAR file from `target/dietplan-service-lambda.jar`

2. **Set environment variables:**
   ```
   DB_URL=jdbc:mysql://your-rds-endpoint:3306/ddac_group18
   DB_USERNAME=your_database_username
   DB_PASSWORD=your_database_password
   USER_SERVICE_URL=https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user/users
   ```

3. **Configure Lambda settings:**
   - Memory: 512 MB (minimum recommended)
   - Timeout: 30 seconds
   - VPC: Configure if your RDS is in a VPC

### 3. API Gateway Integration

1. **Create API Gateway:**
   - Type: REST API
   - Integration: Lambda Function
   - Lambda Function: Your deployed dietplan-service function

2. **Configure routes:**
   - Create a `{proxy+}` resource
   - Enable CORS if needed
   - Deploy the API

## Docker Deployment (Optional)

### 1. Create Dockerfile

```dockerfile
FROM openjdk:17-jdk-slim

VOLUME /tmp

COPY target/dietplan-service-lambda.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
```

### 2. Build and Run Docker Container

```bash
# Build the image
docker build -t dietplan-service .

# Run the container
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/ddac_group18 \
  -e DB_USERNAME=dietplan_user \
  -e DB_PASSWORD=dietplan_password \
  -e USER_SERVICE_URL=https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user/users \
  dietplan-service
```

## Database Schema

The service automatically creates the following tables:

### diet_plans
```sql
CREATE TABLE diet_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    patient_id BIGINT NOT NULL,
    dietitian_id BIGINT NOT NULL,
    start_date DATE,
    end_date DATE,
    breakfast TEXT,
    lunch TEXT,
    dinner TEXT,
    snacks TEXT,
    daily_calories INT,
    special_instructions TEXT,
    dietary_restrictions TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### users (Reference table)
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);
```

## Troubleshooting

### Common Issues

1. **Connection refused errors:**
   - Verify MySQL is running
   - Check database credentials
   - Ensure the database exists

2. **Port already in use:**
   - Change the port in application properties:
     ```properties
     server.port=8081
     ```

3. **Lambda timeout errors:**
   - Increase Lambda timeout
   - Check database connectivity from Lambda
   - Verify VPC configuration

4. **Database connection issues in Lambda:**
   - Ensure RDS security group allows Lambda access
   - Check VPC configuration
   - Verify database endpoint and credentials

### Logging

Check application logs for detailed error information:

- **Local development:** Console output
- **Lambda:** CloudWatch Logs
- **Docker:** `docker logs container_name`

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_URL` | Database JDBC URL | `jdbc:mysql://localhost:3306/ddac_group18` |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | `password` |
| `USER_SERVICE_URL` | User Service API URL | `https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user/users` |
| `PORT` | Server port | `8080` |

## Health Monitoring

The service provides a health check endpoint:

```bash
GET /diet-plans/health
```

Response:
```json
{
  "status": "UP",
  "service": "dietplan-service"
}
```

Use this endpoint for:
- Load balancer health checks
- Monitoring systems
- Container orchestration health checks

## Performance Considerations

1. **Database Indexing:**
   - Add indexes on frequently queried columns (`patient_id`, `dietitian_id`, `status`)

2. **Connection Pooling:**
   - The service uses HikariCP for connection pooling
   - Configure pool size based on expected load

3. **Lambda Cold Starts:**
   - Consider using provisioned concurrency for consistent performance
   - Optimize Lambda memory allocation

## Security Best Practices

1. **Database Security:**
   - Use strong passwords
   - Enable SSL connections
   - Restrict database access to application only

2. **Lambda Security:**
   - Use IAM roles with minimal required permissions
   - Store sensitive configuration in AWS Secrets Manager
   - Enable VPC for database access

3. **API Security:**
   - Implement authentication/authorization
   - Use HTTPS in production
   - Validate all input parameters