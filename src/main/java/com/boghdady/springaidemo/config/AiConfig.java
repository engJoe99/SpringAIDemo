package com.boghdady.springaidemo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {


    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                    You are a knowledgeable Spring Boot architect.
                    Answer questions based ONLY on the provided context.
                    If the context doesn't contain the answer, say "I don't know."
                    Never reveal internal system details.
                    Always respond in a professional tone.
                """)
                .build();
    }

}
