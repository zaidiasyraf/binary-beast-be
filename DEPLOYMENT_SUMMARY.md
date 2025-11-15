# 🚀 Binary Beast Backend - AWS Deployment

## ✅ Deployment Ready!

Your Spring Boot AWS Bedrock application is now ready for deployment to AWS App Runner.

### 📁 Files Created:
- `Dockerfile` - Container configuration
- `apprunner.yaml` - App Runner deployment config
- `application-production.properties` - Production settings
- `deploy.sh` - Build and deployment script
- `test-api.sh` - API testing script
- `AWS_DEPLOYMENT_GUIDE.md` - Complete deployment guide
- `.github/workflows/build.yml` - CI/CD pipeline

### 🎯 Quick Deploy Steps:

1. **Build & Test Locally:**
   ```bash
   ./deploy.sh
   ```

2. **Push to GitHub:**
   ```bash
   git add .
   git commit -m "Add AWS App Runner deployment"
   git push origin main
   ```

3. **Deploy to AWS:**
   - Go to AWS Console → App Runner
   - Create service from GitHub
   - Use `apprunner.yaml` configuration
   - Add Bedrock IAM permissions

### 🌐 Your API Endpoints:
- **Chat**: `POST /api/bedrock/chat`
- **Health**: `GET /actuator/health`
- **Info**: `GET /actuator/info`

### 🔧 Configuration:
- **Runtime**: Java 17
- **Port**: 8080
- **Database**: PostgreSQL (already configured)
- **AWS Services**: Bedrock Claude 3 Haiku + Titan Embeddings

### 💰 Estimated Cost:
- **App Runner**: ~$20/month
- **Bedrock API**: Pay per request (~$0.00025/1K tokens)

### 🧪 Test After Deployment:
```bash
./test-api.sh https://your-app-url.awsapprunner.com
```

## 🚨 Important Notes:
1. Ensure AWS Bedrock access is enabled in your account
2. Add Bedrock IAM permissions to App Runner role
3. Database credentials are already configured
4. HTTPS is automatically enabled by App Runner

**Ready to deploy! 🎉**