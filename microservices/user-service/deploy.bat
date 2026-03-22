@echo off
REM User Service Deployment Script for Windows
REM This script builds and deploys the User Service microservice to AWS Lambda

setlocal enabledelayedexpansion

REM Configuration
set ENVIRONMENT=%1
if "%ENVIRONMENT%"=="" set ENVIRONMENT=dev

set REGION=%2
if "%REGION%"=="" set REGION=us-east-1

set STACK_NAME=user-service-%ENVIRONMENT%
set S3_BUCKET=user-service-deployments-%ENVIRONMENT%

echo === User Service Deployment Script ===
echo Environment: %ENVIRONMENT%
echo Region: %REGION%
echo Stack Name: %STACK_NAME%
echo S3 Bucket: %S3_BUCKET%

REM Check if AWS CLI is installed
aws --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: AWS CLI is not installed. Please install it first.
    exit /b 1
)

REM Check if SAM CLI is installed
sam --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: SAM CLI is not installed. Please install it first.
    exit /b 1
)

REM Build the application
echo Building the application...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo ERROR: Maven build failed
    exit /b 1
)

REM Create S3 bucket if it doesn't exist
echo Creating S3 bucket if it doesn't exist...
aws s3api create-bucket --bucket %S3_BUCKET% --region %REGION% --create-bucket-configuration LocationConstraint=%REGION% >nul 2>&1

REM Build SAM application
echo Building SAM application...
call sam build
if errorlevel 1 (
    echo ERROR: SAM build failed
    exit /b 1
)

REM Deploy the application
echo Deploying the application...
if "%DB_PASSWORD%"=="" set DB_PASSWORD=TempPassword123!

call sam deploy ^
    --template-file template.yaml ^
    --stack-name %STACK_NAME% ^
    --capabilities CAPABILITY_IAM ^
    --region %REGION% ^
    --s3-bucket %S3_BUCKET% ^
    --parameter-overrides Environment=%ENVIRONMENT% DBPassword=%DB_PASSWORD%

if errorlevel 1 (
    echo ERROR: SAM deployment failed
    exit /b 1
)

REM Get the API Gateway URL
for /f "tokens=*" %%i in ('aws cloudformation describe-stacks --stack-name %STACK_NAME% --region %REGION% --query "Stacks[0].Outputs[?OutputKey=='UserServiceApiUrl'].OutputValue" --output text') do set API_URL=%%i

echo === Deployment Complete ===
echo API Gateway URL: %API_URL%
echo You can test the API using: curl %API_URL%api/users/health

REM Test the health endpoint
echo Testing health endpoint...
curl -s "%API_URL%api/users/health"

echo === Deployment script finished ===
pause
