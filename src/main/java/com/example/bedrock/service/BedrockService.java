package com.example.bedrock.service;

import com.example.bedrock.dto.ChatRequest;
import com.example.bedrock.dto.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BedrockService {

    private final BedrockRuntimeClient bedrockClient;
    private final String modelId;
    private final ObjectMapper objectMapper;

    public BedrockService(BedrockRuntimeClient bedrockClient,
                         @Value("${aws.bedrock.model-id}") String modelId) {
        this.bedrockClient = bedrockClient;
        this.modelId = modelId;
        this.objectMapper = new ObjectMapper();
    }

    public ChatResponse chat(ChatRequest request) {
        int maxRetries = 5;
        int retryCount = 0;
        long baseDelayMs = 1000; // Start with 1 second
        
        while (retryCount < maxRetries) {
            try {
                // Build messages array for Claude 3 Messages API
                List<Map<String, Object>> messages = new ArrayList<>();
                
                // Add conversation history
                if (request.getConversationHistory() != null && !request.getConversationHistory().isEmpty()) {
                    for (ChatRequest.Message msg : request.getConversationHistory()) {
                        Map<String, Object> message = new HashMap<>();
                        message.put("role", msg.getRole());
                        
                        List<Map<String, String>> content = new ArrayList<>();
                        Map<String, String> textBlock = new HashMap<>();
                        textBlock.put("type", "text");
                        textBlock.put("text", msg.getContent());
                        content.add(textBlock);
                        message.put("content", content);
                        
                        messages.add(message);
                    }
                }
                
                // Add current message
                Map<String, Object> currentMessage = new HashMap<>();
                currentMessage.put("role", "user");
                List<Map<String, String>> content = new ArrayList<>();
                Map<String, String> textBlock = new HashMap<>();
                textBlock.put("type", "text");
                textBlock.put("text", request.getMessage());
                content.add(textBlock);
                currentMessage.put("content", content);
                messages.add(currentMessage);

                // Create the request body for Claude 3 Messages API
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("anthropic_version", "bedrock-2023-05-31");
                requestBody.put("max_tokens", 1000);
                requestBody.put("messages", messages);
                requestBody.put("temperature", 0.7);
                requestBody.put("top_p", 0.9);

                String requestBodyJson = objectMapper.writeValueAsString(requestBody);

                // Create the invoke model request
                InvokeModelRequest invokeRequest = InvokeModelRequest.builder()
                        .modelId(modelId)
                        .body(SdkBytes.fromString(requestBodyJson, StandardCharsets.UTF_8))
                        .build();

                // Invoke the model
                InvokeModelResponse invokeResponse = bedrockClient.invokeModel(invokeRequest);

                // Parse the response (Claude 3 Messages API format)
                String responseBody = invokeResponse.body().asString(StandardCharsets.UTF_8);
                JsonNode responseJson = objectMapper.readTree(responseBody);
                
                // Claude 3 returns content as an array
                JsonNode contentArray = responseJson.get("content");
                StringBuilder responseText = new StringBuilder();
                if (contentArray != null && contentArray.isArray()) {
                    for (JsonNode contentItem : contentArray) {
                        if (contentItem.has("text")) {
                            responseText.append(contentItem.get("text").asText());
                        }
                    }
                }
                
                String completion = responseText.toString().trim();
                
                // Build updated conversation history
                List<ChatRequest.Message> updatedHistory = new ArrayList<>();
                if (request.getConversationHistory() != null) {
                    updatedHistory.addAll(request.getConversationHistory());
                }
                // Add user message
                updatedHistory.add(new ChatRequest.Message("user", request.getMessage()));
                // Add assistant response
                updatedHistory.add(new ChatRequest.Message("assistant", completion));
                
                return new ChatResponse(completion, modelId, updatedHistory);
                
            } catch (Exception e) {
                // Check if it's a 429 error (Too Many Requests)
                String errorMessage = e.getMessage();
                boolean isRateLimitError = errorMessage != null && (
                    errorMessage.contains("429") || 
                    errorMessage.contains("Too many requests") ||
                    errorMessage.contains("Throttling"));
                
                if (isRateLimitError) {
                    retryCount++;
                    if (retryCount >= maxRetries) {
                        throw new RuntimeException("Error invoking Bedrock model: Too many requests after " + maxRetries + " retries. " + errorMessage, e);
                    }
                    
                    // Exponential backoff: 1s, 2s, 4s, 8s, 16s
                    long delayMs = baseDelayMs * (long) Math.pow(2, retryCount - 1);
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                } else {
                    // For non-rate-limit errors, throw immediately
                    throw new RuntimeException("Error invoking Bedrock model: " + errorMessage, e);
                }
            }
        }
        
        throw new RuntimeException("Failed to invoke Bedrock model after " + maxRetries + " retries");
    }
}
