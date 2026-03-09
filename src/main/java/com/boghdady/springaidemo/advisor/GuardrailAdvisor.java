package com.boghdady.springaidemo.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;

import java.util.List;

@Slf4j
public class GuardrailAdvisor implements CallAdvisor {

    private static final List<String> BLOCKED_TERMS = List.of(
            "hack", "exploit", "bypass security", "ignore instructions"
    );


    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

        String userMsg = chatClientRequest.prompt().getContents().toLowerCase();

        for(String blocked : BLOCKED_TERMS) {
            if(userMsg.contains(blocked)) {
                log.info("[Guardrail] Blocked Request: {}", blocked);

                // SHORT-CIRCUIT: Return response WITHOUT calling the model
                // This is how SafeGuardAdvisor works internally
                AssistantMessage blockedMsg = new AssistantMessage("I'm sorry, but I'm not allowed to discuss that topic.");

                ChatResponse response = new ChatResponse(List.of(new Generation(blockedMsg)));
                return new ChatClientResponse(response, chatClientRequest.context());
            }
        }

        log.info("[Guardrail] Allowed Request: {}", userMsg);
        return callAdvisorChain.nextCall(chatClientRequest);
    }

    @Override
    public String getName() {
        return "GuardrailAdvisor";
    }

    @Override
    public int getOrder() {
        // order=50: runs VERY EARLY, before memory (100) and RAG (200)
        // Safety should block bad requests before any processing
        return 50;
    }
}
