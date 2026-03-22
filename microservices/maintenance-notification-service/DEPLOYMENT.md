# Template Service Deployment Guide

## Prerequisites

- AWS CLI configured
- Java 21 installed
- Maven installed
- AWS Lambda execution role created
- RDS MySQL database accessible

## Step-by-Step Deployment

### 1. Prepare Database

```sql
-- Connect to your RDS MySQL instance
-- Create database for the service
CREATE DATABASE template_db;

-- Create dedicated user
CREATE USER 'template_user'@'%' IDENTIFIED BY 'secure_password_here';
GRANT ALL PRIVILEGES ON template_db.* TO 'template_user'@'%';
FLUSH PRIVILEGES;

-- Verify tables are created (after first deployment)
USE template_db;
SHOW TABLES;
DESCRIBE template_entities;
```

### 2. Build the Application

```bash
# Navigate to your service directory
cd microservices/your-service-name

# Clean and build
mvn clean package

# Verify the JAR file is created
ls -la target/*-aws.jar
```

### 3. Deploy Lambda Function

```bash
# Create the Lambda function
aws lambda create-function \
    --function-name your-service-function \
    --runtime java21 \
    --role arn:aws:iam::YOUR_ACCOUNT_ID:role/lambda-execution-role \
    --handler com.example.yourservice.handler.YourServiceHandler \
    --zip-file fileb://target/your-service-lambda-1.0.0-aws.jar \
    --timeout 30 \
    --memory-size 512 \
    --environment Variables='{
        "DB_HOST":"your-rds-endpoint.region.rds.amazonaws.com",
        "DB_PORT":"3306",
        "DB_NAME":"your_service_db",
        "DB_USERNAME":"your_service_user",
        "DB_PASSWORD":"your_secure_password"
    }' \
    --region your-region
```

### 4. Create API Gateway

```bash
# Create HTTP API
aws apigatewayv2 create-api \
    --name your-service-api \
    --protocol-type HTTP \
    --target arn:aws:lambda:YOUR_REGION:YOUR_ACCOUNT_ID:function:your-service-function \
    --region your-region
```

### 5. Configure Lambda Permissions

```bash
# Add permission for API Gateway to invoke Lambda
aws lambda add-permission \
    --function-name your-service-function \
    --statement-id api-gateway-invoke \
    --action lambda:InvokeFunction \
    --principal apigateway.amazonaws.com \
    --source-arn "arn:aws:execute-api:YOUR_REGION:YOUR_ACCOUNT_ID:YOUR_API_ID/*/*" \
    --region your-region
```

### 6. Test the Deployment

```bash
# Get the API Gateway URL
aws apigatewayv2 get-apis --region your-region

# Test the health endpoint
curl -X GET "https://your-api-id.execute-api.your-region.amazonaws.com/api/templates"

# Test creating an entity
curl -X POST "https://your-api-id.execute-api.your-region.amazonaws.com/api/templates" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Entity",
    "description": "Test Description"
  }'
```

### 7. Update Frontend Configuration

Add the API Gateway URL to your frontend's `application.properties`:

```properties
microservices.yourservice.url=https://your-api-id.execute-api.your-region.amazonaws.com
```

## Environment Variables Reference

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_HOST` | RDS MySQL endpoint | `ddac-db.cluster-xyz.us-east-1.rds.amazonaws.com` |
| `DB_PORT` | Database port | `3306` |
| `DB_NAME` | Database name | `patient_db` |
| `DB_USERNAME` | Database username | `patient_user` |
| `DB_PASSWORD` | Database password | `secure_password_123` |

## Updating Existing Deployment

```bash
# Build updated code
mvn clean package

# Update Lambda function code
aws lambda update-function-code \
    --function-name your-service-function \
    --zip-file fileb://target/your-service-lambda-1.0.0-aws.jar \
    --region your-region

# Update environment variables if needed
aws lambda update-function-configuration \
    --function-name your-service-function \
    --environment Variables='{
        "DB_HOST":"new-endpoint.region.rds.amazonaws.com",
        "DB_PORT":"3306",
        "DB_NAME":"your_service_db",
        "DB_USERNAME":"your_service_user",
        "DB_PASSWORD":"new_password"
    }' \
    --region your-region
```

## Monitoring and Logs

```bash
# View Lambda logs
aws logs describe-log-groups --log-group-name-prefix "/aws/lambda/your-service-function"

# Stream logs in real-time
aws logs tail /aws/lambda/your-service-function --follow

# Check function metrics
aws cloudwatch get-metric-statistics \
    --namespace AWS/Lambda \
    --metric-name Duration \
    --dimensions Name=FunctionName,Value=your-service-function \
    --start-time 2025-01-01T00:00:00Z \
    --end-time 2025-01-02T00:00:00Z \
    --period 3600 \
    --statistics Average
```

## Troubleshooting

### Common Deployment Issues

1. **Lambda Timeout**
   ```bash
   aws lambda update-function-configuration \
       --function-name your-service-function \
       --timeout 60
   ```

2. **Memory Issues**
   ```bash
   aws lambda update-function-configuration \
       --function-name your-service-function \
       --memory-size 1024
   ```

3. **Database Connection Issues**
   - Check VPC configuration
   - Verify security groups
   - Test database connectivity

4. **Permission Issues**
   - Verify Lambda execution role has necessary permissions
   - Check API Gateway permissions

### Health Check Endpoints

Add these to your service for monitoring:

```java
// In your handler
if (path.equals("/api/health")) {
    return Map.of(
        "status", "healthy",
        "timestamp", Instant.now().toString(),
        "service", "your-service"
    );
}
```

## Rollback Procedure

```bash
# List function versions
aws lambda list-versions-by-function --function-name your-service-function

# Rollback to previous version
aws lambda update-alias \
    --function-name your-service-function \
    --name LIVE \
    --function-version previous-version-number
```

## Security Considerations

1. **Environment Variables**: Use AWS Systems Manager Parameter Store for sensitive data
2. **VPC**: Place Lambda in VPC if database is in private subnet
3. **IAM**: Use least privilege principle for Lambda execution role
4. **API Gateway**: Consider adding authentication/authorization
5. **Database**: Use encrypted connections and rotate passwords regularly

## Cost Optimization

1. **Memory**: Start with 512MB and adjust based on usage
2. **Timeout**: Set appropriate timeout to avoid unnecessary charges
3. **Provisioned Concurrency**: Only use if needed for consistent performance
4. **Dead Letter Queues**: Set up for error handling and retry logic

## Next Steps

1. Set up CI/CD pipeline for automated deployments
2. Configure monitoring and alerting
3. Implement logging strategy
4. Set up automated testing
5. Document API endpoints
6. Plan scaling strategy
