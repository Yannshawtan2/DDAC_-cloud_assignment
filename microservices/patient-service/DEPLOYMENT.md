# Patient Service Deployment Guide

This guide provides step-by-step instructions for deploying the Patient Service microservice.

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher (for production)
- AWS CLI configured (for AWS Lambda deployment)
- AWS SAM CLI (optional, for local testing)

## Local Development Setup

### 1. Database Setup

```sql
-- Create database
CREATE DATABASE patient_service_db;

-- Create user (optional)
CREATE USER 'patient_service'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON patient_service_db.* TO 'patient_service'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Configuration

Update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/patient_service_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Build and Run

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the application
mvn package

# Run locally
java -jar target/patient-service-1.0.0.jar
```

The service will be available at `http://localhost:8080`

## AWS Lambda Deployment

### 1. Build for Lambda

```bash
# Package for Lambda deployment
mvn clean package
```

### 2. Create Lambda Function

#### Using AWS CLI:

```bash
# Create execution role (if not exists)
aws iam create-role --role-name patient-service-lambda-role \
  --assume-role-policy-document '{
    "Version": "2012-10-17",
    "Statement": [
      {
        "Effect": "Allow",
        "Principal": {
          "Service": "lambda.amazonaws.com"
        },
        "Action": "sts:AssumeRole"
      }
    ]
  }'

# Attach basic execution policy
aws iam attach-role-policy \
  --role-name patient-service-lambda-role \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

# Attach VPC execution policy (if using VPC)
aws iam attach-role-policy \
  --role-name patient-service-lambda-role \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole

# Create Lambda function
aws lambda create-function \
  --function-name patient-service \
  --runtime java11 \
  --role arn:aws:iam::YOUR_ACCOUNT_ID:role/patient-service-lambda-role \
  --handler com.example.patientservice.lambda.PatientLambdaHandler::handleRequest \
  --zip-file fileb://target/patient-service-1.0.0.jar \
  --timeout 30 \
  --memory-size 512
```

#### Using AWS Console:

1. Go to AWS Lambda Console
2. Click "Create function"
3. Choose "Author from scratch"
4. Function name: `patient-service`
5. Runtime: `Java 11`
6. Upload the JAR file from `target/patient-service-1.0.0.jar`
7. Set handler: `com.example.patientservice.lambda.PatientLambdaHandler::handleRequest`
8. Configure memory (512 MB recommended) and timeout (30 seconds)

### 3. Environment Variables

Set the following environment variables in Lambda:

```
SPRING_DATASOURCE_URL=jdbc:mysql://your-rds-endpoint:3306/patient_service_db
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_PROFILES_ACTIVE=production
```

### 4. VPC Configuration (if using RDS)

- Configure Lambda to run in the same VPC as your RDS instance
- Ensure security groups allow communication between Lambda and RDS
- Add NAT Gateway for internet access if needed

### 5. API Gateway Setup

1. Create a new API Gateway (REST API)
2. Create a proxy resource `{proxy+}`
3. Set integration type to "Lambda Function"
4. Enable "Use Lambda Proxy integration"
5. Select your Lambda function
6. Deploy the API

## Database Migration

### Production Database Setup

1. **Create RDS Instance:**
   - Engine: MySQL 8.0
   - Instance class: db.t3.micro (for development) or larger for production
   - Storage: 20 GB minimum
   - Enable automated backups

2. **Security Groups:**
   - Allow inbound traffic on port 3306 from Lambda security group
   - Restrict access to necessary sources only

