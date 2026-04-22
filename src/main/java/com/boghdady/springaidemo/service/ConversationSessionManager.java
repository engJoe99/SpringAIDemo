package com.boghdady.springaidemo.service;

import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConversationSessionManager {

    // messages per session
    private static final int MAX_HISTORY_SIZE = 40;

    // sessionId → list of messages (user → assistant turns)
    private final Map<String, List<Message>> sessions = new ConcurrentHashMap<>();

    public String createSession() {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, new ArrayList<>());
        return sessionId;
    }

    public List<Message> getHistory(String sessionId) {
        return sessions.computeIfAbsent(sessionId, k -> new ArrayList<>());
    }

    public void addMessage(String sessionId, Message message) {
        List<Message> history = getHistory(sessionId);
        history.add(message);

        // Trim history to avoid token overflow
        if (history.size() > MAX_HISTORY_SIZE) {
            history.subList(0, history.size() - MAX_HISTORY_SIZE).clear();
        }
    }

    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }


}
