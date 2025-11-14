# Spring Boot AWS Bedrock Application

A Spring Boot application that provides a REST endpoint to interact with AWS Bedrock's Claude model for chat completion.

## Prerequisites

Before running this application, ensure you have:

1. **Java 17+** installed
2. **Maven 3.6+** installed
3. **AWS Account** with Bedrock access enabled
4. **AWS Credentials** configured

## Setup Instructions

### 1. AWS Credentials Configuration

Create or update your AWS credentials file at `~/.aws/credentials`:

```ini
[default]
aws_access_key_id = YOUR_ACCESS_KEY_ID
aws_secret_access_key = YOUR_SECRET_ACCESS_KEY
```

### 2. AWS Bedrock Access

1. Log in to the AWS Console
2. Navigate to AWS Bedrock service
3. Request access to the Claude model (anthropic.claude-v2)
4. Wait for access approval (this may take some time)

### 3. Configure Application

Update `src/main/resources/application.properties` if needed:

```properties
# Change the region if your Bedrock access is in a different region
aws.region=us-east-1

# Change the model ID if using a different Claude model
aws.bedrock.model-id=anthropic.claude-v2
```

## Building and Running

### Build the application:

```bash
mvn clean install
```

### Run the application:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoint

### POST `/api/bedrock/chat`

Send a chat message to AWS Bedrock Claude model.

#### Request Body

```json
{
  "message": "Hello, how are you?",
  "conversationHistory": []
}
```

**Fields:**
- `message` (required): The user's message/prompt
- `conversationHistory` (optional): Array of previous messages for context
  ```json
  [
    {
      "role": "user",
      "content": "Previous user message"
    },
    {
      "role": "assistant",
      "content": "Previous assistant response"
    }
  ]
  ```

#### Response

```json
{
  "response": "I'm doing well, thank you! How can I assist you today?",
  "model": "anthropic.claude-v2"
}
```

#### Example with cURL

**Simple request:**
```bash
curl -X POST http://localhost:8080/api/bedrock/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "What is the capital of France?"
  }'
```

**Request with conversation history:**
```bash
curl -X POST http://localhost:8080/api/bedrock/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "What did I ask you before?",
    "conversationHistory": [
      {
        "role": "user",
        "content": "What is the capital of France?"
      },
      {
        "role": "assistant",
        "content": "The capital of France is Paris."
      }
    ]
  }'
```

## Error Handling

The API returns appropriate HTTP status codes:
- `200 OK`: Successful request
- `400 Bad Request`: Invalid request (e.g., missing message field)
- `500 Internal Server Error`: Error communicating with AWS Bedrock

Error response format:
```json
{
  "error": "Error message description"
}
```

## Troubleshooting

1. **AWS Credentials Error**: Ensure your `~/.aws/credentials` file is properly configured
2. **Access Denied**: Verify that your AWS account has access to Bedrock and the Claude model
3. **Region Mismatch**: Ensure the region in `application.properties` matches where you have Bedrock access
4. **Model Not Found**: Verify the model ID in `application.properties` matches an available model in your region

## Project Structure

```
src/main/java/com/example/bedrock/
  ├── BedrockApplication.java          # Main Spring Boot application
  ├── controller/
  │   └── BedrockController.java       # REST controller
  ├── service/
  │   └── BedrockService.java          # Bedrock service layer
  ├── dto/
  │   ├── ChatRequest.java             # Request DTO
  │   └── ChatResponse.java            # Response DTO
  └── config/
      └── BedrockConfig.java           # AWS Bedrock configuration
```