3. **Database Initialization:**
   ```sql
   CREATE DATABASE patient_service_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

### Data Migration from Monolith

If migrating from an existing monolithic application:

1. **Export Patient Data:**
   ```sql
   -- Export patients from Users table where role = 'PATIENT'
   SELECT id, email, name, created_at, updated_at 
   FROM Users 
   WHERE role = 'PATIENT';
   
   -- Export patient health data
   SELECT * FROM PatientData;
   ```

2. **Import to New Schema:**
   ```sql
   -- Import patients
   INSERT INTO patients (id, email, name, created_at, updated_at)
   SELECT id, email, name, created_at, updated_at 
   FROM exported_users;
   
   -- Import health data
   INSERT INTO patient_health_data (
     patient_id, weight, weight_updated_at, height, height_updated_at,
     waist_circumference, waist_circumference_updated_at,
     blood_pressure, blood_pressure_updated_at,
     blood_glucose_level, blood_glucose_level_updated_at,
     created_at, updated_at
   )
   SELECT 
     user_id, weight, weight_updated_at, height, height_updated_at,
     waist_circumference, waist_circumference_updated_at,
     blood_pressure, blood_pressure_updated_at,
     blood_glucose_level, blood_glucose_level_updated_at,
     created_at, updated_at
   FROM exported_patient_data;
   ```

## Monitoring and Logging

### CloudWatch Logs

- Lambda logs are automatically sent to CloudWatch
- Log group: `/aws/lambda/patient-service`
- Configure log retention period (e.g., 30 days)

### CloudWatch Metrics

Monitor these key metrics:
- Invocation count
- Duration
- Error rate
- Throttles
- Database connection pool metrics

### Health Checks

The service includes health check endpoints:
- `GET /actuator/health` - Overall health status
- `GET /actuator/health/db` - Database connectivity

## Security Considerations

1. **Database Security:**
   - Use strong passwords
   - Enable SSL/TLS for database connections
   - Restrict database access to Lambda security group only

2. **Lambda Security:**
   - Use least privilege IAM roles
   - Enable VPC if accessing private resources
   - Encrypt environment variables

3. **API Security:**
   - Implement authentication/authorization
   - Use API Gateway throttling
   - Enable CORS only for trusted domains

## Performance Optimization

1. **Lambda Configuration:**
   - Memory: 512 MB (adjust based on load testing)
   - Timeout: 30 seconds
   - Reserved concurrency: Set based on expected load

2. **Database Optimization:**
   - Connection pooling (configured in application.properties)
   - Database indexing on frequently queried fields
   - Regular database maintenance

3. **Cold Start Optimization:**
   - Consider using provisioned concurrency for high-traffic endpoints
   - Optimize application startup time
   - Use GraalVM native image for faster startup (advanced)

## Troubleshooting

### Common Issues

1. **Database Connection Timeout:**
   - Check VPC configuration
   - Verify security group rules
   - Ensure RDS is in the same VPC as Lambda

2. **Lambda Timeout:**
   - Increase timeout setting
   - Optimize database queries
   - Check for connection pool exhaustion

3. **Memory Issues:**
   - Increase Lambda memory allocation
   - Monitor memory usage in CloudWatch
   - Optimize JVM heap settings

### Debugging

1. **Enable Debug Logging:**
   ```properties
   logging.level.com.example.patientservice=DEBUG
   logging.level.org.springframework.web=DEBUG
   ```

2. **Local Testing:**
   ```bash
   # Run with test profile
   java -jar target/patient-service-1.0.0.jar --spring.profiles.active=test
   ```

3. **Lambda Testing:**
   - Use AWS SAM CLI for local testing
   - Test with sample events
   - Monitor CloudWatch logs

## Rollback Procedure

1. **Lambda Rollback:**
   ```bash
   # List function versions
   aws lambda list-versions-by-function --function-name patient-service
   
   # Update alias to previous version
   aws lambda update-alias \
     --function-name patient-service \
     --name LIVE \
     --function-version PREVIOUS_VERSION
   ```

2. **Database Rollback:**
   - Restore from automated backup
   - Use point-in-time recovery if needed

## Support

For deployment issues:
1. Check CloudWatch logs
2. Verify configuration settings
3. Test database connectivity
4. Review security group settings
5. Contact the development team with specific error messages