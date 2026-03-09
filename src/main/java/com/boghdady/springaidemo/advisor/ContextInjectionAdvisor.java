package com.boghdady.springaidemo.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContextInjectionAdvisor implements CallAdvisor {

    // Simulated knowledge base (in production: VectorStore)
    private static final Map<String, List<String>> KNOWLEDGE_BASE = Map.of(
            "advisor", List.of(
                    "Advisors in Spring AI are interceptors that modify prompts and process responses.",
                    "Advisors implement the Chain of Responsibility pattern, ordered by getOrder().",
                    "Built-in advisors include MessageChatMemoryAdvisor, QuestionAnswerAdvisor, and SimpleLoggerAdvisor."
            ),
            "chatclient", List.of(
                    "ChatClient is the high-level fluent API that orchestrates the advisor chain.",
                    "ChatClient configures advisors at build time via defaultAdvisors() or at runtime via .advisors().",
                    "ChatClient delegates execution to ChatModel after all advisors have processed the request."
            ),
            "tool", List.of(
                    "Tools (Function Calling) are actions the LLM decides to invoke during generation.",
                    "Unlike Advisors, Tools are visible to the LLM via tool descriptions in the prompt.",
                    "Advisors provide context transparently; Tools provide actions the LLM controls."
            )
    );

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

        String userMsg = chatClientRequest.prompt().getContents();
        String normalizedUserMsg = userMsg.toLowerCase();

        List<String> relevantDocs = KNOWLEDGE_BASE.entrySet().stream()
                .filter(entry -> normalizedUserMsg.contains(entry.getKey()))
                .flatMap(entry -> entry.getValue().stream())
                .toList();

        if (relevantDocs.isEmpty()) {
            log.info("[ContextInjectionAdvisor] No context found for message: {}", userMsg);
            return callAdvisorChain.nextCall(chatClientRequest);
        }

        String stuffedContext = String.join("\n---\n", relevantDocs);

        log.info("[ContextInjectionAdvisor] Injecting {} context entries for message: {}", relevantDocs.size(), userMsg);

        SystemMessage contextMsg = new SystemMessage("""
                Use the following context to answer the user's question:
                %s

                Answer using the context when it is relevant.
                If the context does not help, say: I don't have enough context.
                """.formatted(stuffedContext));

        List<Message> modifiedMessages = new ArrayList<>();
        modifiedMessages.addAll(chatClientRequest.prompt().getInstructions());
        modifiedMessages.add(contextMsg);
        modifiedMessages.add(new UserMessage(userMsg));

        Prompt modifiedPrompt = new Prompt(modifiedMessages, chatClientRequest.prompt().getOptions());

        ChatClientRequest modifiedRequest = new ChatClientRequest(
                modifiedPrompt,
                chatClientRequest.context()
        );

        return callAdvisorChain.nextCall(modifiedRequest);
    }

    @Override
    public String getName() {
        return "ContextInjectionAdvisor";
    }

    @Override
    public int getOrder() {
        // order=200: same as QuestionAnswerAdvisor
        // runs AFTER memory (100), BEFORE logger (1000)
        return 200;
    }
}
