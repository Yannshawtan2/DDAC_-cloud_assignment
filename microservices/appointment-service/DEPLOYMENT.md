# Appointment Service Deployment Guide

This guide covers deploying the Appointment Service to AWS Lambda with API Gateway integration.

## Prerequisites

### Required Tools
- AWS CLI configured with appropriate permissions
- Maven 3.6+
- Java 21
- AWS SAM CLI (optional, for advanced deployment)

### AWS Permissions Required
- Lambda function creation and management
- API Gateway management
- IAM role creation
- RDS access (for database)
- CloudWatch Logs access

## Deployment Steps

### 1. Build the Application

```bash
cd microservices/appointment-service
mvn clean package
```

This creates two JAR files:
- `target/appointment-service-1.0.0.jar` (Spring Boot executable)
- `target/appointment-service-1.0.0-aws.jar` (Lambda-optimized)

### 2. Create IAM Role

```bash
# Create trust policy
cat > lambda-trust-policy.json << EOF
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
EOF

# Create IAM role
aws iam create-role \
    --role-name appointment-service-lambda-role \
    --assume-role-policy-document file://lambda-trust-policy.json

# Attach basic Lambda execution policy
aws iam attach-role-policy \
    --role-name appointment-service-lambda-role \
    --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

# Attach VPC execution policy (if using VPC)
aws iam attach-role-policy \
    --role-name appointment-service-lambda-role \
    --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole
```

### 3. Create Lambda Function

```bash
# Get the role ARN
ROLE_ARN=$(aws iam get-role --role-name appointment-service-lambda-role --query 'Role.Arn' --output text)

# Create Lambda function
aws lambda create-function \
    --function-name appointment-service \
    --runtime java21 \
    --role $ROLE_ARN \
    --handler com.example.appointmentservice.lambda.AppointmentLambdaHandler \
    --zip-file fileb://target/appointment-service-1.0.0-aws.jar \
    --timeout 30 \
    --memory-size 1024 \
    --description "Appointment Service Microservice"
```

### 4. Configure Environment Variables

```bash
aws lambda update-function-configuration \
    --function-name appointment-service \
    --environment Variables='{
        "DB_URL":"jdbc:mysql://your-rds-endpoint:3306/ddac_appointments",
        "DB_USERNAME":"your-db-username",
        "DB_PASSWORD":"your-db-password",
        "SPRING_PROFILES_ACTIVE":"prod"
    }'
```

### 5. Create API Gateway

#### Create REST API
```bash
# Create API
API_ID=$(aws apigateway create-rest-api \
    --name appointment-service-api \
    --description "Appointment Service API" \
    --query 'id' --output text)

echo "API ID: $API_ID"

# Get root resource ID
ROOT_RESOURCE_ID=$(aws apigateway get-resources \
    --rest-api-id $API_ID \
    --query 'items[0].id' --output text)
```

#### Create Resources and Methods
```bash
# Create /api resource
API_RESOURCE_ID=$(aws apigateway create-resource \
    --rest-api-id $API_ID \
    --parent-id $ROOT_RESOURCE_ID \
    --path-part api \
    --query 'id' --output text)

# Create /api/appointments resource
APPOINTMENTS_RESOURCE_ID=$(aws apigateway create-resource \
    --rest-api-id $API_ID \
    --parent-id $API_RESOURCE_ID \
    --path-part appointments \
    --query 'id' --output text)

# Create proxy resource for all sub-paths
PROXY_RESOURCE_ID=$(aws apigateway create-resource \
    --rest-api-id $API_ID \
    --parent-id $APPOINTMENTS_RESOURCE_ID \
    --path-part '{proxy+}' \
    --query 'id' --output text)

# Create ANY method for proxy resource
aws apigateway put-method \
    --rest-api-id $API_ID \
    --resource-id $PROXY_RESOURCE_ID \
    --http-method ANY \
    --authorization-type NONE

# Create OPTIONS method for CORS
aws apigateway put-method \
    --rest-api-id $API_ID \
    --resource-id $PROXY_RESOURCE_ID \
    --http-method OPTIONS \
    --authorization-type NONE
```

