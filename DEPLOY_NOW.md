# 🚀 Deploy Your App in 5 Minutes

## Prerequisites
- AWS Account with Bedrock access
- GitHub account (your repo: `zaidiasyraf/binary-beast-be`)

## Option 1: AWS App Runner (Easiest - No CLI needed)

1. **Push your code to GitHub**:
   ```bash
   git push origin master
   ```

2. **Go to AWS Console**:
   - Open [AWS App Runner](https://console.aws.amazon.com/apprunner)
   - Click "Create service"

3. **Configure Source**:
   - Repository type: "Source code repository"
   - Connect to GitHub
   - Repository: `zaidiasyraf/binary-beast-be`
   - Branch: `master`
   - Configuration: "Use configuration file" ✅

4. **Service Settings**:
   - Service name: `binary-beast-api`
   - Virtual CPU: 0.25 vCPU
   - Memory: 0.5 GB

5. **Environment Variables** (Add these):
   ```
   AWS_ACCESS_KEY_ID=your_access_key
   AWS_SECRET_ACCESS_KEY=your_secret_key
   AWS_REGION=us-east-1
   ```

6. **Deploy**: Click "Create & deploy"

## Option 2: AWS CLI (Advanced)

```bash
# Configure AWS CLI
aws configure

# Run deployment script
./deploy-apprunner.sh
```

## ✅ After Deployment

Your API will be available at:
```
https://xxxxxxxxxx.us-east-1.awsapprunner.com
```

### Test Endpoints:
- Health: `GET /actuator/health`
- Chat: `POST /api/bedrock/chat`

### Test with curl:
```bash
curl -X POST https://your-app-url.awsapprunner.com/api/bedrock/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello, how are you?"}'
```

## 🔧 Troubleshooting

1. **Build fails**: Check Java 17 is configured
2. **AWS access denied**: Verify Bedrock permissions
3. **Database connection**: Check RDS security groups
4. **Environment variables**: Ensure AWS credentials are set

## 📊 Monitoring

- App Runner Console: View logs and metrics
- CloudWatch: Detailed monitoring
- Health endpoint: `/actuator/health`