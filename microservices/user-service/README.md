# User Service Microservice

This is a Spring Boot microservice for user management that can be deployed as an AWS Lambda function.

## Architecture

The User Service follows a microservices architecture with:
- **Spring Boot** - Core framework
- **AWS Lambda** - Serverless compute
- **API Gateway** - HTTP API endpoint
- **RDS MySQL** - Database
- **Spring Security** - Authentication (simplified for cloud project)

## Project Structure

```
user-service/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/userservice/
│       │       ├── config/           # Configuration classes
│       │       ├── controller/       # REST controllers
│       │       ├── dto/             # Data Transfer Objects
│       │       ├── lambda/          # AWS Lambda handler
│       │       ├── model/           # Entity classes
│       │       ├── repository/      # Data access layer
│       │       ├── service/         # Business logic
│       │       └── UserServiceApplication.java
│       └── resources/
│           ├── application.properties
│           └── application-production.properties
├── pom.xml
├── template.yaml          # AWS SAM template
├── deploy.sh             # Deployment script (Linux/Mac)
└── deploy.bat            # Deployment script (Windows)
```

## API Endpoints

### User Management
- `POST /api/users` - Create a new user
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/email/{email}` - Get user by email
- `GET /api/users/role/{role}` - Get users by role
- `GET /api/users/search?name={name}&role={role}` - Search users
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `POST /api/users/validate-password` - Validate user password

### Health Check
- `GET /api/users/health` - Health check endpoint

## User Roles

The system supports the following roles:
- `PATIENT` - Default role for new registrations
- `DOCTOR` - Medical practitioners
- `DIETICIAN` - Nutrition specialists
- `ADMIN` - System administrators

## Local Development

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 (or H2 for testing)

### Running Locally

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd microservices/user-service
   ```

2. **Configure Database**
   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/user_service
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

3. **Build and Run**
   ```bash
   mvn clean package
   mvn spring-boot:run
   ```

4. **Test the API**
   ```bash
   curl http://localhost:8081/api/users/health
   ```

## AWS Deployment

### Prerequisites
- AWS CLI configured
- SAM CLI installed
- AWS account with appropriate permissions

### Deploy to AWS

1. **Set Environment Variables**
   ```bash
   export DB_PASSWORD="your_secure_password"
   ```

2. **Deploy using the script**
   ```bash
   # Linux/Mac
   ./deploy.sh dev us-east-1
   
   # Windows
   deploy.bat dev us-east-1
   ```

3. **Manual Deployment**
   ```bash
   # Build the application
   mvn clean package -DskipTests
   
   # Deploy with SAM
   sam build
   sam deploy \
     --template-file template.yaml \
     --stack-name user-service-dev \
     --capabilities CAPABILITY_IAM \
     --region us-east-1 \
     --parameter-overrides \
       Environment=dev \
       DBPassword=your_secure_password
   ```

## Integration with Main Application

The main application integrates with the User Service via the `UserServiceClient` class:

```java
@Component
public class UserServiceClient {
    @Value("${user-service.base-url}")
    private String userServiceBaseUrl;
    
    // Methods to call the microservice
}
```

Configure the microservice URL in your main application's `application.properties`:
```properties
# For local development
user-service.base-url=http://localhost:8081

# For AWS deployment
user-service.base-url=https://your-api-gateway-url.amazonaws.com/dev
```

## Security

The microservice uses Spring Security with:
- BCrypt password encoding
- Stateless session management
- Simple authentication (no JWT for this cloud project)
- CORS enabled for cross-origin requests

## Database Schema

The service uses a simple user table:
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);
```

## Monitoring and Logging

- CloudWatch logs for Lambda execution
- Application logs with different levels (DEBUG, INFO, WARN, ERROR)
- Health check endpoint for monitoring

## Error Handling

The API returns appropriate HTTP status codes:
- `200 OK` - Success
- `201 Created` - Resource created
- `400 Bad Request` - Invalid request
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## Testing

```bash
# Run unit tests
mvn test

# Test health endpoint
curl https://your-api-gateway-url.amazonaws.com/dev/api/users/health

# Test user creation
curl -X POST https://your-api-gateway-url.amazonaws.com/dev/api/users \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","name":"Test User","role":"PATIENT"}'
```

## Cost Optimization

This serverless architecture provides:
- **Pay-per-use pricing** - Only pay for actual requests
- **Automatic scaling** - Scales from 0 to handle traffic
- **No server management** - AWS handles infrastructure
- **Regional deployment** - Deploy close to users

## Troubleshooting

### Common Issues

1. **Cold Start Latency**
   - First request may be slower (~2-3 seconds)
   - Consider provisioned concurrency for production

2. **Database Connection**
   - Ensure Lambda has VPC access to RDS
   - Check security group rules

3. **Memory/Timeout**
   - Adjust Lambda memory and timeout settings
   - Monitor CloudWatch metrics

### Logs

Check CloudWatch logs for detailed error information:
```bash
aws logs describe-log-groups --log-group-name-prefix /aws/lambda/user-service
```

## Migration from Monolith

To migrate from the existing monolithic service:

1. **Deploy the microservice** alongside the monolith
2. **Update configuration** to point to the microservice
3. **Test thoroughly** with both local and AWS deployment
4. **Gradual rollout** by switching service calls
5. **Monitor** performance and error rates

## Future Enhancements

Possible improvements:
- JWT token authentication
- Redis caching layer
- Database connection pooling
- API versioning
- Rate limiting
- Metrics and monitoring dashboard