#### Configure Lambda Integration
```bash
# Get Lambda function ARN
LAMBDA_ARN=$(aws lambda get-function \
    --function-name appointment-service \
    --query 'Configuration.FunctionArn' --output text)

# Get AWS account ID and region
ACCOUNT_ID=$(aws sts get-caller-identity --query 'Account' --output text)
REGION=$(aws configure get region)

# Create Lambda integration for ANY method
aws apigateway put-integration \
    --rest-api-id $API_ID \
    --resource-id $PROXY_RESOURCE_ID \
    --http-method ANY \
    --type AWS_PROXY \
    --integration-http-method POST \
    --uri arn:aws:apigateway:$REGION:lambda:path/2015-03-31/functions/$LAMBDA_ARN/invocations

# Configure CORS for OPTIONS method
aws apigateway put-integration \
    --rest-api-id $API_ID \
    --resource-id $PROXY_RESOURCE_ID \
    --http-method OPTIONS \
    --type MOCK \
    --request-templates '{
        "application/json": "{\"statusCode\": 200}"
    }'

# Configure OPTIONS method response
aws apigateway put-method-response \
    --rest-api-id $API_ID \
    --resource-id $PROXY_RESOURCE_ID \
    --http-method OPTIONS \
    --status-code 200 \
    --response-parameters '{
        "method.response.header.Access-Control-Allow-Headers": false,
        "method.response.header.Access-Control-Allow-Methods": false,
        "method.response.header.Access-Control-Allow-Origin": false
    }'

# Configure OPTIONS integration response
aws apigateway put-integration-response \
    --rest-api-id $API_ID \
    --resource-id $PROXY_RESOURCE_ID \
    --http-method OPTIONS \
    --status-code 200 \
    --response-parameters '{
        "method.response.header.Access-Control-Allow-Headers": "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token'",
        "method.response.header.Access-Control-Allow-Methods": "'GET,POST,PUT,DELETE,OPTIONS'",
        "method.response.header.Access-Control-Allow-Origin": "'*'"
    }'
```

#### Grant API Gateway Permission to Invoke Lambda
```bash
aws lambda add-permission \
    --function-name appointment-service \
    --statement-id apigateway-invoke \
    --action lambda:InvokeFunction \
    --principal apigateway.amazonaws.com \
    --source-arn "arn:aws:execute-api:$REGION:$ACCOUNT_ID:$API_ID/*/*"
```

### 6. Deploy API

```bash
# Create deployment
aws apigateway create-deployment \
    --rest-api-id $API_ID \
    --stage-name prod \
    --description "Production deployment"

# Get API endpoint
API_ENDPOINT="https://$API_ID.execute-api.$REGION.amazonaws.com/prod"
echo "API Endpoint: $API_ENDPOINT"
```

### 7. Test Deployment

```bash
# Test health endpoint
curl "$API_ENDPOINT/api/appointments/health"

# Expected response:
# {"status":"UP","service":"appointment-service"}
```

## Database Setup

### RDS MySQL Instance

```bash
# Create DB subnet group
aws rds create-db-subnet-group \
    --db-subnet-group-name appointment-db-subnet-group \
    --db-subnet-group-description "Subnet group for appointment database" \
    --subnet-ids subnet-12345678 subnet-87654321

# Create security group
SECURITY_GROUP_ID=$(aws ec2 create-security-group \
    --group-name appointment-db-sg \
    --description "Security group for appointment database" \
    --vpc-id vpc-12345678 \
    --query 'GroupId' --output text)

# Allow MySQL access from Lambda
aws ec2 authorize-security-group-ingress \
    --group-id $SECURITY_GROUP_ID \
    --protocol tcp \
    --port 3306 \
    --source-group $LAMBDA_SECURITY_GROUP_ID

# Create RDS instance
aws rds create-db-instance \
    --db-instance-identifier appointment-db \
    --db-instance-class db.t3.micro \
    --engine mysql \
    --master-username admin \
    --master-user-password YourSecurePassword123! \
    --allocated-storage 20 \
    --db-name ddac_appointments \
    --vpc-security-group-ids $SECURITY_GROUP_ID \
    --db-subnet-group-name appointment-db-subnet-group \
    --backup-retention-period 7 \
    --storage-encrypted
```

