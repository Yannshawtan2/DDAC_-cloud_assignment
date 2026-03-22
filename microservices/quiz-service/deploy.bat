@echo off
REM Quiz Service Deployment Script for Windows
REM This script builds and deploys the Quiz microservice to AWS Lambda

echo === Quiz Service Deployment ===
echo Building and deploying Quiz microservice...

REM Check if AWS CLI is installed
aws --version >nul 2>&1
if errorlevel 1 (
    echo Error: AWS CLI is not installed. Please install AWS CLI first.
    exit /b 1
)

REM Check if Maven is installed
mvn --version >nul 2>&1
if errorlevel 1 (
    echo Error: Maven is not installed. Please install Maven first.
    exit /b 1
)

REM Set variables
set SERVICE_NAME=quiz-service
set FUNCTION_NAME=ddac-quiz-service
set REGION=us-east-1
set ROLE_ARN=arn:aws:iam::your-account:role/lambda-execution-role
set HANDLER=com.example.quiz.lambda.QuizLambdaHandler

REM Build the project
echo Building Maven project...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo Error: Maven build failed
    exit /b 1
)

REM Check if JAR file exists
set JAR_FILE=target\quiz-service-lambda.jar
if not exist "%JAR_FILE%" (
    echo Error: JAR file not found at %JAR_FILE%
    exit /b 1
)

echo JAR file created successfully: %JAR_FILE%

REM Deploy to AWS Lambda
echo Deploying to AWS Lambda...

REM Check if function exists
aws lambda get-function --function-name %FUNCTION_NAME% --region %REGION% >nul 2>&1
if errorlevel 1 (
    echo Function does not exist. Creating new function...
    aws lambda create-function ^
        --function-name %FUNCTION_NAME% ^
        --runtime java17 ^
        --role %ROLE_ARN% ^
        --handler %HANDLER% ^
        --zip-file fileb://%JAR_FILE% ^
        --timeout 30 ^
        --memory-size 512 ^
        --environment Variables="{\"SPRING_PROFILES_ACTIVE\":\"production\",\"SPRING_DATASOURCE_URL\":\"jdbc:mysql://your-db-host:3306/ddac_quiz_db\",\"SPRING_DATASOURCE_USERNAME\":\"your-db-username\",\"SPRING_DATASOURCE_PASSWORD\":\"your-db-password\",\"USER_SERVICE_URL\":\"https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user\"}" ^
        --region %REGION%
) else (
    echo Function exists. Updating function code...
    aws lambda update-function-code ^
        --function-name %FUNCTION_NAME% ^
        --zip-file fileb://%JAR_FILE% ^
        --region %REGION%
    
    echo Updating function configuration...
    aws lambda update-function-configuration ^
        --function-name %FUNCTION_NAME% ^
        --timeout 30 ^
        --memory-size 512 ^
        --environment Variables="{\"SPRING_PROFILES_ACTIVE\":\"production\",\"SPRING_DATASOURCE_URL\":\"jdbc:mysql://your-db-host:3306/ddac_quiz_db\",\"SPRING_DATASOURCE_USERNAME\":\"your-db-username\",\"SPRING_DATASOURCE_PASSWORD\":\"your-db-password\",\"USER_SERVICE_URL\":\"https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user\"}" ^
        --region %REGION%
)

if errorlevel 1 (
    echo Error: Deployment failed
    exit /b 1
)

echo Deployment completed successfully!

REM Test the deployment
echo Testing the deployment...
aws lambda invoke ^
    --function-name %FUNCTION_NAME% ^
    --payload file://test-events/get-questions-active.json ^
    --region %REGION% ^
    response.json

if exist response.json (
    echo Response:
    type response.json
    echo.
    del response.json
)

echo === Deployment Summary ===
echo Service: %SERVICE_NAME%
echo Function: %FUNCTION_NAME%
echo Region: %REGION%
echo Handler: %HANDLER%
echo JAR: %JAR_FILE%
echo Status: SUCCESS

echo Quiz Service deployment completed!
pause