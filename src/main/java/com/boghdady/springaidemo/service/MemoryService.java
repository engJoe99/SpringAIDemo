package com.boghdady.springaidemo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MemoryService {

    private final ChatClient statelessClient;
    private final ChatClient messageMemoryClient;
    private final ChatClient promptMemoryClient;

    public MemoryService (
            @Qualifier("statelessClient")ChatClient statelessClient,
            @Qualifier("messageMemoryClient")ChatClient messageMemoryClient,
            @Qualifier("promptMemoryClient")ChatClient promptMemoryClient) {

        this.statelessClient = statelessClient;
        this.messageMemoryClient = messageMemoryClient;
        this.promptMemoryClient = promptMemoryClient;

    }

    // ================================================================
    //  1. STATELESS
    //     No conversationId needed — there is no memory to scope.
    //     Advisor chain: (none) → LLM
    // ================================================================
    public String chatStateless(String message) {
        return statelessClient.prompt()
                .user(message)
                .call()
                .content();
    }


    // ================================================================
    //  2. MESSAGE MEMORY (MessageChatMemoryAdvisor)
    //
    //  What happens internally per call:
    //    REQUEST:
    //      1. MemoryAdvisor loads history for conversationId from MySQL
    //      2. Injects past messages as structured Message objects
    //      3. Adds current user message to MySQL
    //      4. LoggingAdvisor logs the FULL prompt (with history)
    //      5. LLM receives prompt with full context
    //    RESPONSE:
    //      6. LoggingAdvisor logs raw LLM output
    //      7. MemoryAdvisor saves assistant response to MySQL
    //      8. MessageWindowChatMemory evicts oldest if > 10 messages
    // ================================================================
    public String chatWithMessageMemory(String convId, String message) {
        return messageMemoryClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, convId))
                .call()
                .content();
    }


    // ================================================================
    //  3. PROMPT MEMORY (PromptChatMemoryAdvisor)
    //
    //  Difference vs MessageMemory:
    //    - History is NOT injected as Message objects
    //    - History is serialized as a TEXT BLOCK appended to system prompt
    //    - The LLM sees ONE large system message containing all history
    //    - Window is 4 messages — eviction is visible quickly
    // ================================================================
    public String chatWithPromptMemory(String convId, String message) {
        return promptMemoryClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, convId))
                .call()
                .content();
    }


}