## Environment-Specific Configurations

### Production Environment

```bash
# Update Lambda configuration for production
aws lambda update-function-configuration \
    --function-name appointment-service \
    --environment Variables='{
        "DB_URL":"jdbc:mysql://appointment-db.cluster-xyz.us-east-1.rds.amazonaws.com:3306/ddac_appointments",
        "DB_USERNAME":"admin",
        "DB_PASSWORD":"YourSecurePassword123!",
        "SPRING_PROFILES_ACTIVE":"prod",
        "LOGGING_LEVEL_ROOT":"WARN",
        "LOGGING_LEVEL_COM_EXAMPLE":"INFO"
    }' \
    --memory-size 1024 \
    --timeout 30
```

### Staging Environment

```bash
# Create staging version
aws lambda create-alias \
    --function-name appointment-service \
    --name staging \
    --function-version $LATEST

# Create staging API stage
aws apigateway create-deployment \
    --rest-api-id $API_ID \
    --stage-name staging \
    --description "Staging deployment"
```

## Monitoring and Logging

### CloudWatch Configuration

```bash
# Create log group
aws logs create-log-group \
    --log-group-name /aws/lambda/appointment-service

# Set retention policy
aws logs put-retention-policy \
    --log-group-name /aws/lambda/appointment-service \
    --retention-in-days 14
```

### CloudWatch Alarms

```bash
# Create error rate alarm
aws cloudwatch put-metric-alarm \
    --alarm-name "appointment-service-error-rate" \
    --alarm-description "High error rate for appointment service" \
    --metric-name Errors \
    --namespace AWS/Lambda \
    --statistic Sum \
    --period 300 \
    --threshold 10 \
    --comparison-operator GreaterThanThreshold \
    --dimensions Name=FunctionName,Value=appointment-service \
    --evaluation-periods 2

# Create duration alarm
aws cloudwatch put-metric-alarm \
    --alarm-name "appointment-service-duration" \
    --alarm-description "High duration for appointment service" \
    --metric-name Duration \
    --namespace AWS/Lambda \
    --statistic Average \
    --period 300 \
    --threshold 25000 \
    --comparison-operator GreaterThanThreshold \
    --dimensions Name=FunctionName,Value=appointment-service \
    --evaluation-periods 2
```

## Security Hardening

### Lambda Security

```bash
# Create custom policy for database access
cat > lambda-db-policy.json << EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "rds-db:connect"
            ],
            "Resource": [
                "arn:aws:rds-db:$REGION:$ACCOUNT_ID:dbuser:appointment-db/appointment_user"
            ]
        }
    ]
}
EOF

# Create and attach policy
aws iam create-policy \
    --policy-name appointment-service-db-policy \
    --policy-document file://lambda-db-policy.json

aws iam attach-role-policy \
    --role-name appointment-service-lambda-role \
    --policy-arn arn:aws:iam::$ACCOUNT_ID:policy/appointment-service-db-policy
```

### API Gateway Security

```bash
# Create API key
API_KEY_ID=$(aws apigateway create-api-key \
    --name appointment-service-key \
    --description "API key for appointment service" \
    --enabled \
    --query 'id' --output text)

# Create usage plan
USAGE_PLAN_ID=$(aws apigateway create-usage-plan \
    --name appointment-service-plan \
    --description "Usage plan for appointment service" \
    --throttle burstLimit=100,rateLimit=50 \
    --quota limit=10000,period=DAY \
    --api-stages apiId=$API_ID,stage=prod \
    --query 'id' --output text)

# Associate API key with usage plan
aws apigateway create-usage-plan-key \
    --usage-plan-id $USAGE_PLAN_ID \
    --key-id $API_KEY_ID \
    --key-type API_KEY
```

