@echo off
REM Deployment script for Patient Service
REM This script builds the project, runs tests, and packages it for deployment

echo ========================================
echo Patient Service Deployment Script
echo ========================================

REM Check if Maven is installed
mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven and add it to your PATH
    pause
    exit /b 1
)

echo Step 1: Cleaning previous builds...
mvn clean
if %errorlevel% neq 0 (
    echo ERROR: Failed to clean project
    pause
    exit /b 1
)

echo Step 2: Running tests...
mvn test
if %errorlevel% neq 0 (
    echo ERROR: Tests failed
    pause
    exit /b 1
)

echo Step 3: Compiling and packaging...
mvn package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Failed to package project
    pause
    exit /b 1
)

echo Step 4: Checking if JAR file was created...
if exist "target\patient-service-1.0.0.jar" (
    echo SUCCESS: JAR file created successfully
    echo Location: target\patient-service-1.0.0.jar
) else (
    echo ERROR: JAR file was not created
    pause
    exit /b 1
)

echo.
echo ========================================
echo Deployment completed successfully!
echo ========================================
echo.
echo Next steps:
echo 1. Configure your database connection in application.properties
echo 2. Deploy the JAR file to your target environment
echo 3. Set up environment variables for production
echo.
echo For AWS Lambda deployment:
echo 1. Upload the JAR file to AWS Lambda
echo 2. Set the handler to: com.example.patientservice.lambda.PatientLambdaHandler::handleRequest
echo 3. Configure environment variables and VPC settings
echo.

pause