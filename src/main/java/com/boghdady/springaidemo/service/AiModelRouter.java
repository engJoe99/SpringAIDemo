package com.boghdady.springaidemo.service;

import com.boghdady.springaidemo.config.ModelType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AiModelRouter {


    private final ChatClient openAiClient;
    private final ChatClient ollamaAiClient;

    public AiModelRouter(@Qualifier("openai") ChatClient openAiClient,
                         @Qualifier("ollama") ChatClient ollamaAiClient) {
        this.openAiClient = openAiClient;
        this.ollamaAiClient = ollamaAiClient;
    }

    public ChatClient resolve(ModelType type) {
        return switch (type) {
            case OPENAI -> openAiClient;
            case OLLAMA -> ollamaAiClient;
        };
    }

}
