package com.boghdady.springaidemo.controller;

import com.boghdady.springaidemo.model.ChatRequest;
import com.boghdady.springaidemo.model.ChatResponse;
import com.boghdady.springaidemo.model.SessionResponse;
import com.boghdady.springaidemo.service.McpChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class McpChatController {

    private final McpChatService mcpChatService;

    @PostMapping("/sessions")
    public ResponseEntity<SessionResponse> createSession() {
        return ResponseEntity.ok(new SessionResponse(mcpChatService.createSession()));
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        long start = System.currentTimeMillis();

        String sessionId = request.sessionId() != null
                ? request.sessionId()
                : mcpChatService.createSession();

        String response = mcpChatService.chat(sessionId, request.message());

        return ResponseEntity.ok(new ChatResponse(
                sessionId,
                response,
                System.currentTimeMillis() - start
        ));
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Map<String, String>> clearSession(@PathVariable String sessionId) {
        mcpChatService.clearSession(sessionId);
        return ResponseEntity.ok(Map.of("status", "cleared", "sessionId", sessionId));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "mcp-chat-ollama"));
    }

}
