package com.boghdady.springaidemo.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

@Slf4j
public class RequestTimingAdvisor implements CallAdvisor {
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

        // ═══ BEFORE: Runs before the model call ═══
        long startTime = System.currentTimeMillis();
        String userMsg = chatClientRequest.prompt().getContents();
        log.info("RequestTimingAdvisor: Starting request for user message: {}", userMsg);

        // ═══ DELEGATE: Pass to the next advisor in the chain ═══
        ChatClientResponse response = callAdvisorChain.nextCall(chatClientRequest);

        // ═══ AFTER: Runs after the model returns ═══
        long duration = System.currentTimeMillis() - startTime;
        log.info("Request Completed in: " + duration + "ms");

        return response;
    }

    @Override
    public String getName() {
        return "RequestTimingAdvisor";
    }

    @Override
    public int getOrder() {
        // order=900: runs AFTER memory/RAG advisors (100, 200)
        // but BEFORE logger (1000)
        return 900;
    }
}
