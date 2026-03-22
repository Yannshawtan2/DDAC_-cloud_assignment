@echo off
REM Appointment Service Deployment Script
REM This script automates the deployment of the appointment service to AWS Lambda

setlocal enabledelayedexpansion

echo ========================================
echo Appointment Service Deployment Script
echo ========================================
echo.

REM Check if AWS CLI is installed
aws --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: AWS CLI is not installed or not in PATH
    echo Please install AWS CLI and configure it before running this script
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven before running this script
    pause
    exit /b 1
)

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 21 before running this script
    pause
    exit /b 1
)

echo All prerequisites are installed.
echo.

REM Configuration variables
set FUNCTION_NAME=appointment-service
set API_NAME=appointment-service-api
set ROLE_NAME=appointment-service-lambda-role
set REGION=us-east-1
set STAGE=prod

REM Get AWS account ID
echo Getting AWS account information...
for /f "tokens=*" %%i in ('aws sts get-caller-identity --query "Account" --output text 2^>nul') do set ACCOUNT_ID=%%i
if "!ACCOUNT_ID!"=="" (
    echo ERROR: Unable to get AWS account ID. Please check your AWS credentials.
    pause
    exit /b 1
)
echo AWS Account ID: !ACCOUNT_ID!
echo.

REM Step 1: Build the application
echo Step 1: Building the application...
echo =====================================
mvn clean package -q
if %errorlevel% neq 0 (
    echo ERROR: Maven build failed
    pause
    exit /b 1
)
echo Build completed successfully.
echo.

REM Check if JAR file exists
if not exist "target\%FUNCTION_NAME%-1.0.0-aws.jar" (
    echo ERROR: Lambda JAR file not found at target\%FUNCTION_NAME%-1.0.0-aws.jar
    pause
    exit /b 1
)

REM Step 2: Check/Create IAM Role
echo Step 2: Checking IAM Role...
echo ==============================
aws iam get-role --role-name %ROLE_NAME% >nul 2>&1
if %errorlevel% neq 0 (
    echo Creating IAM role %ROLE_NAME%...
    
    REM Create trust policy file
    echo {
    echo   "Version": "2012-10-17",
    echo   "Statement": [
    echo     {
    echo       "Effect": "Allow",
    echo       "Principal": {
    echo         "Service": "lambda.amazonaws.com"
    echo       },
    echo       "Action": "sts:AssumeRole"
    echo     }
    echo   ]
    echo } > trust-policy.json
    
    aws iam create-role --role-name %ROLE_NAME% --assume-role-policy-document file://trust-policy.json
    if %errorlevel% neq 0 (
        echo ERROR: Failed to create IAM role
        del trust-policy.json
        pause
        exit /b 1
    )
    
    REM Attach policies
    aws iam attach-role-policy --role-name %ROLE_NAME% --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
    aws iam attach-role-policy --role-name %ROLE_NAME% --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole
    
    del trust-policy.json
    echo IAM role created successfully.
    
    REM Wait for role to be available
    echo Waiting for IAM role to be available...
    timeout /t 10 /nobreak >nul
) else (
    echo IAM role %ROLE_NAME% already exists.
)
echo.

REM Get role ARN
for /f "tokens=*" %%i in ('aws iam get-role --role-name %ROLE_NAME% --query "Role.Arn" --output text') do set ROLE_ARN=%%i
echo Role ARN: !ROLE_ARN!
echo.

REM Step 3: Create/Update Lambda Function
echo Step 3: Creating/Updating Lambda Function...
echo =============================================
aws lambda get-function --function-name %FUNCTION_NAME% >nul 2>&1
if %errorlevel% neq 0 (
    echo Creating Lambda function %FUNCTION_NAME%...
    aws lambda create-function ^
        --function-name %FUNCTION_NAME% ^
        --runtime java21 ^
        --role !ROLE_ARN! ^
        --handler com.example.appointmentservice.lambda.AppointmentLambdaHandler ^
        --zip-file fileb://target/%FUNCTION_NAME%-1.0.0-aws.jar ^
        --timeout 30 ^
        --memory-size 1024 ^
        --description "Appointment Service Microservice"
    if %errorlevel% neq 0 (
        echo ERROR: Failed to create Lambda function
        pause
        exit /b 1
    )
    echo Lambda function created successfully.
) else (
    echo Updating Lambda function %FUNCTION_NAME%...
    aws lambda update-function-code ^
        --function-name %FUNCTION_NAME% ^
        --zip-file fileb://target/%FUNCTION_NAME%-1.0.0-aws.jar
    if %errorlevel% neq 0 (
        echo ERROR: Failed to update Lambda function
        pause
        exit /b 1
    )
    echo Lambda function updated successfully.
)
echo.

