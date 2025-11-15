# Binary Beast - AI-Powered Banking Assistant

A comprehensive Spring Boot application that provides intelligent banking assistance using AWS Bedrock AI models with RAG (Retrieval-Augmented Generation) capabilities. The application serves as a finance advisor for Aeon Bank Malaysia, leveraging customer data and AI to provide personalized banking insights.

## Architecture Overview

This application combines multiple AWS services to deliver a sophisticated AI banking assistant:
- **AWS Bedrock** for AI model inference (Claude 3 Haiku & Titan Embeddings)
- **Amazon RDS PostgreSQL 15.12** with pgvector extension for vector similarity search
- **AWS Elastic Beanstalk** for scalable application deployment
- **RAG Implementation** for context-aware responses using customer data

## Prerequisites

1. **Java 17+** installed
2. **Maven 3.6+** installed
3. **AWS Account** with the following services enabled:
   - AWS Bedrock (Claude 3 Haiku & Titan Embeddings access)
   - Amazon RDS PostgreSQL 15.12
   - AWS Elastic Beanstalk
4. **AWS Credentials** configured

## AWS Services Used

### 1. AWS Bedrock Models
- **Claude 3 Haiku** (`anthropic.claude-3-haiku-20240307-v1:0`) - Primary chat model for conversational AI
- **Titan Embeddings** (`amazon.titan-embed-text-v1`) - Text embedding generation for RAG functionality

### 2. Amazon RDS PostgreSQL 15.12
- **Database**: PostgreSQL 15.12 with pgvector extension
- **Vector Search**: Enables similarity search for RAG implementation
- **Connection**: `database-1.cluster-ct8k2m8uiz5p.ap-southeast-1.rds.amazonaws.com:5432`

### 3. AWS Elastic Beanstalk
- **Deployment Platform**: Java 17 Corretto platform
- **Auto-scaling**: Configured for production workloads
- **Health Monitoring**: Spring Boot Actuator integration

## Database Schema

The application manages comprehensive banking data with vector embeddings:

### Core Entities
- **Customers** - Customer profiles with embedded content for AI search
- **Accounts** - Bank account information with transaction history
- **Transactions** - Financial transactions with contextual embeddings
- **Products** - Banking products and services
- **Customer Relationships** - Relationship mapping between customers
- **Digital Feature Usage** - Digital banking feature analytics
- **Logins** - Authentication and session management

### Vector Search Capabilities
Each entity includes:
- `content_text` - Searchable text representation
- `embedding` - 1536-dimensional vector for similarity search

## API Endpoints

### Chat Endpoints

#### POST `/api/bedrock/chat`
Basic chat with Claude 3 Haiku model.

```json
{
  "message": "What are my account options?",
  "conversationHistory": []
}
```

#### POST `/api/bedrock/chat/rag`
RAG-enhanced chat with customer data context.

```json
{
  "message": "Show me my recent transactions and suggest improvements",
  "conversationHistory": []
}
```

### Session Management

#### DELETE `/api/bedrock/chat/{sessionId}`
Clear conversation history for a session.

### RAG Management

#### POST `/api/bedrock/rag/generate-embeddings`
Generate embeddings for existing data (initial setup).

### Health Check

#### GET `/actuator/health`
Application health status for load balancer monitoring.

## Configuration

### Application Properties

```properties
# Application
spring.application.name=bedrock-app
server.port=8085

# AWS Configuration
aws.region=us-east-1
aws.bedrock.model-id=anthropic.claude-3-haiku-20240307-v1:0
aws.bedrock.embedding-model-id=amazon.titan-embed-text-v1

# RAG Configuration
rag.max-retrieval-results=5

# PostgreSQL Database
spring.datasource.url=jdbc:postgresql://database-1.cluster-ct8k2m8uiz5p.ap-southeast-1.rds.amazonaws.com:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=BinaryBeast4
```

## Building and Running

### Local Development

```bash
# Build the application
mvn clean install

# Run locally
mvn spring-boot:run
```

The application will start on `http://localhost:8085`

### AWS Elastic Beanstalk Deployment

```bash
# Build deployment package
./deploy.sh

# Deploy using EB CLI
eb init
eb create production
eb deploy
```

## RAG (Retrieval-Augmented Generation)

The application implements sophisticated RAG capabilities:

