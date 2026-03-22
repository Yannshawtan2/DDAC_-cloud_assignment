@echo off
REM Treatment Plan Service Deployment Script for Windows

echo Starting Treatment Plan Service deployment...

REM Configuration
set FUNCTION_NAME=treatment-plan-service
set REGION=us-east-1
set RUNTIME=java17
set HANDLER=com.example.treatmentplan.lambda.TreatmentPlanLambdaHandler::handleRequest
set MEMORY_SIZE=1024
set TIMEOUT=30
set ROLE_NAME=lambda-execution-role

REM Check if AWS CLI is installed
aws --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] AWS CLI is not installed. Please install it first.
    exit /b 1
)

REM Check if Maven is installed
mvn --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven is not installed. Please install it first.
    exit /b 1
)

REM Build the project
echo [INFO] Building the project...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo [ERROR] Build failed. Please check the error messages above.
    exit /b 1
)

echo [INFO] Build completed successfully.

REM Check if JAR file exists
set JAR_FILE=target\treatment-plan-service-lambda.jar
if not exist "%JAR_FILE%" (
    echo [ERROR] JAR file not found: %JAR_FILE%
    exit /b 1
)

echo [INFO] JAR file created: %JAR_FILE%

REM Get AWS account ID
for /f "tokens=*" %%i in ('aws sts get-caller-identity --query Account --output text') do set ACCOUNT_ID=%%i
if errorlevel 1 (
    echo [ERROR] Failed to get AWS account ID. Please check your AWS credentials.
    exit /b 1
)

echo [INFO] AWS Account ID: %ACCOUNT_ID%

REM Create IAM role if it doesn't exist
set ROLE_ARN=arn:aws:iam::%ACCOUNT_ID%:role/%ROLE_NAME%
aws iam get-role --role-name "%ROLE_NAME%" >nul 2>&1
if errorlevel 1 (
    echo [INFO] Creating IAM role: %ROLE_NAME%
    
    REM Create trust policy
    echo { > trust-policy.json
    echo   "Version": "2012-10-17", >> trust-policy.json
    echo   "Statement": [ >> trust-policy.json
    echo     { >> trust-policy.json
    echo       "Effect": "Allow", >> trust-policy.json
    echo       "Principal": { >> trust-policy.json
    echo         "Service": "lambda.amazonaws.com" >> trust-policy.json
    echo       }, >> trust-policy.json
    echo       "Action": "sts:AssumeRole" >> trust-policy.json
    echo     } >> trust-policy.json
    echo   ] >> trust-policy.json
    echo } >> trust-policy.json

    aws iam create-role --role-name "%ROLE_NAME%" --assume-role-policy-document file://trust-policy.json

    REM Attach basic execution policy
    aws iam attach-role-policy --role-name "%ROLE_NAME%" --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

    REM Attach VPC execution policy (if needed)
    aws iam attach-role-policy --role-name "%ROLE_NAME%" --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole

    echo [INFO] IAM role created successfully.
    
    REM Clean up temporary file
    del trust-policy.json
    
    REM Wait for role to be ready
    echo [INFO] Waiting for IAM role to be ready...
    timeout /t 10 /nobreak >nul
) else (
    echo [INFO] IAM role already exists: %ROLE_NAME%
)

REM Check if Lambda function exists
aws lambda get-function --function-name "%FUNCTION_NAME%" --region "%REGION%" >nul 2>&1
if errorlevel 1 (
    echo [INFO] Creating new Lambda function: %FUNCTION_NAME%
    
    REM Create new function
    aws lambda create-function ^
        --function-name "%FUNCTION_NAME%" ^
        --runtime "%RUNTIME%" ^
        --role "%ROLE_ARN%" ^
        --handler "%HANDLER%" ^
        --zip-file fileb://"%JAR_FILE%" ^
        --memory-size %MEMORY_SIZE% ^
        --timeout %TIMEOUT% ^
        --region "%REGION%" ^
        --environment Variables="{\"SPRING_PROFILES_ACTIVE\":\"production\",\"USER_SERVICE_URL\":\"https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user\"}" ^
        --description "Treatment Plan Management Microservice"
    
    if errorlevel 1 (
        echo [ERROR] Failed to create Lambda function.
        exit /b 1
    )
    
    echo [INFO] Lambda function created successfully.
) else (
    echo [INFO] Updating existing Lambda function: %FUNCTION_NAME%
    
    REM Update function code
    aws lambda update-function-code ^
        --function-name "%FUNCTION_NAME%" ^
        --zip-file fileb://"%JAR_FILE%" ^
        --region "%REGION%"
    
    if errorlevel 1 (
        echo [ERROR] Failed to update Lambda function code.
        exit /b 1
    )
    
    echo [INFO] Lambda function code updated successfully.
    
    REM Update function configuration
    aws lambda update-function-configuration ^
        --function-name "%FUNCTION_NAME%" ^
        --runtime "%RUNTIME%" ^
        --handler "%HANDLER%" ^
        --memory-size %MEMORY_SIZE% ^
        --timeout %TIMEOUT% ^
        --region "%REGION%" ^
        --environment Variables="{\"SPRING_PROFILES_ACTIVE\":\"production\",\"USER_SERVICE_URL\":\"https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user\"}"
    
    if errorlevel 1 (
        echo [ERROR] Failed to update Lambda function configuration.
        exit /b 1
    )
    
    echo [INFO] Lambda function configuration updated successfully.
)

REM Test the function
echo [INFO] Testing the Lambda function...
if exist "test-events\health-check.json" (
    aws lambda invoke ^
        --function-name "%FUNCTION_NAME%" ^
        --payload file://test-events/health-check.json ^
        --region "%REGION%" ^
        response.json
    
    if errorlevel 1 (
        echo [WARN] Lambda function test failed, but deployment was successful.
    ) else (
        echo [INFO] Lambda function test completed. Response:
        type response.json
        echo.
        del response.json
    )
) else (
    echo [WARN] Health check test event not found. Skipping function test.
)

echo [INFO] Deployment completed successfully!
echo [INFO] Function Name: %FUNCTION_NAME%
echo [INFO] Region: %REGION%
echo [INFO] Runtime: %RUNTIME%

echo.
echo [INFO] Next steps:
echo 1. Set up API Gateway to expose HTTP endpoints (optional)
echo 2. Configure database environment variables:
echo    - DB_USERNAME
echo    - DB_PASSWORD
echo 3. Test the service with the provided test events
echo 4. Monitor CloudWatch logs for any issues
echo.
echo [INFO] You can test individual endpoints using the test events in test-events/ directory

pause