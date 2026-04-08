package com.boghdady.springaidemo.config;

import com.boghdady.springaidemo.tools.CalculatorTool;
import com.boghdady.springaidemo.tools.UserLookupTool;
import com.boghdady.springaidemo.tools.WeatherTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {


    @Bean("multiToolChatClient")
    public ChatClient multiToolChatClient(ChatClient.Builder builder,
                                          WeatherTool weatherTool,
                                         CalculatorTool calculatorTool,
                                         UserLookupTool userLookupTool ) {

        return builder
                .defaultSystem("""
                    You are a helpful assistant with access to tools.
                    Use tools when the user asks for weather, calculations,
                    or user information. Always explain what tool you used
                    and why in your response.
                    """)
                .defaultTools(weatherTool, calculatorTool, userLookupTool)
                .build();
    }

    @Bean("statelessChatClient")
    public ChatClient statelessChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                    You are a helpful assistant that does not have access to any tools.
                    Answer questions to the best of your ability without using any tools.
                    """)
                .build();
    }

    @Bean("weatherOnlyChatClient")
    public ChatClient waetherOnlyChatClient(ChatClient.Builder builder, WeatherTool weatherTool) {
        return builder
                .defaultSystem("""
                    You are a helpful assistant with access to a weather tool.
                    Use the weather tool when the user asks for weather information.
                    Always explain what tool you used and why in your response.
                    """)
                .defaultTools(weatherTool)
                .build();
    }


}
