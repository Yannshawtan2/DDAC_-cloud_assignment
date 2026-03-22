#!/bin/bash

# Event Ads Service API Test Script
# This script tests the event ads service endpoints

set -e

# Configuration
BASE_URL=${1:-"http://localhost:8082"}
SERVICE_NAME="Event Ads Service"

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
    "title": "Health Awareness Workshop",
    "content": "Join us for a comprehensive health awareness workshop covering nutrition, exercise, and preventive care.",
    "imageData": null
}'

UPDATE_DATA='{
    "title": "Updated Health Awareness Workshop",
    "content": "Join us for an updated comprehensive health awareness workshop covering nutrition, exercise, and preventive care. New topics added!",
    "imageData": null
}'

# Run tests
echo "1. Testing GET /event-ads (Get all event ads)"
test_endpoint "GET" "/event-ads" "" "Get all event ads"

echo "2. Testing POST /event-ads (Create event ad)"
test_endpoint "POST" "/event-ads" "$CREATE_DATA" "Create event ad"

echo "3. Testing GET /event-ads/1 (Get event ad by ID)"
test_endpoint "GET" "/event-ads/1" "" "Get event ad by ID"

echo "4. Testing PUT /event-ads/1 (Update event ad by ID)"
test_endpoint "PUT" "/event-ads/1" "$UPDATE_DATA" "Update event ad by ID"

echo "5. Testing DELETE /event-ads/1 (Delete event ad by ID)"
test_endpoint "DELETE" "/event-ads/1" "" "Delete event ad by ID"

echo ""
echo "🎉 All tests completed!"
echo "📋 Summary:"
echo "   - Service: $SERVICE_NAME"
echo "   - Base URL: $BASE_URL"
echo "   - Tested endpoints: 5" 