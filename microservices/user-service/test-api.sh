#!/bin/bash

# Simple test script to verify User Service is working

set -e

BASE_URL=${1:-http://localhost:8081}

echo "=== Testing User Service at $BASE_URL ==="

# Test health endpoint
echo "Testing health endpoint..."
curl -s "$BASE_URL/api/users/health" | jq '.'

# Test creating a user
echo "Testing user creation..."
curl -s -X POST "$BASE_URL/api/users" \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","name":"Test User","role":"PATIENT"}' | jq '.'

# Test getting all users
echo "Testing get all users..."
curl -s "$BASE_URL/api/users" | jq '.'

# Test getting user by email
echo "Testing get user by email..."
curl -s "$BASE_URL/api/users/email/test@example.com" | jq '.'

echo "=== Test completed ==="
