package com.boghdady.springaidemo.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

@Slf4j
public class ChatLoggingAdvisor implements CallAdvisor {

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

        // ===== BEFORE: Request phase =====
        int messageCount = chatClientRequest.prompt().getInstructions().size();
        log.info("\n┌───────────────────────────────────────────┐");
        log.info("│         REQUEST PHASE  (order={})         │", getOrder());
        log.info("├───────────────────────────────────────────┤");
        log.info("│  Total messages in prompt: {}             │", messageCount);
        log.info("├───────────────────────────────────────────┤");
        chatClientRequest.prompt().getInstructions().forEach(msg ->
                log.info("│  [{}]  {}...",
                        msg.getMessageType(),
                        msg.getText().substring(0, Math.min(60, msg.getText().length()))));
        log.info("└───────────────────────────────────────────┘");

        // Proceed with the next advisor in the chain
        ChatClientResponse clientResponse = callAdvisorChain.nextCall(chatClientRequest);

        // ===== AFTER: Response phase =====
        // raw LLM output
        String content = clientResponse.chatResponse().getResult().getOutput().getText();
        log.info("\n┌───────────────────────────────────────────┐");
        log.info("│        RESPONSE PHASE  (order={})         │", getOrder());
        log.info("├───────────────────────────────────────────┤");
        log.info("│  Response length : {} chars               │", content.length());
        log.info("│  Preview         : {}...",
                content.substring(0, Math.min(80, content.length())));
        log.info("└───────────────────────────────────────────┘");

        return clientResponse;
    }

    @Override
    public String getName() {
        return "ChatLoggingAdvisor";
    }

    @Override
    public int getOrder() {
        return 900;
    }
}
