package com.boghdady.springaidemo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    /*
    * the builder automatically picks the default-system and
    * default-options from application.yaml
    */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }

}
