package com.boghdady.springaidemo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    @Qualifier("openai")
    public ChatClient openAiClient(OpenAiChatModel model) {
        return ChatClient.builder(model)
                .defaultSystem("You Are Senior Java Developer")
                .build();
    }

    @Bean
    @Qualifier("ollama")
    public ChatClient ollamaClient(OllamaChatModel model) {
        return ChatClient.builder(model)
                .defaultSystem("You Are Senior Java Developer")
                .build();
    }

}
