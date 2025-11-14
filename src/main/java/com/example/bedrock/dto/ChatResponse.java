package com.example.bedrock.dto;

import lombok.Data;

@Data
public class ChatResponse {
    private String response;
    private String model;

    public ChatResponse(String response, String model) {
        this.response = response;
        this.model = model;
    }
}

