package com.example.bedrock.service;

import com.example.bedrock.dto.ChatRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConversationService {
    
    private final Map<String, List<ChatRequest.Message>> conversations = new ConcurrentHashMap<>();
    
    /**
     * Save conversation history for a session
     */
    public void saveConversation(String sessionId, List<ChatRequest.Message> history) {
        if (sessionId != null && !sessionId.isEmpty() && history != null) {
            conversations.put(sessionId, new ArrayList<>(history));
        }
    }
    
    /**
     * Get conversation history for a session
     */
    public List<ChatRequest.Message> getConversation(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return new ArrayList<>();
        }
        return conversations.getOrDefault(sessionId, new ArrayList<>());
    }
    
    /**
     * Clear conversation history for a session
     */
    public void clearConversation(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            conversations.remove(sessionId);
        }
    }
    
    /**
     * Check if a session exists
     */
    public boolean hasSession(String sessionId) {
        return sessionId != null && !sessionId.isEmpty() && conversations.containsKey(sessionId);
    }
}

