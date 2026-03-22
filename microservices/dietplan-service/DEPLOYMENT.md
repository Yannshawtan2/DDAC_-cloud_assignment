# Diet Plan Service Deployment Guide

This guide covers various deployment strategies for the Diet Plan Service.

## AWS Lambda Deployment

### Prerequisites

- AWS CLI configured with appropriate permissions
- AWS SAM CLI (optional, for advanced deployments)
- Java 17+
- Maven 3.6+

### Step 1: Build the Application

```bash
cd microservices/dietplan-service
mvn clean package
```

This creates `target/dietplan-service-lambda.jar` ready for Lambda deployment.

### Step 2: Create Lambda Function

#### Using AWS Console

1. **Navigate to AWS Lambda Console**
2. **Create Function**
   - Choose "Author from scratch"
   - Function name: `dietplan-service`
   - Runtime: Java 17
   - Architecture: x86_64

3. **Upload Code**
   - Upload `dietplan-service-lambda.jar`
   - Handler: `com.example.dietplan.lambda.DietPlanLambdaHandler`

4. **Configure Environment Variables**
   ```
   DB_URL=jdbc:mysql://your-rds-endpoint:3306/ddac_group18
   DB_USERNAME=your_db_username
   DB_PASSWORD=your_db_password
   ```

5. **Configure Memory and Timeout**
   - Memory: 1024 MB (recommended)
   - Timeout: 30 seconds

#### Using AWS CLI

```bash
# Create the function
aws lambda create-function \
  --function-name dietplan-service \
  --runtime java17 \
  --role arn:aws:iam::YOUR_ACCOUNT:role/lambda-execution-role \
  --handler com.example.dietplan.lambda.DietPlanLambdaHandler \
  --zip-file fileb://target/dietplan-service-lambda.jar \
  --timeout 30 \
  --memory-size 1024 \
  --environment Variables='{
    "DB_URL":"jdbc:mysql://your-rds-endpoint:3306/ddac_group18",
    "DB_USERNAME":"your_db_username",
    "DB_PASSWORD":"your_db_password"
  }'

# Update function code (for updates)
aws lambda update-function-code \
  --function-name dietplan-service \
  --zip-file fileb://target/dietplan-service-lambda.jar
```

### Step 3: API Gateway Integration

#### Create REST API

```bash
# Create API
aws apigateway create-rest-api --name dietplan-service-api

# Get API ID from the response
API_ID=your_api_id

# Get root resource ID
aws apigateway get-resources --rest-api-id $API_ID

# Create proxy resource
aws apigateway create-resource \
  --rest-api-id $API_ID \
  --parent-id ROOT_RESOURCE_ID \
  --path-part "{proxy+}"

# Create method
aws apigateway put-method \
  --rest-api-id $API_ID \
  --resource-id PROXY_RESOURCE_ID \
  --http-method ANY \
  --authorization-type NONE

# Create integration
aws apigateway put-integration \
  --rest-api-id $API_ID \
  --resource-id PROXY_RESOURCE_ID \
  --http-method ANY \
  --type AWS_PROXY \
  --integration-http-method POST \
  --uri arn:aws:apigateway:REGION:lambda:path/2015-03-31/functions/arn:aws:lambda:REGION:ACCOUNT:function:dietplan-service/invocations

# Deploy API
aws apigateway create-deployment \
  --rest-api-id $API_ID \
  --stage-name prod
```

### Step 4: Database Setup (RDS)

#### Create RDS MySQL Instance

```bash
aws rds create-db-instance \
  --db-instance-identifier dietplan-service-db \
  --db-instance-class db.t3.micro \
  --engine mysql \
  --engine-version 8.0.35 \
  --allocated-storage 20 \
  --db-name ddac_group18 \
  --master-username admin \
  --master-user-password your_secure_password \
  --vpc-security-group-ids sg-your-security-group \
  --backup-retention-period 7 \
  --storage-encrypted
```

#### Configure Security Group

```bash
# Create security group for RDS
aws ec2 create-security-group \
  --group-name dietplan-service-db-sg \
  --description "Security group for Diet Plan Service RDS"

# Allow Lambda access to RDS
aws ec2 authorize-security-group-ingress \
  --group-id sg-your-rds-sg \
  --protocol tcp \
  --port 3306 \
  --source-group sg-your-lambda-sg
```

## Docker Deployment

### Build Docker Image

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/dietplan-service-lambda.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=production

ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Build image
docker build -t dietplan-service:latest .

# Run container
docker run -d \
  --name dietplan-service \
  -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/ddac_group18 \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  dietplan-service:latest
```

### Docker Compose

```yaml
# docker-compose.yml
version: '3.8'

services:
  dietplan-service:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DB_URL=jdbc:mysql://mysql:3306/ddac_group18
      - DB_USERNAME=root
      - DB_PASSWORD=rootpassword
    depends_on:
      - mysql
    networks:
      - app-network

  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=rootpassword
      - MYSQL_DATABASE=ddac_group18
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - app-network

volumes:
  mysql_data:

networks:
  app-network:
    driver: bridge
```

```bash
# Deploy with Docker Compose
docker-compose up -d
```

## Kubernetes Deployment

### Deployment Configuration

```yaml
# k8s-deployment.yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: dietplan-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: dietplan-service
  template:
    metadata:
      labels:
        app: dietplan-service
    spec:
      containers:
      - name: dietplan-service
        image: dietplan-service:latest
        ports:
        - containerPort: 8080
        env:
        - name: DB_URL
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: url
        - name: DB_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: username
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        livenessProbe:
          httpGet:
            path: /diet-plans/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /diet-plans/health
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 10

