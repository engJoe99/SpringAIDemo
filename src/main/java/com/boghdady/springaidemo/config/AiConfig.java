package com.boghdady.springaidemo.config;

import com.boghdady.springaidemo.advisor.ChatLoggingAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    // ==================================================
    //  ChatMemory Beans — Different window sizes
    // ==================================================
    @Bean("standardMemory")
    ChatMemory standardMemory(JdbcChatMemoryRepository repository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(10)
                .build();
    }

    @Bean("smallMemory")
    ChatMemory smallMemory(JdbcChatMemoryRepository repository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(4)
                .build();
    }

    // =========================================================
    //  ChatClient Beans - Different advisors and system prompts
    // =========================================================
    @Bean("statelessClient")
    ChatClient statelessClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("You are a helpful assistant")
                .build();
    }

    @Bean("messageMemoryClient")
    ChatClient messageMemoryClient(ChatClient.Builder builder, ChatMemory standardMemory) {
        return builder
                .defaultSystem("""
                        You are a helpful coding assistant.
                        Remember the user's language preferences, frameworks, and goals across turns.
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(standardMemory).build(), new ChatLoggingAdvisor())
                .build();
    }


    @Bean("promptMemoryClient")
    ChatClient promptMemoryClient(ChatClient.Builder builder, ChatMemory smallMemory) {
        return builder
                .defaultSystem("""
                        You are a customer support agent for a software company.
                        Use the conversation context to give consistent, personalized help.
                        """)
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(smallMemory).build(), new ChatLoggingAdvisor())
                .build();
    }


}
