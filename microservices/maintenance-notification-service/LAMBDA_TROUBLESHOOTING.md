# Maintenance Notification Service Lambda Deployment Troubleshooting

## 🚨 **Common Issues and Solutions**

### 1. **Environment Variables Missing**
Your Lambda needs these environment variables set:
```
SPRING_DATASOURCE_URL=jdbc:mysql://your-database-host:3306/ddac_db
SPRING_DATASOURCE_USERNAME=your-username
SPRING_DATASOURCE_PASSWORD=your-password
```

### 2. **Lambda Configuration**
- **Handler**: `com.example.maintenancenotification.lambda.MaintenanceNotificationLambdaHandler::handleRequest`
- **Runtime**: Java 21
- **Memory**: At least 1024 MB (recommended 2048 MB)
- **Timeout**: At least 30 seconds

### 3. **API Gateway Integration**
Ensure your API Gateway is configured with:
- **Integration Type**: Lambda Proxy Integration
- **Resource Path**: `/{proxy+}`
- **Method**: ANY
- **Enable CORS**: Yes

### 4. **Test Events**

#### **GET All Notifications** (Use this corrected event):
```json
{
  "body": null,
  "resource": "/{proxy+}",
  "path": "/maintenance-notifications",
  "httpMethod": "GET",
  "isBase64Encoded": false,
  "queryStringParameters": null,
  "multiValueQueryStringParameters": null,
  "pathParameters": {
    "proxy": "maintenance-notifications"
  },
  "stageVariables": null,
  "headers": {
    "Accept": "application/json",
    "Content-Type": "application/json",
    "Host": "1234567890.execute-api.us-east-1.amazonaws.com"
  },
  "multiValueHeaders": {
    "Accept": ["application/json"],
    "Content-Type": ["application/json"],
    "Host": ["1234567890.execute-api.us-east-1.amazonaws.com"]
  },
  "requestContext": {
    "accountId": "123456789012",
    "resourceId": "123456",
    "stage": "prod",
    "requestId": "c6af9ac6-7b61-11e6-9a41-93e8deadbeef",
    "requestTime": "31/Jul/2025:12:34:56 +0000",
    "requestTimeEpoch": 1722427496000,
    "identity": {
      "sourceIp": "127.0.0.1",
      "userAgent": "Custom User Agent String"
    },
    "path": "/prod/maintenance-notifications",
    "resourcePath": "/{proxy+}",
    "httpMethod": "GET",
    "apiId": "1234567890",
    "protocol": "HTTP/1.1"
  }
}
```

#### **POST Create Notification**:
```json
{
  "body": "{\"title\":\"Test Maintenance\",\"message\":\"This is a test maintenance notification\",\"priority\":\"HIGH\",\"scheduledStartTime\":\"2025-08-01T10:00:00Z\",\"scheduledEndTime\":\"2025-08-01T12:00:00Z\"}",
  "resource": "/{proxy+}",
  "path": "/maintenance-notifications",
  "httpMethod": "POST",
  "isBase64Encoded": false,
  "queryStringParameters": null,
  "multiValueQueryStringParameters": null,
  "pathParameters": {
    "proxy": "maintenance-notifications"
  },
  "stageVariables": null,
  "headers": {
    "Accept": "application/json",
    "Content-Type": "application/json",
    "Host": "1234567890.execute-api.us-east-1.amazonaws.com"
  },
  "multiValueHeaders": {
    "Accept": ["application/json"],
    "Content-Type": ["application/json"],
    "Host": ["1234567890.execute-api.us-east-1.amazonaws.com"]
  },
  "requestContext": {
    "accountId": "123456789012",
    "resourceId": "123456",
    "stage": "prod",
    "requestId": "c6af9ac6-7b61-11e6-9a41-93e8deadbeef",
    "requestTime": "31/Jul/2025:12:34:56 +0000",
    "requestTimeEpoch": 1722427496000,
    "identity": {
      "sourceIp": "127.0.0.1",
      "userAgent": "Custom User Agent String"
    },
    "path": "/prod/maintenance-notifications",
    "resourcePath": "/{proxy+}",
    "httpMethod": "POST",
    "apiId": "1234567890",
    "protocol": "HTTP/1.1"
  }
}
```

### 5. **Common Error Solutions**

#### **Error: "Internal Server Error" or "Task timed out"**
- Increase Lambda timeout to at least 30 seconds
- Increase memory to 2048 MB
- Check CloudWatch logs for detailed errors

#### **Error: Database connection issues**
- Ensure RDS is in the same VPC as Lambda (if using VPC)
- Check security groups allow MySQL traffic (port 3306)
- Verify environment variables are set correctly

#### **Error: "502 Bad Gateway"**
- Check Lambda function logs in CloudWatch
- Verify handler path is correct
- Ensure JAR file was uploaded properly

#### **Error: Method not allowed**
- Verify API Gateway has `{proxy+}` resource
- Ensure method is set to `ANY`
- Check resource path matches your endpoint

### 6. **Debugging Steps**

1. **Check CloudWatch Logs**: Go to CloudWatch → Log Groups → `/aws/lambda/your-function-name`
2. **Test Lambda Directly**: Use the test events above in Lambda console
3. **Verify Environment Variables**: Check Lambda configuration → Environment variables
4. **Check VPC Configuration**: If using VPC, ensure NAT Gateway/Internet Gateway is configured

### 7. **JAR File Verification**
The correct JAR to upload: `maintenance-notification-service-lambda.jar` (54.6 MB)

### 8. **Expected Response Format**
A successful GET request should return:
```json
{
  "statusCode": 200,
  "headers": {
    "Content-Type": "application/json"
  },
  "body": "[]"  // Empty array if no notifications exist
}
```