---
apiVersion: v1
kind: Service
metadata:
  name: dietplan-service
spec:
  selector:
    app: dietplan-service
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer

---
apiVersion: v1
kind: Secret
metadata:
  name: db-secret
type: Opaque
data:
  url: <base64-encoded-db-url>
  username: <base64-encoded-username>
  password: <base64-encoded-password>
```

```bash
# Deploy to Kubernetes
kubectl apply -f k8s-deployment.yml
```

## Production Considerations

### Environment Variables

| Variable | Description | Required | Default |
|----------|-------------|----------|---------|
| `DB_URL` | Database JDBC URL | Yes | - |
| `DB_USERNAME` | Database username | Yes | - |
| `DB_PASSWORD` | Database password | Yes | - |
| `PORT` | Server port | No | 8080 |
| `SPRING_PROFILES_ACTIVE` | Spring profile | No | production |

### Security

#### IAM Role for Lambda

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
        "ec2:CreateNetworkInterface",
        "ec2:DescribeNetworkInterfaces",
        "ec2:DeleteNetworkInterface"
      ],
      "Resource": "*"
    }
  ]
}
```

#### RDS Security

- Enable encryption at rest
- Use SSL/TLS for connections
- Restrict access to Lambda security group only
- Regular security updates

### Monitoring

#### CloudWatch Alarms

```bash
# Lambda error rate alarm
aws cloudwatch put-metric-alarm \
  --alarm-name "DietPlanService-ErrorRate" \
  --alarm-description "Diet Plan Service error rate" \
  --metric-name Errors \
  --namespace AWS/Lambda \
  --statistic Sum \
  --period 300 \
  --threshold 5 \
  --comparison-operator GreaterThanThreshold \
  --dimensions Name=FunctionName,Value=dietplan-service

# Database connection alarm
aws cloudwatch put-metric-alarm \
  --alarm-name "DietPlanService-DBConnections" \
  --alarm-description "Diet Plan Service DB connections" \
  --metric-name DatabaseConnections \
  --namespace AWS/RDS \
  --statistic Average \
  --period 300 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold \
  --dimensions Name=DBInstanceIdentifier,Value=dietplan-service-db
```

#### Health Checks

```bash
# Application health check
curl https://your-api-gateway-url/diet-plans/health

# Database connectivity test
curl https://your-api-gateway-url/diet-plans
```

### Performance Optimization

#### Lambda Configuration

- **Memory**: Start with 1024 MB, adjust based on performance metrics
- **Timeout**: 30 seconds for most operations
- **Provisioned Concurrency**: For consistent performance
- **Environment Variables**: Use for configuration, not secrets

#### Database Optimization

```sql
-- Add indexes for frequently queried columns
CREATE INDEX idx_diet_plans_patient_id ON diet_plans(patient_id);
CREATE INDEX idx_diet_plans_dietitian_id ON diet_plans(dietitian_id);
CREATE INDEX idx_diet_plans_status ON diet_plans(status);
CREATE INDEX idx_diet_plans_created_at ON diet_plans(created_at);

-- Composite index for common queries
CREATE INDEX idx_diet_plans_dietitian_status ON diet_plans(dietitian_id, status);
CREATE INDEX idx_diet_plans_patient_status ON diet_plans(patient_id, status);
```

### Backup and Recovery

#### RDS Automated Backups

```bash
# Modify DB instance for automated backups
aws rds modify-db-instance \
  --db-instance-identifier dietplan-service-db \
  --backup-retention-period 7 \
  --preferred-backup-window "03:00-04:00" \
  --preferred-maintenance-window "Sun:04:00-Sun:05:00"
```

#### Manual Backup

```bash
# Create manual snapshot
aws rds create-db-snapshot \
  --db-instance-identifier dietplan-service-db \
  --db-snapshot-identifier dietplan-service-snapshot-$(date +%Y%m%d%H%M%S)
```

### Scaling

#### Auto Scaling (Lambda)

- Lambda automatically scales based on request volume
- Configure reserved concurrency if needed
- Monitor throttling metrics

#### Database Scaling

```bash
# Vertical scaling (increase instance size)
aws rds modify-db-instance \
  --db-instance-identifier dietplan-service-db \
  --db-instance-class db.t3.small \
  --apply-immediately

# Read replicas for read scaling
aws rds create-db-instance-read-replica \
  --db-instance-identifier dietplan-service-db-replica \
  --source-db-instance-identifier dietplan-service-db
```

### Cost Optimization

- Use appropriate Lambda memory allocation
- Monitor and optimize database instance size
- Implement connection pooling
- Use RDS Reserved Instances for predictable workloads
- Regular cleanup of old data if applicable

### Troubleshooting

#### Common Issues

1. **Lambda Timeout**
   - Increase timeout value
   - Optimize database queries
   - Check database connection pool settings

2. **Database Connection Issues**
   - Verify security group settings
   - Check VPC configuration
   - Validate connection string

3. **API Gateway 502 Errors**
   - Check Lambda function logs
   - Verify integration configuration
   - Check timeout settings

#### Logging

- **Lambda**: CloudWatch Logs automatically captures application logs
- **API Gateway**: Enable execution logging and access logging
- **RDS**: Enable general log and slow query log for debugging