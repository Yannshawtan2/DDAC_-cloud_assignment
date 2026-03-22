#!/bin/bash

# Treatment Plan Service Deployment Script
echo "Starting Treatment Plan Service deployment..."

# Configuration
FUNCTION_NAME="treatment-plan-service"
REGION="us-east-1"
RUNTIME="java17"
HANDLER="com.example.treatmentplan.lambda.TreatmentPlanLambdaHandler::handleRequest"
MEMORY_SIZE=1024
TIMEOUT=30
ROLE_NAME="lambda-execution-role"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    print_error "AWS CLI is not installed. Please install it first."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed. Please install it first."
    exit 1
fi

# Build the project
print_status "Building the project..."
if mvn clean package -DskipTests; then
    print_status "Build completed successfully."
else
    print_error "Build failed. Please check the error messages above."
    exit 1
fi

# Check if JAR file exists
JAR_FILE="target/treatment-plan-service-lambda.jar"
if [ ! -f "$JAR_FILE" ]; then
    print_error "JAR file not found: $JAR_FILE"
    exit 1
fi

print_status "JAR file created: $JAR_FILE"

# Get AWS account ID
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
if [ $? -ne 0 ]; then
    print_error "Failed to get AWS account ID. Please check your AWS credentials."
    exit 1
fi

print_status "AWS Account ID: $ACCOUNT_ID"

# Create IAM role if it doesn't exist
ROLE_ARN="arn:aws:iam::$ACCOUNT_ID:role/$ROLE_NAME"
if ! aws iam get-role --role-name "$ROLE_NAME" &>/dev/null; then
    print_status "Creating IAM role: $ROLE_NAME"
    
    # Create trust policy
    cat > trust-policy.json << EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF

    aws iam create-role \
        --role-name "$ROLE_NAME" \
        --assume-role-policy-document file://trust-policy.json

    # Attach basic execution policy
    aws iam attach-role-policy \
        --role-name "$ROLE_NAME" \
        --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

    # Attach VPC execution policy (if needed)
    aws iam attach-role-policy \
        --role-name "$ROLE_NAME" \
        --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole

    print_status "IAM role created successfully."
    
    # Clean up temporary file
    rm -f trust-policy.json
    
    # Wait for role to be ready
    print_status "Waiting for IAM role to be ready..."
    sleep 10
else
    print_status "IAM role already exists: $ROLE_NAME"
fi

# Check if Lambda function exists
if aws lambda get-function --function-name "$FUNCTION_NAME" --region "$REGION" &>/dev/null; then
    print_status "Updating existing Lambda function: $FUNCTION_NAME"
    
    # Update function code
    aws lambda update-function-code \
        --function-name "$FUNCTION_NAME" \
        --zip-file fileb://"$JAR_FILE" \
        --region "$REGION"
    
    if [ $? -eq 0 ]; then
        print_status "Lambda function code updated successfully."
    else
        print_error "Failed to update Lambda function code."
        exit 1
    fi
    
    # Update function configuration
    aws lambda update-function-configuration \
        --function-name "$FUNCTION_NAME" \
        --runtime "$RUNTIME" \
        --handler "$HANDLER" \
        --memory-size $MEMORY_SIZE \
        --timeout $TIMEOUT \
        --region "$REGION" \
        --environment Variables='{
            "SPRING_PROFILES_ACTIVE":"production",
            "USER_SERVICE_URL":"https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user"
        }'
    
    if [ $? -eq 0 ]; then
        print_status "Lambda function configuration updated successfully."
    else
        print_error "Failed to update Lambda function configuration."
        exit 1
    fi
    
else
    print_status "Creating new Lambda function: $FUNCTION_NAME"
    
    # Create new function
    aws lambda create-function \
        --function-name "$FUNCTION_NAME" \
        --runtime "$RUNTIME" \
        --role "$ROLE_ARN" \
        --handler "$HANDLER" \
        --zip-file fileb://"$JAR_FILE" \
        --memory-size $MEMORY_SIZE \
        --timeout $TIMEOUT \
        --region "$REGION" \
        --environment Variables='{
            "SPRING_PROFILES_ACTIVE":"production",
            "USER_SERVICE_URL":"https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/user"
        }' \
        --description "Treatment Plan Management Microservice"
    
    if [ $? -eq 0 ]; then
        print_status "Lambda function created successfully."
    else
        print_error "Failed to create Lambda function."
        exit 1
    fi
fi

# Test the function
print_status "Testing the Lambda function..."
if [ -f "test-events/health-check.json" ]; then
    aws lambda invoke \
        --function-name "$FUNCTION_NAME" \
        --payload file://test-events/health-check.json \
        --region "$REGION" \
        response.json
    
    if [ $? -eq 0 ]; then
        print_status "Lambda function test completed. Response:"
        cat response.json
        echo ""
        rm -f response.json
    else
        print_warning "Lambda function test failed, but deployment was successful."
    fi
else
    print_warning "Health check test event not found. Skipping function test."
fi

print_status "Deployment completed successfully!"
print_status "Function Name: $FUNCTION_NAME"
print_status "Region: $REGION"
print_status "Runtime: $RUNTIME"

# Display next steps
echo ""
print_status "Next steps:"
echo "1. Set up API Gateway to expose HTTP endpoints (optional)"
echo "2. Configure database environment variables:"
echo "   - DB_USERNAME"
echo "   - DB_PASSWORD"
echo "3. Test the service with the provided test events"
echo "4. Monitor CloudWatch logs for any issues"
echo ""
print_status "You can test individual endpoints using the test events in test-events/ directory"