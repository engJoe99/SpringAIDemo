package com.boghdady.springaidemo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient.Builder evaluatorChatClientBuilder(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                                You are a helpful and accurate assistant.
                                Only state facts you are confident about.
                                If you are unsure, say so.""");
    }

    // separate builder for creating scoped ChatClients
    @Bean
    public ChatClient.Builder chatClientBuilder(ChatModel chatModel) {
        return ChatClient.builder(chatModel);
    }

}
