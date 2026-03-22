# Template Service Setup Guide

## Quick Start

This template provides a standardized structure for creating new microservices in the DDac healthcare system.

## Step 1: Copy and Rename

```bash
# Copy the template
cp -r template-service your-service-name
cd your-service-name

# Example for patient service:
cp -r template-service patient-service
cd patient-service
```

## Step 2: Update Project Configuration

### 2.1 Update pom.xml
Replace the following placeholders:
- `{SERVICE_NAME}` → `patient` (lowercase)
- `{service-name}` → `patient-service`

### 2.2 Update Package Names
Rename all package directories and update imports:
```
com.example.template → com.example.patient
```

### 2.3 Update Class Names
Replace in all Java files:
- `Template` → `Patient` (or your entity name)
- `template` → `patient` (lowercase)

## Step 3: Configure Your Entity

### 3.1 Define Your Entity Model
Edit `src/main/java/com/example/{service}/model/{Entity}.java`:
- Add your entity fields
- Configure JPA annotations
- Add relationships if needed

### 3.2 Update DTOs
Edit files in `src/main/java/com/example/{service}/dto/`:
- `{Entity}Dto.java` - Response DTO
- `Create{Entity}Request.java` - Create request DTO
- `Update{Entity}Request.java` - Update request DTO

### 3.3 Configure Repository
Edit `src/main/java/com/example/{service}/repository/{Entity}Repository.java`:
- Add custom query methods if needed

### 3.4 Implement Service Logic
Edit `src/main/java/com/example/{service}/service/{Service}Service.java`:
- Implement business logic
- Add validation rules
- Handle exceptions

### 3.5 Update Lambda Handler
Edit `src/main/java/com/example/{service}/handler/{Service}Handler.java`:
- Update routing logic for your endpoints
- Configure request/response parsing

## Step 4: Database Configuration

### 4.1 Update application.properties
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:your_service_db}
spring.datasource.username=${DB_USERNAME:your_service_user}
spring.datasource.password=${DB_PASSWORD:password}

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.com.example.{service}=DEBUG
```

### 4.2 Create Database Schema
```sql
-- Create database
CREATE DATABASE {service_name}_db;

-- Create user
CREATE USER '{service_name}_user'@'%' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON {service_name}_db.* TO '{service_name}_user'@'%';
FLUSH PRIVILEGES;
```

## Step 5: Testing

### 5.1 Local Testing
```bash
# Build the project
mvn clean compile

# Run tests
mvn test

# Package for deployment
mvn clean package
```

### 5.2 Update Test Classes
Edit files in `src/test/java/com/example/{service}/`:
- Update test class names
- Add service-specific test cases
- Configure test data

## Step 6: Deployment

### 6.1 Deploy to AWS Lambda
```bash
# Package the application
mvn clean package

# Deploy using AWS CLI
aws lambda create-function \
    --function-name {service-name}-function \
    --runtime java21 \
    --role arn:aws:iam::your-account:role/lambda-execution-role \
    --handler com.example.{service}.handler.{Service}Handler \
    --zip-file fileb://target/{service-name}-lambda-1.0.0-aws.jar \
    --timeout 30 \
    --memory-size 512
```

### 6.2 Configure Environment Variables
Set in AWS Lambda console:
```
DB_HOST=your-rds-endpoint
DB_PORT=3306
DB_NAME={service_name}_db
DB_USERNAME={service_name}_user
DB_PASSWORD=secure_password
```

### 6.3 Create API Gateway
```bash
aws apigatewayv2 create-api \
    --name {service-name}-api \
    --protocol-type HTTP \
    --target arn:aws:lambda:region:account:{service-name}-function
```

## Step 7: Frontend Integration

### 7.1 Create Service Client
Copy and update `frontend-integration/ServiceClient.java` template:
- Update class name and methods
- Configure service URL
- Add specific request/response DTOs

### 7.2 Update Frontend Configuration
Add to `application.properties`:
```properties
microservices.{service_name}.url=https://your-api-gateway-url
```

## Step 8: Documentation

### 8.1 Update README
- Document your service's purpose
- List API endpoints
- Add example requests/responses

### 8.2 API Documentation
Document your endpoints:
```
GET    /api/{entities}           - Get all entities
GET    /api/{entities}/{id}      - Get entity by ID
POST   /api/{entities}           - Create new entity
PUT    /api/{entities}/{id}      - Update entity
DELETE /api/{entities}/{id}      - Delete entity
```

## Troubleshooting

### Common Issues

1. **Build Errors**: Check package names and imports
2. **Database Connection**: Verify environment variables
3. **Lambda Timeout**: Increase timeout in AWS console
4. **CORS Issues**: Check headers in handler response

### Getting Help

- Check the main migration guide: `MICROSERVICE_MIGRATION_TEMPLATE.md`
- Review the working user-service implementation
- Ask team members for assistance
- Check AWS CloudWatch logs for runtime errors

## Checklist

- [ ] Copied template and renamed directories
- [ ] Updated pom.xml with correct names
- [ ] Updated all package names and imports
- [ ] Defined entity model and relationships
- [ ] Created DTOs for requests/responses
- [ ] Implemented service business logic
- [ ] Updated Lambda handler routing
- [ ] Configured database connection
- [ ] Created database schema
- [ ] Tested locally
- [ ] Deployed to AWS Lambda
- [ ] Configured environment variables
- [ ] Created API Gateway
- [ ] Updated frontend integration
- [ ] Documented API endpoints
- [ ] Updated team README

## Next Steps

After completing setup:
1. Test all endpoints thoroughly
2. Add monitoring and logging
3. Implement error handling
4. Add security if needed
5. Performance optimization
6. Update team documentation
