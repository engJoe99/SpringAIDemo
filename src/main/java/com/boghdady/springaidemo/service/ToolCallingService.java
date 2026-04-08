package com.boghdady.springaidemo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
@Slf4j
public class ToolCallingService {

    private final ChatClient multiToolChatClient;
    private final ChatClient statelessChatClient;
    private final ChatClient waetherOnlyChatClient;

    public ToolCallingService(@Qualifier("multiToolChatClient") ChatClient multiToolChatClient,
                              @Qualifier("statelessChatClient") ChatClient statelessChatClient,
                              @Qualifier("weatherOnlyChatClient") ChatClient waetherOnlyChatClient) {
        this.multiToolChatClient = multiToolChatClient;
        this.statelessChatClient = statelessChatClient;
        this.waetherOnlyChatClient = waetherOnlyChatClient;
    }

    public String chatWithTools(String userMessage) {

        log.info("chatWithTools: {}", userMessage);
        return multiToolChatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }

    /*
    * This method demonstrates how to pass additional context information (like user ID) to the tools during a chat interaction.
    * userId is injected server-side (e.g. from the authenticated user session), LLM can't see it
    * */
    public String chatWithToolsAndContext(String userMessage, String userId) {

        log.info("chatWithToolsAndContext: {} (userId={})", userMessage, userId);
        return multiToolChatClient.prompt()
                .user(userMessage)
                .toolContext(Map.of("userId", userId))
                .call()
                .content();
    }

    public String chatStatelessly(String userMessage) {

        log.info("chatStatelessly: {}", userMessage);
        return statelessChatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }


    public String chatWithWeatherToolOnly(String userMessage) {

        log.info("chatWithWeatherToolOnly: {}", userMessage);
        return waetherOnlyChatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }

    public Flux<String> streamChatWithTools(String userMessage) {

        log.info("streamChatWithTools: {}", userMessage);
        return multiToolChatClient.prompt()
                .user(userMessage)
                .stream()
                .content();
    }

}
