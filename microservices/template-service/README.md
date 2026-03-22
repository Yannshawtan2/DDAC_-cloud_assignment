# Template Service

This is a template microservice for the DDac healthcare system. Use this as a starting point for creating new microservices.

## Overview

This template provides a standardized structure for creating AWS Lambda microservices with Spring Boot, JPA, and MySQL.

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
- ✅ Unit and integration tests
- ✅ Frontend integration templates

## Quick Start

1. **Copy this template:**
   ```bash
   cp -r template-service your-service-name
   cd your-service-name
   ```

2. **Follow the setup guide:**
   Read `SETUP.md` for detailed instructions on customizing this template for your specific entity.

## API Endpoints

```
GET    /api/templates           - Get all entities
GET    /api/templates/{id}      - Get entity by ID
GET    /api/templates?name={n}  - Search entities by name
POST   /api/templates           - Create new entity
PUT    /api/templates/{id}      - Update entity
DELETE /api/templates/{id}      - Delete entity
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/template/
│   │   ├── TemplateLambdaApplication.java    # Main application class
│   │   ├── dto/                              # Data Transfer Objects
│   │   │   ├── TemplateEntityDto.java
│   │   │   ├── CreateTemplateEntityRequest.java
│   │   │   └── UpdateTemplateEntityRequest.java
│   │   ├── handler/                          # Lambda handlers
│   │   │   └── TemplateHandler.java
│   │   ├── model/                            # JPA entities
│   │   │   └── TemplateEntity.java
│   │   ├── repository/                       # Data access layer
│   │   │   └── TemplateEntityRepository.java
│   │   └── service/                          # Business logic layer
│   │       └── TemplateService.java
│   └── resources/
│       └── application.properties            # Configuration
├── test/
│   └── java/com/example/template/
│       └── TemplateServiceTest.java          # Integration tests
└── frontend-integration/                     # Frontend integration files
    ├── TemplateServiceClient.java
    └── README.md
```

## Database Schema

```sql
CREATE TABLE template_entities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);
```

## Configuration

### Environment Variables (AWS Lambda)
```
DB_HOST=your-rds-endpoint
DB_PORT=3306
DB_NAME=template_db
DB_USERNAME=template_user
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
    --function-name template-function \
    --runtime java21 \
    --role arn:aws:iam::your-account:role/lambda-execution-role \
    --handler com.example.template.handler.TemplateHandler \
    --zip-file fileb://target/template-lambda-1.0.0-aws.jar \
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

### Manual Testing
Use the test endpoints in your frontend application or tools like Postman to test the API.

## Frontend Integration

See `frontend-integration/README.md` for instructions on integrating this microservice with your frontend application.

## Customization Checklist

- [ ] Rename all `Template*` classes to your entity name
- [ ] Update package names from `com.example.template` to `com.example.{your-service}`
- [ ] Define your entity model in `TemplateEntity.java`
- [ ] Create your DTOs with appropriate fields
- [ ] Implement business logic in `TemplateService.java`
- [ ] Update API routes in `TemplateHandler.java`
- [ ] Configure database connection for your service
- [ ] Create database schema
- [ ] Update tests with your specific test cases
- [ ] Create frontend service client
- [ ] Deploy to AWS Lambda
- [ ] Update team documentation

## Common Patterns

### Entity with User Relationship
```java
@ManyToOne
@JoinColumn(name = "user_id")
private User user;
```

### Soft Delete
```java
@Column(name = "deleted_at")
private LocalDateTime deletedAt;

@PreRemove
public void preRemove() {
    this.deletedAt = LocalDateTime.now();
}
```

### Audit Fields
```java
@CreatedDate
@Column(name = "created_at", nullable = false, updatable = false)
private LocalDateTime createdAt;

@LastModifiedDate
@Column(name = "updated_at")
private LocalDateTime updatedAt;
```

## Troubleshooting

### Common Issues

1. **Build Errors**: Check package names and imports
2. **Database Connection**: Verify environment variables
3. **Lambda Timeout**: Increase timeout in AWS console
4. **CORS Issues**: Check headers in handler response

### Getting Help

- Check the main migration guide: `../../MICROSERVICE_MIGRATION_TEMPLATE.md`
- Review the working user-service implementation
- Ask team members for assistance
- Check AWS CloudWatch logs for runtime errors

## Contributing

1. Follow the established patterns in this template
2. Update documentation when making changes
3. Add tests for new functionality
4. Use pull requests for code review

## License

This template is part of the DDac healthcare system project.
