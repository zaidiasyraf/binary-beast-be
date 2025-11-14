# Curl Examples for Bedrock Chat API with Session Management

## Overview

The API now supports server-side session management. You can use a session ID to maintain conversation context across multiple requests.

## Endpoints

- `POST /api/bedrock/chat` - Send a chat message (with optional session management)
- `DELETE /api/bedrock/chat/{sessionId}` - Clear conversation history for a session

## Session Management

Include the `X-Session-Id` header in your requests to maintain conversation context. The server will automatically:
- Load previous conversation history for the session
- Save updated conversation history after each response

## Examples

### 1. First Request (Start New Conversation)

**Without Session ID (Stateless):**
```bash
curl -X POST http://localhost:8080/api/bedrock/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "What is the capital of France?"
  }'
```

**Response:**
```json
{
  "response": "The capital of France is Paris.",
  "model": "anthropic.claude-3-5-sonnet-20240620-v1:0",
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
}
```

### 2. Continue Conversation with Session ID

**With Session ID (Stateful):**
```bash
# First message - start a session
curl -X POST http://localhost:8080/api/bedrock/chat \
  -H "Content-Type: application/json" \
  -H "X-Session-Id: my-session-123" \
  -d '{
    "message": "What is the capital of France?"
  }'
```

**Response:**
```json
{
  "response": "The capital of France is Paris.",
  "model": "anthropic.claude-3-5-sonnet-20240620-v1:0",
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
}
```

**Second message - continues the conversation:**
```bash
curl -X POST http://localhost:8080/api/bedrock/chat \
  -H "Content-Type: application/json" \
  -H "X-Session-Id: my-session-123" \
  -d '{
    "message": "What did I ask you before?"
  }'
```

**Response:**
```json
{
  "response": "You asked me what the capital of France is. I told you it's Paris.",
  "model": "anthropic.claude-3-5-sonnet-20240620-v1:0",
  "conversationHistory": [
    {
      "role": "user",
      "content": "What is the capital of France?"
    },
    {
      "role": "assistant",
      "content": "The capital of France is Paris."
    },
    {
      "role": "user",
      "content": "What did I ask you before?"
    },
    {
      "role": "assistant",
      "content": "You asked me what the capital of France is. I told you it's Paris."
    }
  ]
}
```

### 3. Multi-Turn Conversation Example

```bash
# Message 1
curl -X POST http://localhost:8080/api/bedrock/chat \
  -H "Content-Type: application/json" \
  -H "X-Session-Id: conversation-001" \
  -d '{
    "message": "What is artificial intelligence?"
  }'

# Message 2 (AI remembers previous context)
curl -X POST http://localhost:8080/api/bedrock/chat \
  -H "Content-Type: application/json" \
  -H "X-Session-Id: conversation-001" \
  -d '{
    "message": "What are its main applications?"
  }'

# Message 3 (AI remembers entire conversation)
curl -X POST http://localhost:8080/api/bedrock/chat \
  -H "Content-Type: application/json" \
  -H "X-Session-Id: conversation-001" \
  -d '{
    "message": "Can you give me examples of each?"
  }'
```

### 4. Manual Conversation History (Override Session)

You can still provide conversation history manually in the request body. This will override the session history:

```bash
curl -X POST http://localhost:8080/api/bedrock/chat \
  -H "Content-Type: application/json" \
  -H "X-Session-Id: my-session-123" \
  -d '{
    "message": "Tell me more about it",
    "conversationHistory": [
      {
        "role": "user",
        "content": "What is machine learning?"
      },
      {
        "role": "assistant",
        "content": "Machine learning is a subset of AI..."
      }
    ]
  }'
```

### 5. Clear Conversation History

Delete a session's conversation history:

```bash
curl -X DELETE http://localhost:8080/api/bedrock/chat/my-session-123
```

**Response:**
```json
{
  "message": "Conversation cleared for session: my-session-123"
}
```

### 6. Multiple Independent Sessions

You can have multiple independent conversations using different session IDs:

```bash
# Session 1 - Math conversation
curl -X POST http://localhost:8080/api/bedrock/chat \
  -H "Content-Type: application/json" \
  -H "X-Session-Id: math-session" \
  -d '{"message": "What is 2+2?"}'

# Session 2 - History conversation (independent)
curl -X POST http://localhost:8080/api/bedrock/chat \
  -H "Content-Type: application/json" \
  -H "X-Session-Id: history-session" \
  -d '{"message": "When was World War 2?"}'

# Continue Session 1 (remembers math context)
curl -X POST http://localhost:8080/api/bedrock/chat \
  -H "Content-Type: application/json" \
  -H "X-Session-Id: math-session" \
  -d '{"message": "What about 3+3?"}'
```

## Session ID Best Practices

1. **Generate Unique IDs**: Use UUIDs or unique identifiers for session IDs
   ```bash
   # Generate UUID in bash
   SESSION_ID=$(uuidgen)
   echo $SESSION_ID
   ```

2. **Reuse Session IDs**: Use the same session ID for related conversations

3. **Clear Old Sessions**: Periodically clear unused sessions to free memory

4. **Session ID Format**: Any string is valid (e.g., `user-123`, `uuid-abc-def`, `chat-001`)

## Notes

- **Session Storage**: Currently stored in-memory (lost on server restart)
- **No Expiration**: Sessions persist until explicitly cleared or server restarts
- **Thread-Safe**: Multiple requests with the same session ID are handled safely
- **Optional**: Session ID is optional - you can still use stateless requests

## Example Script

Here's a complete example script that demonstrates a conversation:

```bash
#!/bin/bash

SESSION_ID="my-chat-$(date +%s)"
API_URL="http://localhost:8080/api/bedrock/chat"

echo "Starting conversation with session: $SESSION_ID"
echo ""

# Message 1
echo "User: What is Python?"
curl -X POST $API_URL \
  -H "Content-Type: application/json" \
  -H "X-Session-Id: $SESSION_ID" \
  -d '{"message": "What is Python?"}' | jq -r '.response'
echo ""

# Message 2
echo "User: What are its main features?"
curl -X POST $API_URL \
  -H "Content-Type: application/json" \
  -H "X-Session-Id: $SESSION_ID" \
  -d '{"message": "What are its main features?"}' | jq -r '.response'
echo ""

# Message 3
echo "User: Can you give me an example?"
curl -X POST $API_URL \
  -H "Content-Type: application/json" \
  -H "X-Session-Id: $SESSION_ID" \
  -d '{"message": "Can you give me an example?"}' | jq -r '.response'
echo ""

echo "Conversation complete!"
```

