package com.boghdady.springaidemo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EvaluationConfig {

    // to check if the LLM response is relevant to the user question
    @Bean
    public RelevancyEvaluator relevancyEvaluator(
            @Qualifier("evaluatorChatClientBuilder") ChatClient.Builder builder) {

        return RelevancyEvaluator.builder()
                .chatClientBuilder(builder)
                .build();
    }

}
