# Maintenance Notification Service

This is a Spring Boot microservice for managing maintenance notifications in the DDac healthcare system. It can be deployed as an AWS Lambda function.

## Overview

The Maintenance Notification Service provides centralized management of system maintenance notifications with features for creating, updating, activating, and managing notification priorities.

## Features

- ✅ Spring Boot 3.5.3 with Java 21
- ✅ AWS Lambda integration
- ✅ JPA with MySQL database
- ✅ RESTful API endpoints
- ✅ Priority-based notification management
- ✅ Active/inactive notification states
- ✅ CORS configuration
- ✅ Error handling and validation
- ✅ Frontend integration client

## API Endpoints

```
GET    /api/notifications           - Get all notifications
GET    /api/notifications/{id}      - Get notification by ID
GET    /api/notifications/active    - Get all active notifications
GET    /api/notifications?priority={p} - Get notifications by priority
POST   /api/notifications           - Create new notification
POST   /api/notifications/{id}/activate   - Activate notification (deactivates others)
POST   /api/notifications/{id}/deactivate - Deactivate notification
PUT    /api/notifications/{id}      - Update notification
DELETE /api/notifications/{id}      - Delete notification
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/maintenancenotification/
│   │   ├── MaintenanceNotificationLambdaApplication.java  # Main application class
│   │   ├── dto/                                          # Data Transfer Objects
│   │   │   ├── MaintenanceNotificationDto.java
│   │   │   ├── CreateMaintenanceNotificationRequest.java
│   │   │   └── UpdateMaintenanceNotificationRequest.java
│   │   ├── handler/                                      # Lambda handlers
│   │   │   └── MaintenanceNotificationHandler.java
│   │   ├── model/                                        # JPA entities
│   │   │   └── MaintenanceNotification.java
│   │   ├── repository/                                   # Data access layer
│   │   │   └── MaintenanceNotificationRepository.java
│   │   └── service/                                      # Business logic layer
│   │       └── MaintenanceNotificationService.java
│   └── resources/
│       └── application.properties                        # Configuration
├── test/
│   └── java/com/example/maintenancenotification/
│       └── MaintenanceNotificationServiceTest.java      # Integration tests
└── frontend-integration/                                # Frontend integration files
    ├── MaintenanceNotificationServiceClient.java
    └── README.md
```

## Database Schema

```sql
CREATE TABLE maintenance_notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT FALSE
);
```

## Priority Levels

- `LOW` - Minor updates or information
- `MEDIUM` - Important notices
- `HIGH` - Critical updates requiring attention  
- `CRITICAL` - Emergency maintenance notifications

## Configuration

### Environment Variables (AWS Lambda)
```
DB_HOST=your-rds-endpoint
DB_PORT=3306
DB_NAME=maintenance_notification_db
DB_USERNAME=maintenance_user
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
    --function-name maintenance-notification-function \
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
