# 🚀 AWS App Runner Deployment Guide

## Quick Deploy (5 minutes)

### 1. Build Application
```bash
./deploy.sh
```

### 2. Push to GitHub
```bash
git add .
git commit -m "Add App Runner deployment files"
git push origin main
```

### 3. Create App Runner Service

1. **AWS Console** → Search "App Runner" → **Create service**

2. **Source Configuration:**
   - Source: **GitHub**
   - Repository: `your-username/binary-beast-be`
   - Branch: `main`
   - Deployment trigger: **Automatic**

3. **Build Configuration:**
   - Configuration source: **Use a configuration file**
   - Configuration file: `apprunner.yaml`

4. **Service Configuration:**
   - Service name: `binary-beast-api`
   - Virtual CPU: **0.25 vCPU**
   - Memory: **0.5 GB**

5. **Environment Variables:**
   ```
   AWS_REGION=us-east-1
   SPRING_PROFILES_ACTIVE=production
   ```

6. **Auto-scaling:**
   - Min instances: 1
   - Max instances: 25

7. **Health check:**
   - Path: `/actuator/health`

8. **Click "Create & Deploy"**

## 🌐 Your Endpoints

After deployment (2-5 minutes):
- **Base URL**: `https://xxxxxxxxxx.us-east-1.awsapprunner.com`
- **Chat API**: `https://xxxxxxxxxx.us-east-1.awsapprunner.com/api/bedrock/chat`
- **Health Check**: `https://xxxxxxxxxx.us-east-1.awsapprunner.com/actuator/health`

## 🧪 Test Your API

```bash
# Replace with your actual App Runner URL
URL="https://xxxxxxxxxx.us-east-1.awsapprunner.com"

# Test health
curl $URL/actuator/health

# Test chat
curl -X POST $URL/api/bedrock/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello from AWS!"}'
```

## 🔒 IAM Permissions

App Runner creates an IAM role automatically. Add Bedrock permissions:

1. **AWS Console** → **IAM** → **Roles**
2. Find `AppRunnerInstanceRole-xxxxx`
3. **Attach policies** → **Create policy**:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "bedrock:InvokeModel",
                "bedrock:InvokeModelWithResponseStream"
            ],
            "Resource": [
                "arn:aws:bedrock:us-east-1::foundation-model/anthropic.claude-3-haiku-20240307-v1:0",
                "arn:aws:bedrock:us-east-1::foundation-model/amazon.titan-embed-text-v1"
            ]
        }
    ]
}
```

## 💰 Cost Estimate
- **App Runner**: ~$20/month
- **Bedrock API**: ~$0.00025 per 1K tokens
- **Database**: Already running

## 🔄 Updates
Push to GitHub → App Runner auto-deploys (zero downtime)

## 🚨 Troubleshooting

**View Logs:**
1. App Runner Console → Your service → **Logs** tab

**Common Issues:**
- Build fails: Check `apprunner.yaml` syntax
- Health check fails: Ensure `/actuator/health` works locally
- Bedrock access: Verify IAM permissions
- Database connection: Check environment variables