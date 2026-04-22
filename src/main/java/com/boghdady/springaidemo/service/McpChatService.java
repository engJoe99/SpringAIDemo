package com.boghdady.springaidemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class McpChatService {

    private final ChatClient chatClient;
    private final ConversationSessionManager sessionManager;

    public String chat(String sessionId, String userMessage) {
        log.info("[session={}] User: {}", sessionId, userMessage);
        Instant start = Instant.now();

        var userMsg = new UserMessage(userMessage);
        sessionManager.addMessage(sessionId, userMsg);

        try {
            String response = chatClient.prompt()
                    .messages(sessionManager.getHistory(sessionId))
                    .call()
                    .content();

            sessionManager.addMessage(sessionId, new AssistantMessage(response));

            log.info("[session={}] Response in {}ms",
                    sessionId, Duration.between(start, Instant.now()).toMillis());

            return response;

        } catch (Exception e) {
            log.error("[session={}] Chat failed: {}", sessionId, e.getMessage(), e);
            throw new RuntimeException("Chat error: " + e.getMessage(), e);
        }
    }

    public String createSession() {
        String sessionId = sessionManager.createSession();
        log.info("Created session: {}", sessionId);
        return sessionId;
    }

    public void clearSession(String sessionId) {
        sessionManager.clearSession(sessionId);
        log.info("Cleared session: {}", sessionId);
    }


}
