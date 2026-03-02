package com.boghdady.springaidemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiChatService {

    private final ChatModel chatModel;
    private final ChatClient chatClient;

    // =====================================================
    // 1. DEFAULT SYSTEM MESSAGE (from application.yml)
    //    No .system() call → uses "default-system" globally
    // =====================================================
    public String chatWithDefaults(String userMsg) {
        return chatClient.prompt()
                .user(userMsg)
                .call()
                .content();
    }


    // =====================================================
    // 2. OVERRIDE SYSTEM MESSAGE PER REQUEST
    //    Explicit .system() overrides the default
    // =====================================================
    public String chatWithOverride(String userMsg) {
        return chatClient.prompt()
                .system("You are a strict interviewer. " +
                        "Ask follow-up questions. " +
                        "Be critical and concise.")
                .user(userMsg)
                .call()
                .content();
    }


    // =====================================================
    // 3. EXPLICIT MESSAGE ROLES (low-level with ChatModel)
    //    Demonstrates SystemMessage, UserMessage directly
    // =====================================================
    public String chatWithExplicitRoles(String userMsg) {
        SystemMessage systemMessage = new SystemMessage("You are a database expert. " +
                "Always relate answers to SQL and JPA.");

        UserMessage userMessage = new UserMessage(userMsg);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }

    // =====================================================
    // 4. PROMPT TEMPLATE (from .st file)
    //    Parameterized, reusable, version-controlled
    // classpath root = src/main/resources
    // =====================================================
    public String chatWithTemplate(String topic, String level) {
        PromptTemplate template = new PromptTemplate("classpath:prompts/explain-topic.st");
        Prompt prompt = template.create(Map.of(
                "framework", "Spring Boot",
                "topic", topic,
                "level", level
        ));
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }



}
