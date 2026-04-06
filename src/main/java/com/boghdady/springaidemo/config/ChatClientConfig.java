package com.boghdady.springaidemo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ChatClientConfig {

    @Bean
    @Primary
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("You are a helpful assistant")
                .build();
    }

    @Bean("ragChatClient")
    public ChatClient ragChatClient(ChatClient.Builder builder, VectorStore vectorStore) {

        return builder
                .defaultSystem("""
                        You are a helpful company knowledge assistant.
                        Answer questions using ONLY the provided context.
                        If the context doesn't contain the answer, clearly state:
                        "I don't have that information in the knowledge base."
                        Be concise and accurate. Always cite which part of the context
                        your answer comes from.
                        """)
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder()
                                .topK(5)
                                .similarityThreshold(0.65).build())
                        .build())
                .build();
    }

}
