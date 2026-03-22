#!/bin/bash

# User Service Deployment Script
# This script builds and deploys the User Service microservice to AWS Lambda

set -e

# Configuration
ENVIRONMENT=${1:-dev}
REGION=${2:-us-east-1}
STACK_NAME="user-service-${ENVIRONMENT}"
S3_BUCKET="user-service-deployments-${ENVIRONMENT}"

echo "=== User Service Deployment Script ==="
echo "Environment: ${ENVIRONMENT}"
echo "Region: ${REGION}"
echo "Stack Name: ${STACK_NAME}"
echo "S3 Bucket: ${S3_BUCKET}"

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "ERROR: AWS CLI is not installed. Please install it first."
    exit 1
fi

# Check if SAM CLI is installed
if ! command -v sam &> /dev/null; then
    echo "ERROR: SAM CLI is not installed. Please install it first."
    exit 1
fi

# Build the application
echo "Building the application..."
mvn clean package -DskipTests

# Create S3 bucket if it doesn't exist
echo "Creating S3 bucket if it doesn't exist..."
aws s3api create-bucket \
    --bucket ${S3_BUCKET} \
    --region ${REGION} \
    --create-bucket-configuration LocationConstraint=${REGION} \
    2>/dev/null || true

# Build SAM application
echo "Building SAM application..."
sam build

# Deploy the application
echo "Deploying the application..."
sam deploy \
    --template-file template.yaml \
    --stack-name ${STACK_NAME} \
    --capabilities CAPABILITY_IAM \
    --region ${REGION} \
    --s3-bucket ${S3_BUCKET} \
    --parameter-overrides \
        Environment=${ENVIRONMENT} \
        DBPassword=${DB_PASSWORD:-"TempPassword123!"}

# Get the API Gateway URL
API_URL=$(aws cloudformation describe-stacks \
    --stack-name ${STACK_NAME} \
    --region ${REGION} \
    --query "Stacks[0].Outputs[?OutputKey=='UserServiceApiUrl'].OutputValue" \
    --output text)

echo "=== Deployment Complete ==="
echo "API Gateway URL: ${API_URL}"
echo "You can test the API using: curl ${API_URL}api/users/health"

# Test the health endpoint
echo "Testing health endpoint..."
curl -s "${API_URL}api/users/health" | jq '.' || echo "Health check failed or jq not installed"

echo "=== Deployment script finished ==="
