package com.boghdady.springaidemo.service;

import com.boghdady.springaidemo.advisor.ContextInjectionAdvisor;
import com.boghdady.springaidemo.advisor.GuardrailAdvisor;
import com.boghdady.springaidemo.advisor.RequestTimingAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdvisorDemoService {

    private final ChatClient fullChatClient;
    private final ChatClient simpleChatClient;

    public AdvisorDemoService(@Qualifier("fullChatClient") ChatClient fullChatClient,
                              @Qualifier("simpleChatClient") ChatClient simpleChatClient) {

        this.fullChatClient = fullChatClient;
        this.simpleChatClient = simpleChatClient;
    }

    //  FULL ADVISOR CHAIN (defaultAdvisors)
    //  Guardrail → ContextInjection → Timing → Logger
    public String chatWithAllAdvisors(String message) {
        return fullChatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    // NO ADVISORS (direct model access)
    public String chatWithNoAdvisors(String message) {
        return simpleChatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    // simpleChatClient has NO default advisors,
    // but we ADD a timing advisor just for this request
    public String chatPerRequestAdvisor(String message) {
        return simpleChatClient.prompt()
                .user(message)
                .advisors(new RequestTimingAdvisor())
                .call()
                .content();
    }


    // Send a message with blocked terms to demonstrate
    // the advisor chain being short-circuited
    public String chatBlockedRequest(String message) {
        return fullChatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    // Ask about "advisor" or "chatclient" or "tool" to trigger
    // the ContextInjectionAdvisor's knowledge base
    public String chatWithContext(String message) {
        return simpleChatClient.prompt()
                .user(message)
                .advisors(new ContextInjectionAdvisor())
                .call()
                .content();
    }

}