### 1. Embedding Generation
- Uses AWS Bedrock Titan Embeddings model
- Generates 1536-dimensional vectors for text content
- Stores embeddings in PostgreSQL with pgvector extension

### 2. Vector Similarity Search
- Searches across customers, accounts, transactions, and relationships
- Retrieves top 5 most relevant context items
- Uses cosine similarity for vector matching

### 3. Context Enhancement
- Combines retrieved context with user queries
- Maintains conversation history for continuity
- Provides personalized banking advice

## Example Usage

### Basic Chat
```bash
curl -X POST http://localhost:8085/api/bedrock/chat \
  -H "Content-Type: application/json" \
  -H "X-Session-Id: user123" \
  -d '{
    "message": "What banking services do you offer?"
  }'
```

### RAG-Enhanced Chat
```bash
curl -X POST http://localhost:8085/api/bedrock/chat/rag \
  -H "Content-Type: application/json" \
  -H "X-Session-Id: user123" \
  -d '{
    "message": "Analyze my spending patterns and suggest savings strategies"
  }'
```

### Generate Embeddings
```bash
curl -X POST http://localhost:8085/api/bedrock/rag/generate-embeddings
```

## Project Structure

```
src/main/java/com/example/bedrock/
├── BedrockApplication.java              # Main Spring Boot application
├── controller/
│   ├── BedrockController.java           # Main API endpoints
│   └── HealthController.java            # Health check endpoint
├── service/
│   ├── BedrockService.java              # Claude 3 Haiku integration
│   ├── EmbeddingService.java            # Titan Embeddings service
│   ├── RAGService.java                  # RAG implementation
│   └── ConversationService.java         # Session management
├── entity/
│   ├── Customer.java                    # Customer entity with embeddings
│   ├── Account.java                     # Account entity
│   ├── Transaction.java                 # Transaction entity
│   ├── CustomerRelationship.java        # Relationship mapping
│   ├── Product.java                     # Banking products
│   ├── DigitalFeatureUsage.java         # Feature analytics
│   └── Login.java                       # Authentication
├── repository/
│   └── [Entity]Repository.java          # JPA repositories with vector search
├── dto/
│   ├── ChatRequest.java                 # Request DTO
│   └── ChatResponse.java                # Response DTO
└── config/
    └── BedrockConfig.java               # AWS Bedrock configuration

src/main/resources/
├── db/changelog/                        # Liquibase database migrations
│   ├── changes/
│   │   ├── 001-create-pgvector-extension.xml
│   │   ├── 002-create-customers-table.xml
│   │   └── [other migration files]
│   └── db.changelog-master.xml
├── application.properties               # Main configuration
├── application-production.properties    # Production overrides
└── application-apprunner.properties     # App Runner configuration

.ebextensions/
└── 01-java.config                       # Elastic Beanstalk configuration

.elasticbeanstalk/
└── config.yml                           # EB CLI configuration
```

## Error Handling

The API returns appropriate HTTP status codes:
- `200 OK`: Successful request
- `400 Bad Request`: Invalid request (missing required fields)
- `500 Internal Server Error`: AWS service errors or database issues

Error response format:
```json
{
  "error": "Detailed error message"
}
```

## Monitoring and Logging

- **Spring Boot Actuator**: Health checks and metrics
- **Application Logs**: Comprehensive logging for debugging
- **AWS CloudWatch**: Automatic log aggregation in production
- **Database Monitoring**: RDS performance insights

## Security Considerations

- **IAM Roles**: Proper AWS service permissions
- **Database Security**: VPC isolation and encrypted connections
- **API Security**: Input validation and error handling
- **Credential Management**: Environment-based configuration

## Cost Optimization

- **Claude 3 Haiku**: Cost-effective model choice (~$0.00025 per 1K tokens)
- **Elastic Beanstalk**: Auto-scaling based on demand
- **RDS**: Right-sized instance for workload
- **Embedding Caching**: Reduces Titan API calls

## Troubleshooting

1. **AWS Credentials Error**: Verify IAM permissions for Bedrock and RDS
2. **Database Connection**: Check VPC security groups and connection strings
3. **Bedrock Access**: Ensure model access is enabled in your AWS region
4. **Vector Search Issues**: Verify pgvector extension is installed
5. **Embedding Generation**: Check Titan Embeddings model availability

## Development Team

Binary Beast - AI Banking Solutions Team