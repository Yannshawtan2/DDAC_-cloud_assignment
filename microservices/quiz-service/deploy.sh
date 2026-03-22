#!/bin/bash

# Quiz Service Deployment Script
# This script builds and deploys the Quiz microservice to AWS Lambda

set -e

echo "=== Quiz Service Deployment ==="
echo "Building and deploying Quiz microservice..."

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "Error: AWS CLI is not installed. Please install AWS CLI first."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed. Please install Maven first."
    exit 1
fi

# Set variables
SERVICE_NAME="quiz-service"
FUNCTION_NAME="ddac-quiz-service"
REGION="us-east-1"
ROLE_ARN="arn:aws:iam::your-account:role/lambda-execution-role"
HANDLER="com.example.quiz.lambda.QuizLambdaHandler"

# Build the project
echo "Building Maven project..."
mvn clean package -DskipTests

# Check if JAR file exists
JAR_FILE="target/quiz-service-lambda.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file not found at $JAR_FILE"
    exit 1
fi

echo "JAR file size: $(ls -lh $JAR_FILE | awk '{print $5}')"

# Deploy to AWS Lambda
echo "Deploying to AWS Lambda..."

# Check if function exists
if aws lambda get-function --function-name $FUNCTION_NAME --region $REGION &> /dev/null; then
    echo "Function exists. Updating function code..."
    aws lambda update-function-code \
        --function-name $FUNCTION_NAME \
        --zip-file fileb://$JAR_FILE \
        --region $REGION
    
    echo "Updating function configuration..."
    aws lambda update-function-configuration \
        --function-name $FUNCTION_NAME \
        --timeout 30 \
        --memory-size 512 \
        --environment Variables='{
            "SPRING_PROFILES_ACTIVE":"production",
            "SPRING_DATASOURCE_URL":"jdbc:mysql://your-db-host:3306/ddac_quiz_db",
            "SPRING_DATASOURCE_USERNAME":"your-db-username",
            "SPRING_DATASOURCE_PASSWORD":"your-db-password",
            "USER_SERVICE_URL":"https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user"
        }' \
        --region $REGION
else
    echo "Function does not exist. Creating new function..."
    aws lambda create-function \
        --function-name $FUNCTION_NAME \
        --runtime java17 \
        --role $ROLE_ARN \
        --handler $HANDLER \
        --zip-file fileb://$JAR_FILE \
        --timeout 30 \
        --memory-size 512 \
        --environment Variables='{
            "SPRING_PROFILES_ACTIVE":"production",
            "SPRING_DATASOURCE_URL":"jdbc:mysql://your-db-host:3306/ddac_quiz_db",
            "SPRING_DATASOURCE_USERNAME":"your-db-username",
            "SPRING_DATASOURCE_PASSWORD":"your-db-password",
            "USER_SERVICE_URL":"https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user"
        }' \
        --region $REGION
fi

echo "Deployment completed successfully!"

# Test the deployment
echo "Testing the deployment..."
aws lambda invoke \
    --function-name $FUNCTION_NAME \
    --payload file://test-events/get-questions-active.json \
    --region $REGION \
    response.json

echo "Response:"
cat response.json
echo ""

echo "=== Deployment Summary ==="
echo "Service: $SERVICE_NAME"
echo "Function: $FUNCTION_NAME"
echo "Region: $REGION"
echo "Handler: $HANDLER"
echo "JAR: $JAR_FILE"
echo "Status: SUCCESS"

# Clean up
rm -f response.json

echo "Quiz Service deployment completed!"