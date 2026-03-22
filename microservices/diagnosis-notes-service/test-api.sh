#!/bin/bash

# Diagnosis Notes Service API Test Script
# This script tests the diagnosis notes service endpoints

set -e

# Configuration
BASE_URL=${1:-"http://localhost:8081"}
SERVICE_NAME="Diagnosis Notes Service"

echo "🧪 Testing $SERVICE_NAME API at $BASE_URL"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test function
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo -n "Testing $description... "
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "\n%{http_code}" "$BASE_URL$endpoint")
    elif [ "$method" = "POST" ]; then
        response=$(curl -s -w "\n%{http_code}" -X POST -H "Content-Type: application/json" -d "$data" "$BASE_URL$endpoint")
    elif [ "$method" = "PUT" ]; then
        response=$(curl -s -w "\n%{http_code}" -X PUT -H "Content-Type: application/json" -d "$data" "$BASE_URL$endpoint")
    elif [ "$method" = "DELETE" ]; then
        response=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL$endpoint")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo -e "${GREEN}✅ PASS${NC} (HTTP $http_code)"
    else
        echo -e "${RED}❌ FAIL${NC} (HTTP $http_code)"
        echo "Response: $body"
    fi
}

# Test data
CREATE_DATA='{
    "appointmentId": 1,
    "name": "John Doe",
    "gender": "Male",
    "age": 35,
    "appointmentDate": "2024-01-15",
    "foreignId": "FD123456",
    "height": 175.5,
    "weight": 70.2,
    "bloodSugarIndex": 5.8,
    "diagnosisNote": "Patient shows symptoms of mild diabetes. Recommended lifestyle changes and monitoring."
}'

UPDATE_DATA='{
    "name": "John Doe Updated",
    "gender": "Male",
    "age": 36,
    "appointmentDate": "2024-01-15",
    "foreignId": "FD123456",
    "height": 175.5,
    "weight": 71.0,
    "bloodSugarIndex": 5.6,
    "diagnosisNote": "Patient shows improvement. Continue monitoring blood sugar levels."
}'

# Run tests
echo "1. Testing GET /diagnosis-notes (Get all diagnosis notes)"
test_endpoint "GET" "/diagnosis-notes" "" "Get all diagnosis notes"

echo "2. Testing POST /diagnosis-notes (Create diagnosis note)"
test_endpoint "POST" "/diagnosis-notes" "$CREATE_DATA" "Create diagnosis note"

echo "3. Testing GET /diagnosis-notes/1 (Get diagnosis note by ID)"
test_endpoint "GET" "/diagnosis-notes/1" "" "Get diagnosis note by ID"

echo "4. Testing GET /diagnosis-notes/appointment/1 (Get diagnosis note by appointment ID)"
test_endpoint "GET" "/diagnosis-notes/appointment/1" "" "Get diagnosis note by appointment ID"

echo "5. Testing PUT /diagnosis-notes/1 (Update diagnosis note by ID)"
test_endpoint "PUT" "/diagnosis-notes/1" "$UPDATE_DATA" "Update diagnosis note by ID"

echo "6. Testing PUT /diagnosis-notes/appointment/1 (Update diagnosis note by appointment ID)"
test_endpoint "PUT" "/diagnosis-notes/appointment/1" "$UPDATE_DATA" "Update diagnosis note by appointment ID"

echo "7. Testing DELETE /diagnosis-notes/1 (Delete diagnosis note by ID)"
test_endpoint "DELETE" "/diagnosis-notes/1" "" "Delete diagnosis note by ID"

echo "8. Testing DELETE /diagnosis-notes/appointment/1 (Delete diagnosis note by appointment ID)"
test_endpoint "DELETE" "/diagnosis-notes/appointment/1" "" "Delete diagnosis note by appointment ID"

echo ""
echo "🎉 All tests completed!"
echo "📋 Summary:"
echo "   - Service: $SERVICE_NAME"
echo "   - Base URL: $BASE_URL"
echo "   - Tested endpoints: 8" 