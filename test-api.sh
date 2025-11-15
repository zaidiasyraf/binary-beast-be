#!/bin/bash

# Test script for deployed API
# Usage: ./test-api.sh https://your-app-runner-url.awsapprunner.com

if [ -z "$1" ]; then
    echo "Usage: $0 <app-runner-url>"
    echo "Example: $0 https://xxxxxxxxxx.us-east-1.awsapprunner.com"
    exit 1
fi

URL=$1

echo "🧪 Testing API at: $URL"
echo ""

# Test health endpoint
echo "1. Testing health endpoint..."
curl -s "$URL/actuator/health" | jq '.' || echo "Health check failed"
echo ""

# Test chat endpoint
echo "2. Testing chat endpoint..."
curl -X POST "$URL/api/bedrock/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello from AWS App Runner!"}' \
  -s | jq '.' || echo "Chat API failed"
echo ""

echo "✅ API testing complete!"