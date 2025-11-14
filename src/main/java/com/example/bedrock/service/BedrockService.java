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
            return new ChatResponse(completion, modelId);
        } catch (Exception e) {
            throw new RuntimeException("Error invoking Bedrock model: " + e.getMessage(), e);
        }
    }
}
