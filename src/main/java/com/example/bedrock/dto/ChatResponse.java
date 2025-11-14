package com.example.bedrock.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatResponse {
    private String response;
    private String model;
    private List<ChatRequest.Message> conversationHistory;

    public ChatResponse(String response, String model) {
        this.response = response;
        this.model = model;
    }
    
    public ChatResponse(String response, String model, List<ChatRequest.Message> conversationHistory) {
        this.response = response;
        this.model = model;
        this.conversationHistory = conversationHistory;
    }
}

