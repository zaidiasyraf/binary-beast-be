#!/bin/bash

echo "🚀 Deploying to AWS App Runner..."

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "❌ AWS CLI not found. Please install it first:"
    echo "   brew install awscli"
    exit 1
fi

# Check AWS credentials
if ! aws sts get-caller-identity &> /dev/null; then
    echo "❌ AWS credentials not configured. Run:"
    echo "   aws configure"
    exit 1
fi

# Build the application
echo "📦 Building application..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

# Create App Runner service
echo "🏗️ Creating App Runner service..."

SERVICE_NAME="binary-beast-api"
GITHUB_REPO="https://github.com/zaidiasyraf/binary-beast-be"

# Create the service
aws apprunner create-service \
    --service-name "$SERVICE_NAME" \
    --source-configuration '{
        "ImageRepository": {
            "ImageIdentifier": "public.ecr.aws/aws-containers/hello-app-runner:latest",
            "ImageConfiguration": {
                "Port": "8080"
            },
            "ImageRepositoryType": "ECR_PUBLIC"
        },
        "CodeRepository": {
            "RepositoryUrl": "'$GITHUB_REPO'",
            "SourceCodeVersion": {
                "Type": "BRANCH",
                "Value": "master"
            },
            "CodeConfiguration": {
                "ConfigurationSource": "CONFIGURATION_FILE"
            }
        }
    }' \
    --instance-configuration '{
        "Cpu": "0.25 vCPU",
        "Memory": "0.5 GB"
    }' \
    --region us-east-1

if [ $? -eq 0 ]; then
    echo "✅ App Runner service created successfully!"
    echo ""
    echo "🔗 Check deployment status:"
    echo "   aws apprunner describe-service --service-arn <service-arn> --region us-east-1"
    echo ""
    echo "🌐 Your API will be available at:"
    echo "   https://<random-id>.us-east-1.awsapprunner.com/api/bedrock/chat"
else
    echo "❌ Failed to create App Runner service"
    exit 1
fi