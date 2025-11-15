#!/bin/bash

echo "🚀 Building and preparing for AWS App Runner deployment..."

# Build the application
echo "📦 Building application..."
mvn clean package -DskipTests

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo ""
    echo "📋 Deployment files created:"
    echo "   - Dockerfile"
    echo "   - apprunner.yaml"
    echo "   - application-production.properties"
    echo ""
    echo "🌐 Next steps:"
    echo "1. Push code to GitHub repository"
    echo "2. Go to AWS Console → App Runner"
    echo "3. Create service from GitHub repository"
    echo "4. Use apprunner.yaml configuration"
    echo ""
    echo "🔗 Your API will be available at:"
    echo "   https://xxxxxxxxxx.us-east-1.awsapprunner.com/api/bedrock/chat"
    echo ""
    echo "🧪 Test endpoints:"
    echo "   Health: /actuator/health"
    echo "   Chat: /api/bedrock/chat"
else
    echo "❌ Build failed! Please check the errors above."
    exit 1
fi