package com.example.bedrock.controller;

import com.example.bedrock.dto.ChatRequest;
import com.example.bedrock.dto.ChatResponse;
import com.example.bedrock.service.BedrockService;
import com.example.bedrock.service.ConversationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bedrock")
public class BedrockController {

    private final BedrockService bedrockService;
    private final ConversationService conversationService;

    public BedrockController(BedrockService bedrockService, ConversationService conversationService) {
        this.bedrockService = bedrockService;
        this.conversationService = conversationService;
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(
            @RequestBody ChatRequest request,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        try {
            // Validate request
            if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Message field is required"));
            }

            // If session ID provided, load conversation history
            if (sessionId != null && !sessionId.isEmpty()) {
                List<ChatRequest.Message> history = conversationService.getConversation(sessionId);
                if (history != null && !history.isEmpty()) {
                    // Merge with any history provided in request (request takes precedence)
                    if (request.getConversationHistory() == null || request.getConversationHistory().isEmpty()) {
                        request.setConversationHistory(history);
                    }
                }
            }

            ChatResponse response = bedrockService.chat(request);

            // Save updated conversation history if session ID provided
            if (sessionId != null && !sessionId.isEmpty() && response.getConversationHistory() != null) {
                conversationService.saveConversation(sessionId, response.getConversationHistory());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/chat/{sessionId}")
    public ResponseEntity<?> clearConversation(@PathVariable String sessionId) {
        try {
            conversationService.clearConversation(sessionId);
            return ResponseEntity.ok(Map.of("message", "Conversation cleared for session: " + sessionId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

