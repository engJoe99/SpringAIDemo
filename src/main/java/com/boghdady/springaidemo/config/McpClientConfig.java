package com.boghdady.springaidemo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpClientConfig {

    private static final String SYSTEM_PROMPT = """
            You are a helpful AI assistant with access to the following tools:
            
            1. get_current_weather   — Use when asked about weather in any location
            2. get_country_info      — Use when asked about a country (capital, population, currency, etc.)
            3. fetch                 — Use when asked to read or summarize content from a URL
            4. read_file / list_dir  — Use when asked to read or list local files
            
            RULES:
            - Always use a tool when the question requires real-time or external data
            - For weather, you need latitude and longitude — derive them from the city name
            - Never guess or fabricate data — use tools to get accurate information
            - If a tool fails, explain what went wrong and suggest an alternative
            - Be concise and structured in your responses
            - When using weather: always state the source (Open-Meteo, free API)
            """;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 ToolCallbackProvider mcpTools) {
        return builder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(mcpTools)
                .build();
    }
}