## Backup and Recovery

### Lambda Function Backup

```bash
# Download function code
aws lambda get-function \
    --function-name appointment-service \
    --query 'Code.Location' --output text | xargs wget -O appointment-service-backup.zip

# Export function configuration
aws lambda get-function-configuration \
    --function-name appointment-service > appointment-service-config.json
```

### Database Backup

```bash
# Create manual snapshot
aws rds create-db-snapshot \
    --db-instance-identifier appointment-db \
    --db-snapshot-identifier appointment-db-manual-snapshot-$(date +%Y%m%d%H%M%S)

# Enable automated backups
aws rds modify-db-instance \
    --db-instance-identifier appointment-db \
    --backup-retention-period 7 \
    --preferred-backup-window "03:00-04:00"
```

## Troubleshooting

### Common Deployment Issues

#### 1. Lambda Function Creation Fails
```bash
# Check IAM role exists and has correct trust policy
aws iam get-role --role-name appointment-service-lambda-role

# Verify JAR file exists and is not corrupted
ls -la target/appointment-service-1.0.0-aws.jar
```

#### 2. API Gateway Integration Issues
```bash
# Test Lambda function directly
aws lambda invoke \
    --function-name appointment-service \
    --payload '{}' \
    response.json

# Check API Gateway logs
aws logs filter-log-events \
    --log-group-name API-Gateway-Execution-Logs_$API_ID/prod
```

#### 3. Database Connection Issues
```bash
# Check Lambda logs
aws logs filter-log-events \
    --log-group-name /aws/lambda/appointment-service \
    --start-time $(date -d '1 hour ago' +%s)000

# Test database connectivity
aws rds describe-db-instances \
    --db-instance-identifier appointment-db \
    --query 'DBInstances[0].Endpoint'
```

### Performance Optimization

#### Lambda Optimization
```bash
# Increase memory for better performance
aws lambda update-function-configuration \
    --function-name appointment-service \
    --memory-size 1536

# Enable provisioned concurrency
aws lambda put-provisioned-concurrency-config \
    --function-name appointment-service \
    --qualifier $LATEST \
    --provisioned-concurrency-config ProvisionedConcurrencyConfigs=2
```

#### Database Optimization
```bash
# Upgrade instance class
aws rds modify-db-instance \
    --db-instance-identifier appointment-db \
    --db-instance-class db.t3.small \
    --apply-immediately

# Enable Performance Insights
aws rds modify-db-instance \
    --db-instance-identifier appointment-db \
    --enable-performance-insights \
    --performance-insights-retention-period 7
```

## Rollback Procedures

### Lambda Function Rollback
```bash
# List function versions
aws lambda list-versions-by-function --function-name appointment-service

# Update alias to previous version
aws lambda update-alias \
    --function-name appointment-service \
    --name prod \
    --function-version PREVIOUS_VERSION
```

### API Gateway Rollback
```bash
# List deployments
aws apigateway get-deployments --rest-api-id $API_ID

# Create new deployment with previous configuration
aws apigateway create-deployment \
    --rest-api-id $API_ID \
    --stage-name prod \
    --description "Rollback deployment"
```

## Maintenance

### Regular Updates
```bash
# Update Lambda function code
mvn clean package
aws lambda update-function-code \
    --function-name appointment-service \
    --zip-file fileb://target/appointment-service-1.0.0-aws.jar

# Update environment variables
aws lambda update-function-configuration \
    --function-name appointment-service \
    --environment Variables='{...}'
```

### Monitoring
```bash
# Check function metrics
aws cloudwatch get-metric-statistics \
    --namespace AWS/Lambda \
    --metric-name Invocations \
    --dimensions Name=FunctionName,Value=appointment-service \
    --start-time $(date -d '1 day ago' --iso-8601) \
    --end-time $(date --iso-8601) \
    --period 3600 \
    --statistics Sum
```

This deployment guide provides comprehensive instructions for deploying the Appointment Service to AWS Lambda with proper security, monitoring, and maintenance procedures.