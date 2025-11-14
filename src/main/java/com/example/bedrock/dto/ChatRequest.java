package com.example.bedrock.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class ChatRequest {
    private String message;
    private List<Message> conversationHistory = new ArrayList<>();

    @Data
    public static class Message {
        private String role; // "user" or "assistant"
        private String content;

        public Message() {}

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}

