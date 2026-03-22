# Treatment Plan Service Deployment Guide

## Prerequisites

- AWS CLI configured with appropriate permissions
- Maven 3.6+
- Java 17+
- MySQL database (RDS instance)
- AWS Lambda execution role
- API Gateway (optional, for HTTP endpoints)

## Database Setup

1. Ensure your MySQL database is running and accessible
2. Update the database configuration in `application-production.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://your-rds-endpoint:3306/ddac_group18
   spring.datasource.username=${DB_USERNAME}
   spring.datasource.password=${DB_PASSWORD}
   ```

3. The service will automatically create the `treatment_plans` table on first run

## Build

1. Build the project:
   ```bash
   mvn clean package
   ```

2. This creates `target/treatment-plan-service-lambda.jar` - the deployable Lambda package

## AWS Lambda Deployment

### Manual Deployment

1. Create a Lambda function in AWS Console:
   - Runtime: Java 17
   - Handler: `com.example.treatmentplan.lambda.TreatmentPlanLambdaHandler::handleRequest`
   - Memory: 1024 MB (minimum recommended)
   - Timeout: 30 seconds

2. Upload the JAR file:
   ```bash
   aws lambda update-function-code \
     --function-name treatment-plan-service \
     --zip-file fileb://target/treatment-plan-service-lambda.jar
   ```

3. Set environment variables:
   ```bash
   aws lambda update-function-configuration \
     --function-name treatment-plan-service \
     --environment Variables='{
       "DB_USERNAME":"your_db_username",
       "DB_PASSWORD":"your_db_password",
       "SPRING_PROFILES_ACTIVE":"production"
     }'
   ```

### Using AWS SAM (Recommended)

1. Install AWS SAM CLI
2. Create `template.yaml` (see below)
3. Deploy:
   ```bash
   sam build
   sam deploy --guided
   ```

## API Gateway Integration

If you want HTTP endpoints (recommended for REST API):

1. Create an API Gateway REST API
2. Create a proxy resource `{proxy+}`
3. Create an ANY method that integrates with your Lambda function
4. Enable CORS if needed
5. Deploy the API

Example integration:
- Method: ANY
- Resource: {proxy+}
- Integration Type: Lambda Function
- Lambda Function: treatment-plan-service
- Use Lambda Proxy Integration: Yes

## Environment Variables

Set the following environment variables in Lambda:

```bash
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
SPRING_PROFILES_ACTIVE=production
USER_SERVICE_URL=https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user
```

## Testing Deployment

1. Use the provided test events in `test-events/` directory
2. Test via AWS Lambda Console or AWS CLI:
   ```bash
   aws lambda invoke \
     --function-name treatment-plan-service \
     --payload file://test-events/health-check.json \
     response.json
   ```

3. If using API Gateway, test HTTP endpoints:
   ```bash
   curl https://your-api-gateway-url/treatment-plans/health
   ```

## Monitoring and Logging

1. CloudWatch Logs are automatically configured
2. Monitor Lambda metrics in CloudWatch
3. Set up alarms for:
   - Error rate
   - Duration
   - Memory usage
   - Invocation count

## Troubleshooting

### Common Issues

1. **Cold Start Timeouts**
   - Increase Lambda timeout to 30+ seconds
   - Consider provisioned concurrency for production

2. **Database Connection Issues**
   - Verify VPC configuration if RDS is in VPC
   - Check security group rules
   - Verify database credentials

3. **User Service Integration Failures**
   - Verify User Service URL is correct
   - Check network connectivity
   - Monitor external service timeouts

4. **Memory Issues**
   - Increase Lambda memory (affects CPU allocation)
   - Monitor CloudWatch metrics

### Logs Analysis

Check CloudWatch Logs for:
```
ERROR - User Service integration failures
ERROR - Database connection issues  
WARN - Validation failures
INFO - Request processing times
```

## Security Considerations

1. **IAM Roles**: Ensure Lambda execution role has minimal required permissions
2. **VPC**: Consider placing Lambda in VPC if database requires it
3. **Environment Variables**: Use AWS Systems Manager Parameter Store for sensitive data
4. **API Gateway**: Implement authentication/authorization if needed

## Performance Optimization

1. **Connection Pooling**: Configured in application properties
2. **JVM Tuning**: Consider custom runtime with tuned JVM settings
3. **Warm-up**: Implement keep-alive mechanism or provisioned concurrency
4. **Caching**: Consider implementing response caching for read operations

## Scaling Considerations

1. **Lambda Concurrency**: Set reserved concurrency based on database limits
2. **Database Connections**: Monitor connection pool usage
3. **API Rate Limits**: Implement throttling in API Gateway
4. **User Service Dependencies**: Monitor external service rate limits

## Cost Optimization

1. **Right-size Memory**: Start with 1024MB, adjust based on metrics
2. **Minimize Cold Starts**: Use provisioned concurrency sparingly
3. **Request Bundling**: Consider batching operations where possible
4. **Monitoring**: Set up billing alerts and cost monitoring