# Quiz Service Deployment Guide

## Overview
This guide provides step-by-step instructions for deploying the Quiz Service microservice to AWS Lambda and integrating it with your main application.

## Prerequisites

### 1. Development Environment
- Java 17+
- Maven 3.6+
- AWS CLI configured with appropriate credentials
- MySQL database (AWS RDS recommended for production)

### 2. AWS Resources Required
- AWS Lambda function
- API Gateway (REST API)
- RDS MySQL database
- IAM role for Lambda execution
- VPC configuration (if using RDS in VPC)

### 3. Database Setup

#### Create Quiz Database
```sql
CREATE DATABASE ddac_quiz_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create tables (handled automatically by JPA DDL)
-- Tables created: quiz_questions, quiz_responses, quiz_answers
```

#### Environment Variables for Lambda
```bash
SPRING_PROFILES_ACTIVE=production
SPRING_DATASOURCE_URL=jdbc:mysql://your-rds-endpoint:3306/ddac_quiz_db
SPRING_DATASOURCE_USERNAME=your-db-username
SPRING_DATASOURCE_PASSWORD=your-db-password
USER_SERVICE_URL=https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user
```

## Deployment Steps

### Step 1: Build the Application

```bash
cd microservices/quiz-service
mvn clean package
```

**Expected Output:**
- `target/quiz-service-lambda.jar` (approximately 50-80MB)

### Step 2: Create IAM Role

Create an IAM role with the following policies:
- `AWSLambdaBasicExecutionRole`
- `AWSLambdaVPCAccessExecutionRole` (if RDS is in VPC)

**Trust Policy:**
```json
{
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
}
```

### Step 3: Deploy to Lambda

#### Option A: Using Deployment Script (Recommended)
```bash
# For Windows
deploy.bat

# For Linux/Mac
chmod +x deploy.sh
./deploy.sh
```

#### Option B: Manual Deployment
```bash
# Create Lambda function
aws lambda create-function \
    --function-name ddac-quiz-service \
    --runtime java17 \
    --role arn:aws:iam::YOUR-ACCOUNT:role/lambda-execution-role \
    --handler com.example.quiz.lambda.QuizLambdaHandler \
    --zip-file fileb://target/quiz-service-lambda.jar \
    --timeout 30 \
    --memory-size 512 \
    --environment Variables='{
        "SPRING_PROFILES_ACTIVE":"production",
        "SPRING_DATASOURCE_URL":"jdbc:mysql://your-rds-endpoint:3306/ddac_quiz_db",
        "SPRING_DATASOURCE_USERNAME":"your-username",
        "SPRING_DATASOURCE_PASSWORD":"your-password",
        "USER_SERVICE_URL":"https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user"
    }' \
    --region us-east-1
```

### Step 4: Configure API Gateway

#### Create REST API
1. **Create API**: `ddac-quiz-service-api`
2. **Create Resource**: `/quiz-service`
3. **Create Resource**: `/quiz` under `/quiz-service`
4. **Create Resource**: `{proxy+}` under `/quiz`
5. **Setup Method**: `ANY` on `{proxy+}`
6. **Integration Type**: Lambda Proxy
7. **Lambda Function**: `ddac-quiz-service`

#### Enable CORS
```json
{
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Headers": "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token",
    "Access-Control-Allow-Methods": "GET,POST,PUT,DELETE,OPTIONS"
}
```

#### Deploy API
1. **Create Deployment**: Stage name `prod`
2. **Note API Gateway URL**: `https://YOUR-API-ID.execute-api.us-east-1.amazonaws.com/prod/quiz-service/quiz`

### Step 5: Test Deployment

#### Health Check
```bash
curl https://YOUR-API-ID.execute-api.us-east-1.amazonaws.com/prod/quiz-service/quiz/health
```

**Expected Response:**
```json
{
    "status": "UP",
    "service": "quiz-service"
}
```

#### Create Test Question
```bash
curl -X POST https://YOUR-API-ID.execute-api.us-east-1.amazonaws.com/prod/quiz-service/quiz/questions \
  -H "Content-Type: application/json" \
  -d '{
    "questionText": "How often do you monitor your carbohydrate intake?",
    "description": "Rate consistency of carb tracking",
    "section": "Diet",
    "createdBy": "Diet@email.com",
    "isActive": true
  }'
```

#### Get Questions
```bash
curl https://YOUR-API-ID.execute-api.us-east-1.amazonaws.com/prod/quiz-service/quiz/questions?active=true
```

### Step 6: Update Main Application

#### Configure Properties
Update `src/main/resources/application-microservices.properties`:
```properties
quiz.service.base.url=https://YOUR-API-ID.execute-api.us-east-1.amazonaws.com/prod/quiz-service/quiz
app.microservices.enabled=true
```

#### Enable Microservices Mode
Start your main application with:
```bash
java -jar your-main-app.jar --spring.profiles.active=microservices
```

## Monitoring and Troubleshooting

### CloudWatch Logs
Monitor Lambda logs in CloudWatch:
- Log Group: `/aws/lambda/ddac-quiz-service`
- Look for Spring Boot startup logs
- Monitor for database connection issues

### Common Issues

#### 1. Database Connection Timeout
**Symptoms:** Lambda timeout, connection refused
**Solution:** 
- Ensure RDS security group allows Lambda IP ranges
- Configure VPC if RDS is in private subnet
- Increase Lambda timeout to 30 seconds

#### 2. Cold Start Performance
**Symptoms:** First request takes 10+ seconds
**Solution:**
- Increase memory to 1024MB for faster startup
- Consider provisioned concurrency for production

#### 3. User Service Integration Errors
**Symptoms:** 403 Forbidden when creating questions
**Solution:**
- Verify USER_SERVICE_URL is correct
- Test user service connectivity from Lambda
- Check user roles in user service

### Performance Optimization

#### Memory Configuration
- **Development**: 512MB
- **Production**: 1024MB (faster startup)

#### Database Connection Pool
```properties
# In application-production.properties
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=20000
```

## Security Considerations

### 1. Database Credentials
- Use AWS Secrets Manager for production
- Rotate credentials regularly
- Use IAM database authentication if possible

### 2. API Gateway Security
- Enable API keys for production
- Implement rate limiting
- Use AWS WAF for additional protection

### 3. VPC Configuration
- Place Lambda in same VPC as RDS
- Use private subnets for enhanced security
- Configure NAT Gateway for outbound internet access

## Scaling and Performance

### Auto Scaling
- Lambda automatically scales based on demand
- Monitor concurrent executions in CloudWatch
- Set reserved concurrency if needed

### Database Scaling
- Use RDS with read replicas for read-heavy workloads
- Monitor database CPU and connections
- Consider Aurora Serverless for variable workloads

## Backup and Disaster Recovery

### Database Backups
- Enable automated RDS backups
- Configure backup retention period
- Test restore procedures regularly

### Code Deployment
- Version Lambda functions
- Use aliases for blue-green deployments
- Maintain deployment artifacts in S3

## Cost Optimization

### Lambda Costs
- Monitor invocation count and duration
- Optimize memory allocation
- Use provisioned concurrency only if needed

### Database Costs
- Right-size RDS instance
- Use storage encryption
- Monitor storage growth

## Conclusion

The Quiz Service is now successfully deployed as a microservice with:
- ✅ Serverless architecture with AWS Lambda
- ✅ Integration with User Service API Gateway
- ✅ Role-based authorization
- ✅ Comprehensive quiz functionality
- ✅ Production-ready configuration
- ✅ Monitoring and logging

Your main application can now use the QuizServiceClient to interact with the microservice, providing better scalability and maintainability.