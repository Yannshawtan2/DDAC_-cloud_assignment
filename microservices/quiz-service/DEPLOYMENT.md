# Quiz Service Deployment Guide

## Overview
This guide provides instructions for deploying the Quiz Service microservice to AWS Lambda.

## Prerequisites
- AWS CLI configured with appropriate permissions
- Maven 3.6+ installed
- Java 21 installed
- Access to MySQL database (RDS recommended for production)

## Database Setup

### 1. Create Database Schema
```sql
CREATE DATABASE ddac_quiz_db;
USE ddac_quiz_db;

-- Tables will be auto-created by JPA/Hibernate
```

### 2. Database Configuration
Update the application-production.properties with your database credentials:
```properties
spring.datasource.url=jdbc:mysql://your-rds-endpoint:3306/ddac_quiz_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Build and Package

### 1. Build the Application
```bash
cd microservices/quiz-service
mvn clean package
```

This will create `quiz-service-lambda.jar` in the `target` directory.

### 2. Verify Build
Ensure the JAR file contains all dependencies:
```bash
ls -la target/quiz-service-lambda.jar
```

## AWS Lambda Deployment

### 1. Create Lambda Function
```bash
aws lambda create-function \
  --function-name quiz-service \
  --runtime java21 \
  --role arn:aws:iam::YOUR-ACCOUNT:role/lambda-execution-role \
  --handler com.example.quiz.lambda.QuizLambdaHandler::handleRequest \
  --zip-file fileb://target/quiz-service-lambda.jar \
  --timeout 30 \
  --memory-size 512 \
  --environment Variables='{
    "SPRING_PROFILES_ACTIVE":"production",
    "DB_URL":"jdbc:mysql://your-rds-endpoint:3306/ddac_quiz_db",
    "DB_USERNAME":"your_username",
    "DB_PASSWORD":"your_password"
  }'
```

### 2. Update Lambda Function (for subsequent deployments)
```bash
aws lambda update-function-code \
  --function-name quiz-service \
  --zip-file fileb://target/quiz-service-lambda.jar
```

### 3. Configure API Gateway
Create an API Gateway REST API and configure it to proxy all requests to the Lambda function:

1. Create a new REST API
2. Create a proxy resource `{proxy+}`
3. Create an `ANY` method on the proxy resource
4. Set the integration type to `Lambda Function Proxy`
5. Set the Lambda function to `quiz-service`
6. Deploy the API

## Environment Variables

Set the following environment variables in your Lambda function:

| Variable | Description | Example |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Spring profile | `production` |
| `DB_URL` | Database JDBC URL | `jdbc:mysql://rds-endpoint:3306/ddac_quiz_db` |
| `DB_USERNAME` | Database username | `quiz_user` |
| `DB_PASSWORD` | Database password | `secure_password` |

## IAM Permissions

The Lambda execution role needs the following permissions:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": "arn:aws:logs:*:*:*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "rds:DescribeDBInstances",
        "rds:DescribeDBClusters"
      ],
      "Resource": "*"
    }
  ]
}
```

## Testing the Deployment

### 1. Test Lambda Function Directly
Use the AWS CLI to test the function:
```bash
aws lambda invoke \
  --function-name quiz-service \
  --payload file://test-events/get-all-questions.json \
  response.json
```

### 2. Test via API Gateway
Use curl or Postman to test the API:
```bash
curl -X GET "https://your-api-id.execute-api.region.amazonaws.com/stage/quiz/questions"
```

## Monitoring and Logging

### CloudWatch Logs
Monitor the function logs in CloudWatch:
```bash
aws logs describe-log-groups --log-group-name-prefix /aws/lambda/quiz-service
```

### CloudWatch Metrics
Key metrics to monitor:
- Duration
- Error count
- Invocation count
- Throttles

## Troubleshooting

### Common Issues

1. **Cold Start Timeout**: Increase Lambda timeout and memory
2. **Database Connection Issues**: Check security groups and VPC configuration
3. **Memory Issues**: Increase Lambda memory allocation
4. **ClassPath Issues**: Verify all dependencies are included in the JAR

### Debug Mode
Enable debug logging by setting environment variable:
```
LOGGING_LEVEL_COM_EXAMPLE_QUIZ=DEBUG
```

## Production Considerations

1. **Database Connection Pooling**: Configure appropriate connection pool settings
2. **Error Handling**: Implement comprehensive error handling and retry logic
3. **Security**: Use AWS Secrets Manager for database credentials
4. **Monitoring**: Set up CloudWatch alarms for error rates and performance
5. **Backup**: Implement database backup strategy
6. **Scaling**: Configure appropriate Lambda concurrency limits

## Rollback Procedure

To rollback to a previous version:

1. List function versions:
```bash
aws lambda list-versions-by-function --function-name quiz-service
```

2. Update function configuration to use previous version:
```bash
aws lambda update-alias \
  --function-name quiz-service \
  --name LIVE \
  --function-version <previous-version>
```