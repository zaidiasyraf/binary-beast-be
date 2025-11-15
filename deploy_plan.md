# AWS Deployment Plan - Easiest Method

## 🚀 AWS App Runner (Recommended - Easiest)

The **absolute easiest** way to deploy your Spring Boot application to AWS with public HTTPS endpoint.

### Why App Runner?
- ✅ Zero infrastructure management
- ✅ Automatic HTTPS endpoint
- ✅ Auto-scaling built-in
- ✅ Deploy directly from GitHub
- ✅ Pay only for what you use
- ✅ No Docker knowledge required

### Prerequisites
- GitHub repository with your code
- AWS account with Bedrock access
- 5 minutes of your time

### Step 1: Prepare Your Repository

1. **Push your code to GitHub** (if not already done)
2. **Create Dockerfile** in project root:

```dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

3. **Update application.properties** for production:

```properties
# Use port 8080 for App Runner
server.port=8080

# Health check endpoint
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always

# Your existing config...
aws.region=us-east-1
aws.bedrock.model-id=anthropic.claude-3-haiku-20240307-v1:0
```

### Step 2: Create App Runner Service

1. **Go to AWS Console** → Search "App Runner"
2. **Click "Create service"**
3. **Configure Source:**
   - Source: GitHub
   - Connect to GitHub (authorize AWS)
   - Repository: `your-username/binary-beast-be`
   - Branch: `main`
   - Deployment trigger: Automatic

4. **Configure Build:**
   - Configuration source: **Use a configuration file**
   - Create `apprunner.yaml` in your repo root:

```yaml
version: 1.0
build:
  commands:
    build:
      - echo "Build started on `date`"
      - mvn clean package -DskipTests
      - echo "Build completed on `date`"
      - docker build -t binary-beast-app .
run:
  command: docker run -p 8080:8080 binary-beast-app
  network:
    port: 8080
  env:
    - name: AWS_REGION
      value: us-east-1
    - name: SPRING_PROFILES_ACTIVE
      value: production
```

5. **Configure Service:**
   - Service name: `binary-beast-api`
   - Virtual CPU: 0.25 vCPU
   - Memory: 0.5 GB
   - Environment variables:
     - `AWS_REGION`: `us-east-1`
     - `SPRING_DATASOURCE_URL`: `jdbc:postgresql://database-1.cluster-ct8k2m8uiz5p.ap-southeast-1.rds.amazonaws.com:5432/postgres`
     - `SPRING_DATASOURCE_USERNAME`: `postgres`
     - `SPRING_DATASOURCE_PASSWORD`: `BinaryBeast4`

6. **Configure Security:**
   - Auto-scaling: Default (25-100 instances)
   - Health check: `/actuator/health`

7. **Click "Create & Deploy"**

### Step 3: Wait for Deployment (2-5 minutes)

App Runner will:
- Clone your repository
- Build the application
- Create container
- Deploy to managed infrastructure
- Provide HTTPS endpoint

## 🌐 Your Public Endpoints

After deployment, your API will be available at:
- **Base URL**: `https://xxxxxxxxxx.us-east-1.awsapprunner.com`
- **Chat API**: `https://xxxxxxxxxx.us-east-1.awsapprunner.com/api/bedrock/chat`
- **Health Check**: `https://xxxxxxxxxx.us-east-1.awsapprunner.com/actuator/health`

**Note**: App Runner provides automatic HTTPS with a custom domain!

## 🧪 Test Your Deployment

```bash
# Replace with your actual App Runner URL
URL="https://xxxxxxxxxx.us-east-1.awsapprunner.com"

# Test health endpoint
curl $URL/actuator/health

# Test chat endpoint
curl -X POST $URL/api/bedrock/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello from AWS App Runner!"}'
```

## 🔧 Required Files for App Runner

Create these files in your repository:

### `Dockerfile`
```dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

### `apprunner.yaml`
```yaml
version: 1.0
build:
  commands:
    build:
      - echo "Build started"
      - mvn clean package -DskipTests
      - echo "Build completed"
      - docker build -t binary-beast-app .
run:
  command: docker run -p 8080:8080 binary-beast-app
  network:
    port: 8080
  env:
    - name: AWS_REGION
      value: us-east-1
    - name: SPRING_PROFILES_ACTIVE
      value: production
