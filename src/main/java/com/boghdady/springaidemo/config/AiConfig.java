package com.boghdady.springaidemo.config;

import com.boghdady.springaidemo.advisor.ContextInjectionAdvisor;
import com.boghdady.springaidemo.advisor.GuardrailAdvisor;
import com.boghdady.springaidemo.advisor.RequestTimingAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean("fullChatClient")
    public ChatClient fullChatClient(ChatClient.Builder builder) {
        return builder
                .defaultAdvisors(
                        new GuardrailAdvisor(),
                        new ContextInjectionAdvisor(),
                        new RequestTimingAdvisor(),
                        new SimpleLoggerAdvisor()
                )
                .build();
    }


    @Bean("simpleChatClient")
    public ChatClient simpleChatClient(ChatClient.Builder builder) {
        return builder.build();
    }

}
