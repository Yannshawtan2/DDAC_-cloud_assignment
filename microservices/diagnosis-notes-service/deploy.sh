#!/bin/bash

# Diagnosis Notes Service Deployment Script
# This script builds and deploys the diagnosis notes service to AWS

set -e

# Configuration
SERVICE_NAME="diagnosis-notes-service"
STACK_NAME="diagnosis-notes-service-stack"
ENVIRONMENT=${1:-dev}
REGION=${2:-us-east-1}

echo "🚀 Starting deployment of $SERVICE_NAME to $ENVIRONMENT environment in $REGION"

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "❌ AWS CLI is not installed. Please install it first."
    exit 1
fi

# Check if SAM CLI is installed
if ! command -v sam &> /dev/null; then
    echo "❌ AWS SAM CLI is not installed. Please install it first."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install it first."
    exit 1
fi

echo "📦 Building the application..."

# Clean and build the project
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✅ Build completed successfully"

# Check if JAR file exists
JAR_FILE="target/diagnosis-notes-service-lambda.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ JAR file not found at $JAR_FILE"
    exit 1
fi

echo "📁 JAR file found: $JAR_FILE"

# Deploy using SAM
echo "☁️  Deploying to AWS using SAM..."

sam deploy \
    --template-file template.yaml \
    --stack-name $STACK_NAME \
    --parameter-overrides \
        Environment=$ENVIRONMENT \
        DBUsername=admin \
        DBPassword=your-secure-password \
        DBName=ddac_group18 \
    --capabilities CAPABILITY_IAM \
    --region $REGION \
    --no-fail-on-empty-changeset

if [ $? -ne 0 ]; then
    echo "❌ Deployment failed!"
    exit 1
fi

echo "✅ Deployment completed successfully!"

# Get the API URL
API_URL=$(aws cloudformation describe-stacks \
    --stack-name $STACK_NAME \
    --region $REGION \
    --query 'Stacks[0].Outputs[?OutputKey==`DiagnosisNotesApiUrl`].OutputValue' \
    --output text)

echo "🌐 API URL: $API_URL"

# Test the deployment
echo "🧪 Testing the deployment..."
curl -s "$API_URL/diagnosis-notes" > /dev/null

if [ $? -eq 0 ]; then
    echo "✅ Service is responding correctly!"
else
    echo "⚠️  Service might not be ready yet. Please wait a few minutes and test manually."
fi

echo "🎉 Deployment completed!"
echo "📋 Summary:"
echo "   - Service: $SERVICE_NAME"
echo "   - Environment: $ENVIRONMENT"
echo "   - Region: $REGION"
echo "   - API URL: $API_URL"
echo "   - Stack Name: $STACK_NAME" 