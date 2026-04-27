package com.boghdady.springaidemo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean("evaluatorChatClient")
    public ChatClient evaluatorChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                                You are a helpful and accurate assistant.
                                Only state facts you are confident about.
                                If you are unsure, say so.""")
                .build();
    }

    @Bean("ragClient")
    public ChatClient ragClient(ChatClient.Builder builder ,VectorStore vectorStore) {
        return builder
                .defaultSystem("""
                            Answer only based on the provided context.
                            If the context is not sufficient, say so.
                            Do NOT make up information""")
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder()
                                .topK(3)
                                .similarityThreshold(0.65)
                                .build())
                        .build())
                .build();
    }

    // separate builder for creating scoped ChatClients
    @Bean
    public ChatClient.Builder chatClientBuilder(ChatModel chatModel) {
        return ChatClient.builder(chatModel);
    }

}