```

## 🔒 Security Setup (IAM Role)

App Runner creates an IAM role automatically. Add Bedrock permissions:

1. Go to AWS Console → IAM → Roles
2. Find `AppRunnerInstanceRole` (created by App Runner)
3. Attach policies:
   - `AmazonBedrockFullAccess` (or create custom policy below)

**Custom Bedrock Policy** (recommended):
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

## 🔄 Update and Redeploy

**Automatic Deployment:**
1. Push changes to GitHub
2. App Runner automatically rebuilds and deploys
3. Zero downtime deployment

**Manual Deployment:**
1. Go to App Runner console
2. Select your service
3. Click "Deploy" → "Deploy latest commit"

## 💰 Cost Estimate (App Runner)
- **App Runner**: $0.007/hour when running (~$5/month if always on)
- **Compute**: 0.25 vCPU + 0.5 GB = ~$15/month
- **Requests**: $0.0000025 per request (first 100K free)
- **Bedrock API calls**: Pay per request (~$0.00025 per 1K input tokens)
- **Total**: ~$20/month + API usage

**Scaling**: Only pay when your app is processing requests!

## 🚨 Troubleshooting

### **Elastic Beanstalk Issues**

**"Web Page Blocked" Error:**
```bash
# Check if EB is running
eb status
eb health

# Test from command line (bypasses corporate firewall)
curl -I http://your-app.elasticbeanstalk.com/actuator/health

# View application logs
eb logs
```

**Corporate Firewall Solutions via AWS Console:**

### **Solution 1: API Gateway Proxy (Recommended)**
1. **AWS Console** → **API Gateway** → **Create API**
2. **REST API** → **New API** → Name: `binary-beast-proxy`
3. **Actions** → **Create Resource** → Resource Name: `{proxy+}`
4. **Actions** → **Create Method** → **ANY**
5. **Integration Type**: **HTTP Proxy**
6. **Endpoint URL**: `http://your-eb-url.elasticbeanstalk.com/{proxy}`
7. **Deploy API** → **New Stage** → Stage: `prod`
8. **Copy Invoke URL**: `https://xxxxxx.execute-api.us-east-1.amazonaws.com/prod`

### **Solution 2: CloudFront Distribution**
1. **AWS Console** → **CloudFront** → **Create Distribution**
2. **Origin Domain**: `your-eb-url.elasticbeanstalk.com`
3. **Protocol**: **HTTP Only**
4. **Viewer Protocol Policy**: **Redirect HTTP to HTTPS**
5. **Create Distribution**
6. **Use CloudFront URL**: `https://xxxxxx.cloudfront.net`

### **Solution 3: Application Load Balancer**
1. **AWS Console** → **EC2** → **Load Balancers** → **Create**
2. **Application Load Balancer** → **Internet-facing**
3. **Target Group**: Point to your EB environment
4. **Security Group**: Allow HTTP/HTTPS
5. **Use ALB DNS name**

**Common EB Issues:**
- **502 Bad Gateway**: App not starting, check logs
- **Health check fails**: Ensure `/actuator/health` works
- **Bedrock access denied**: Check IAM role `aws-elasticbeanstalk-ec2-role`
- **Database connection**: Verify environment variables

**EB Commands:**
```bash
# View environment info
eb status

# Check application health
eb health --refresh

# View logs
eb logs

# Redeploy
eb deploy

# Terminate (cleanup)
eb terminate
```

### **App Runner Issues**

**View Logs:**
1. Go to App Runner console
2. Select your service
3. Click "Logs" tab
4. View deployment and application logs

**Common Issues:**
- **Build fails**: Check `apprunner.yaml` syntax and Dockerfile
- **Docker build fails**: Ensure Maven builds successfully first
- **Health check fails**: Ensure `/actuator/health` endpoint works
- **Bedrock access denied**: Check IAM role permissions
- **Database connection**: Verify environment variables
- **Java 17 issues**: Use Docker approach since App Runner doesn't support Java 17 runtime

## 🎯 Production Checklist

- [ ] ✅ HTTPS enabled (automatic with App Runner)
- [ ] ✅ Auto-scaling configured (automatic)
- [ ] ✅ Health checks enabled
- [ ] Set up custom domain (optional)
- [ ] Configure CloudWatch alarms
- [ ] Set up database backups
- [ ] Monitor costs and usage
- [ ] Configure CORS if needed for web frontend

## 🚀 Alternative: Elastic Beanstalk (Java 17 Native Support)

If you prefer native Java support without Docker:

```bash
# Install EB CLI
pip install awsebcli

# Initialize and deploy
eb init binary-beast-api --region us-east-1 --platform "Java 17 running on 64bit Amazon Linux 2"
eb create production --instance-type t3.small
eb setenv SERVER_PORT=5000 AWS_REGION=us-east-1
eb deploy
```

**Benefits:**
- Native Java 17 support
- No Docker required
- More configuration options
- Better for complex applications

**Cost**: ~$31/month (always-on EC2 + Load Balancer)

## 🐳 Alternative: ECS Fargate (Serverless Containers)

For true serverless with Java 17:

1. **Push Docker image to ECR**
2. **Create ECS Fargate service**
3. **Use Application Load Balancer**
4. **Auto-scaling based on CPU/memory**

**Cost**: Pay only for running containers (~$20-40/month)