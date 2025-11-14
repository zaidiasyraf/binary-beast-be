package com.example.bedrock.service;

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
public class EmbeddingService {

    private final BedrockRuntimeClient bedrockClient;
    private final String embeddingModelId;
    private final ObjectMapper objectMapper;

    public EmbeddingService(BedrockRuntimeClient bedrockClient,
                           @Value("${aws.bedrock.embedding-model-id:amazon.titan-embed-text-v1}") String embeddingModelId) {
        this.bedrockClient = bedrockClient;
        this.embeddingModelId = embeddingModelId;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Generate embedding vector for the given text using AWS Bedrock Titan Embeddings
     * @param text The text to generate embedding for
     * @return List of floats representing the embedding vector
     */
    public List<Float> generateEmbedding(String text) {
        try {
            // Build request body for Titan Embeddings model
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputText", text);

            String requestBodyJson = objectMapper.writeValueAsString(requestBody);

            // Create the invoke model request
            InvokeModelRequest invokeRequest = InvokeModelRequest.builder()
                    .modelId(embeddingModelId)
                    .body(SdkBytes.fromString(requestBodyJson, StandardCharsets.UTF_8))
                    .build();

            // Invoke the model
            InvokeModelResponse invokeResponse = bedrockClient.invokeModel(invokeRequest);

            // Parse the response
            String responseBody = invokeResponse.body().asString(StandardCharsets.UTF_8);
            JsonNode responseJson = objectMapper.readTree(responseBody);

            // Extract embedding vector
            JsonNode embeddingNode = responseJson.get("embedding");
            if (embeddingNode == null || !embeddingNode.isArray()) {
                throw new RuntimeException("Invalid embedding response from Bedrock");
            }

            List<Float> embedding = new ArrayList<>();
            for (JsonNode value : embeddingNode) {
                embedding.add((float) value.asDouble());
            }

            return embedding;

        } catch (Exception e) {
            throw new RuntimeException("Error generating embedding: " + e.getMessage(), e);
        }
    }

    /**
     * Convert embedding vector to PostgreSQL vector format string
     * @param embedding List of floats representing the embedding
     * @return String in format "[0.1,0.2,0.3,...]"
     */
    public String embeddingToVectorString(List<Float> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(embedding.get(i));
        }
        sb.append("]");
        return sb.toString();
    }
}