REM Step 4: Configure Environment Variables
echo Step 4: Configuring Environment Variables...
echo =============================================
set /p DB_URL="Enter Database URL (e.g., jdbc:mysql://your-rds-endpoint:3306/ddac_appointments): "
set /p DB_USERNAME="Enter Database Username: "
set /p DB_PASSWORD="Enter Database Password: "

aws lambda update-function-configuration ^
    --function-name %FUNCTION_NAME% ^
    --environment Variables="{\"SPRING_DATASOURCE_URL\":\"%DB_URL%\",\"SPRING_DATASOURCE_USERNAME\":\"%DB_USERNAME%\",\"SPRING_DATASOURCE_PASSWORD\":\"%DB_PASSWORD%\",\"SPRING_PROFILES_ACTIVE\":\"production\"}"
if %errorlevel% neq 0 (
    echo ERROR: Failed to update environment variables
    pause
    exit /b 1
)
echo Environment variables configured successfully.
echo.

REM Step 5: Check/Create API Gateway
echo Step 5: Checking API Gateway...
echo ===============================
for /f "tokens=*" %%i in ('aws apigateway get-rest-apis --query "items[?name==^'%API_NAME%^'].id" --output text 2^>nul') do set API_ID=%%i
if "!API_ID!"=="" (
    echo Creating API Gateway %API_NAME%...
    for /f "tokens=*" %%i in ('aws apigateway create-rest-api --name %API_NAME% --description "Appointment Service API" --query "id" --output text') do set API_ID=%%i
    if "!API_ID!"=="" (
        echo ERROR: Failed to create API Gateway
        pause
        exit /b 1
    )
    echo API Gateway created with ID: !API_ID!
    
    REM Get root resource ID
    for /f "tokens=*" %%i in ('aws apigateway get-resources --rest-api-id !API_ID! --query "items[0].id" --output text') do set ROOT_RESOURCE_ID=%%i
    
    REM Create /api resource
    for /f "tokens=*" %%i in ('aws apigateway create-resource --rest-api-id !API_ID! --parent-id !ROOT_RESOURCE_ID! --path-part api --query "id" --output text') do set API_RESOURCE_ID=%%i
    
    REM Create /api/appointments resource
    for /f "tokens=*" %%i in ('aws apigateway create-resource --rest-api-id !API_ID! --parent-id !API_RESOURCE_ID! --path-part appointments --query "id" --output text') do set APPOINTMENTS_RESOURCE_ID=%%i
    
    REM Create proxy resource
    for /f "tokens=*" %%i in ('aws apigateway create-resource --rest-api-id !API_ID! --parent-id !APPOINTMENTS_RESOURCE_ID! --path-part "{proxy+}" --query "id" --output text') do set PROXY_RESOURCE_ID=%%i
    
    REM Create ANY method
    aws apigateway put-method --rest-api-id !API_ID! --resource-id !PROXY_RESOURCE_ID! --http-method ANY --authorization-type NONE
    
    REM Create OPTIONS method for CORS
    aws apigateway put-method --rest-api-id !API_ID! --resource-id !PROXY_RESOURCE_ID! --http-method OPTIONS --authorization-type NONE
    
    REM Get Lambda function ARN
    for /f "tokens=*" %%i in ('aws lambda get-function --function-name %FUNCTION_NAME% --query "Configuration.FunctionArn" --output text') do set LAMBDA_ARN=%%i
    
    REM Create Lambda integration
    aws apigateway put-integration ^
        --rest-api-id !API_ID! ^
        --resource-id !PROXY_RESOURCE_ID! ^
        --http-method ANY ^
        --type AWS_PROXY ^
        --integration-http-method POST ^
        --uri arn:aws:apigateway:%REGION%:lambda:path/2015-03-31/functions/!LAMBDA_ARN!/invocations
    
    REM Configure CORS
    aws apigateway put-integration ^
        --rest-api-id !API_ID! ^
        --resource-id !PROXY_RESOURCE_ID! ^
        --http-method OPTIONS ^
        --type MOCK ^
        --request-templates "{\"application/json\": \"{\\\"statusCode\\\": 200}\"}"
    
    aws apigateway put-method-response ^
        --rest-api-id !API_ID! ^
        --resource-id !PROXY_RESOURCE_ID! ^
        --http-method OPTIONS ^
        --status-code 200 ^
        --response-parameters "{\"method.response.header.Access-Control-Allow-Headers\": false,\"method.response.header.Access-Control-Allow-Methods\": false,\"method.response.header.Access-Control-Allow-Origin\": false}"
    
    aws apigateway put-integration-response ^
        --rest-api-id !API_ID! ^
        --resource-id !PROXY_RESOURCE_ID! ^
        --http-method OPTIONS ^
        --status-code 200 ^
        --response-parameters "{\"method.response.header.Access-Control-Allow-Headers\": \"'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token'\",\"method.response.header.Access-Control-Allow-Methods\": \"'GET,POST,PUT,DELETE,OPTIONS'\",\"method.response.header.Access-Control-Allow-Origin\": \"'*'\"}"
    
    REM Grant API Gateway permission to invoke Lambda
    aws lambda add-permission ^
        --function-name %FUNCTION_NAME% ^
        --statement-id apigateway-invoke ^
        --action lambda:InvokeFunction ^
        --principal apigateway.amazonaws.com ^
        --source-arn "arn:aws:execute-api:%REGION%:!ACCOUNT_ID!:!API_ID!/*/*"
    
    echo API Gateway configured successfully.
) else (
    echo API Gateway %API_NAME% already exists with ID: !API_ID!
)
echo.

