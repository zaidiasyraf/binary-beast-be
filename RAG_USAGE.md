# RAG (Retrieval Augmented Generation) Usage Guide

This application now supports RAG functionality, which enhances AI responses by retrieving relevant context from the database before generating answers.

## Overview

The RAG system:
1. Generates embeddings for user queries using AWS Bedrock Titan Embeddings model
2. Searches the database for similar records using vector similarity search
3. Retrieves relevant context from customers, accounts, transactions, and relationships
4. Enhances the prompt with retrieved context
5. Generates a response using Claude with the enhanced context

## Prerequisites

1. **AWS Bedrock Access**: Ensure you have access to:
   - Claude model (for chat completion) - already configured
   - Amazon Titan Embeddings model (`amazon.titan-embed-text-v1`) - **NEW**

2. **Database Setup**: 
   - PostgreSQL with pgvector extension installed
   - Database tables created (via Liquibase migrations)
   - Fake data inserted (optional, for testing)

3. **Generate Embeddings**: Before using RAG, you need to generate embeddings for existing data:
   ```bash
   curl -X POST http://localhost:8080/api/bedrock/rag/generate-embeddings
   ```

## API Endpoints

### 1. Chat with RAG

**Endpoint**: `POST /api/bedrock/chat/rag`

**Description**: Chat with AI enhanced by RAG - retrieves relevant context from database before answering.

**Headers**:
- `X-Session-Id` (optional): Session ID for conversation history

**Request Body**:
```json
{
  "message": "Who are the most profitable customers?",
  "conversationHistory": []
}
```

**Response**:
```json
{
  "response": "Based on the database context, the most profitable customers are...",
  "model": "anthropic.claude-3-haiku-20240307-v1:0",
  "conversationHistory": [...]
}
```

**Example**:
```bash
curl -X POST http://localhost:8080/api/bedrock/chat/rag \
  -H "Content-Type: application/json" \
  -H "X-Session-Id: session-123" \
  -d '{
    "message": "Tell me about customers with high disengagement risk",
    "conversationHistory": []
  }'
```

### 2. Generate Embeddings for Existing Data

**Endpoint**: `POST /api/bedrock/rag/generate-embeddings`

**Description**: Generates and stores embeddings for all existing records in the database that don't have embeddings yet.

**Response**:
```json
{
  "message": "Embeddings generated successfully for existing data"
}
```

**Example**:
```bash
curl -X POST http://localhost:8080/api/bedrock/rag/generate-embeddings
```

**Note**: This endpoint processes all records and may take some time. It's safe to run multiple times - it only processes records without embeddings.

### 3. Regular Chat (without RAG)

**Endpoint**: `POST /api/bedrock/chat`

**Description**: Regular chat without RAG - uses only conversation history, no database context.

## Configuration

### Application Properties

```properties
# AWS Bedrock Configuration
aws.region=us-east-1
aws.bedrock.model-id=anthropic.claude-3-haiku-20240307-v1:0
aws.bedrock.embedding-model-id=amazon.titan-embed-text-v1

# RAG Configuration
rag.max-retrieval-results=5  # Number of similar records to retrieve per entity type
```

### AWS Bedrock Regions

**Important**: Amazon Titan Embeddings model availability varies by region:
- `us-east-1` (N. Virginia) - ✅ Available
- `us-west-2` (Oregon) - ✅ Available
- `ap-southeast-1` (Singapore) - Check availability

If you get errors about model not found, try changing `aws.region` to a region where Titan Embeddings is available.

## How RAG Works

1. **Query Embedding**: User query is converted to a vector embedding using Titan Embeddings
2. **Vector Search**: Database is searched for similar records using cosine similarity
3. **Context Retrieval**: Top N similar records are retrieved from:
   - Customers
   - Accounts
   - Transactions
   - Customer Relationships
4. **Prompt Enhancement**: Retrieved context is added to the prompt
5. **Response Generation**: Claude generates response using enhanced prompt

## Example Queries

### Customer Analysis
```bash
# Find high-value customers
curl -X POST http://localhost:8080/api/bedrock/chat/rag \
  -H "Content-Type: application/json" \
  -d '{"message": "Who are the top 5 most profitable customers?"}'

# Disengagement risk
curl -X POST http://localhost:8080/api/bedrock/chat/rag \
  -H "Content-Type: application/json" \
  -d '{"message": "Which customers are at risk of disengagement?"}'
```

### Transaction Analysis
```bash
# Business transactions
curl -X POST http://localhost:8080/api/bedrock/chat/rag \
  -H "Content-Type: application/json" \
  -d '{"message": "What are the recent business transactions between SMEs?"}'
```

### Product Usage
```bash
# Product adoption
curl -X POST http://localhost:8080/api/bedrock/chat/rag \
  -H "Content-Type: application/json" \
  -d '{"message": "Which customers use savings pot feature?"}'
```

## Troubleshooting

### Error: "Model not found"
- Check if Titan Embeddings is available in your AWS region
- Update `aws.bedrock.embedding-model-id` if using a different model
- Ensure Bedrock access is enabled for Titan Embeddings

### Error: "No results found"
- Run the generate embeddings endpoint first
- Check if database has data with `content_text` fields populated
- Verify vector similarity search queries are working

### Slow Performance
- Reduce `rag.max-retrieval-results` value
- Ensure database indexes are created (Liquibase migrations should handle this)
- Check if embeddings are generated (NULL embeddings won't be searched)

## Database Schema

The RAG system uses the following fields:
- `content_text`: Text content used for embedding generation
- `embedding`: Vector embedding stored as PostgreSQL vector type

Entities with embeddings:
- `customers`
- `accounts`
- `transactions`
- `customer_relationships`

## Next Steps

1. **Generate Embeddings**: Run the generate embeddings endpoint
2. **Test RAG**: Try some queries using the `/chat/rag` endpoint
3. **Monitor Performance**: Check response times and adjust `rag.max-retrieval-results` if needed
4. **Customize Context**: Modify `RAGService.buildEnhancedPrompt()` to customize how context is formatted