REM Step 6: Deploy API
echo Step 6: Deploying API...
echo ==========================
aws apigateway create-deployment ^
    --rest-api-id !API_ID! ^
    --stage-name %STAGE% ^
    --description "Deployment at %date% %time%"
if %errorlevel% neq 0 (
    echo ERROR: Failed to deploy API
    pause
    exit /b 1
)
echo API deployed successfully.
echo.

REM Step 7: Display Results
echo Step 7: Deployment Summary
echo ============================
set API_ENDPOINT=https://!API_ID!.execute-api.%REGION%.amazonaws.com/%STAGE%
echo.
echo Deployment completed successfully!
echo.
echo Function Name: %FUNCTION_NAME%
echo API Gateway ID: !API_ID!
echo API Endpoint: !API_ENDPOINT!
echo Health Check: !API_ENDPOINT!/api/appointments/health
echo.
echo You can now test your API using the following endpoints:
echo - GET  !API_ENDPOINT!/api/appointments/health
echo - GET  !API_ENDPOINT!/api/appointments/patient/{userId}
echo - POST !API_ENDPOINT!/api/appointments/patient
echo - GET  !API_ENDPOINT!/api/appointments/doctor/{doctorId}
echo - PUT  !API_ENDPOINT!/api/appointments/doctor/{id}/action
echo.

REM Step 8: Test Health Endpoint
echo Step 8: Testing Health Endpoint...
echo ==================================
echo Testing health endpoint...
curl -s "!API_ENDPOINT!/api/appointments/health" 2>nul
if %errorlevel% equ 0 (
    echo.
    echo Health check successful!
) else (
    echo.
    echo Health check failed. The service might need a few moments to warm up.
    echo You can manually test using: curl "!API_ENDPOINT!/api/appointments/health"
)
echo.

echo ========================================
echo Deployment script completed!
echo ========================================
echo.
echo Next steps:
echo 1. Configure your database connection
echo 2. Test the API endpoints
echo 3. Set up monitoring and alarms
echo 4. Configure custom domain (optional)
echo.
echo For troubleshooting, check:
echo - Lambda logs: aws logs tail /aws/lambda/%FUNCTION_NAME% --follow
echo - API Gateway logs in CloudWatch
echo.

pause
endlocal