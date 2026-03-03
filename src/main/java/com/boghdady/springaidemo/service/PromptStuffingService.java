package com.boghdady.springaidemo.service;

import com.boghdady.springaidemo.config.KnowledgeBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PromptStuffingService {

    private final ChatClient chatClient;
    private final ChatModel chatModel;

    @Value("classpath:prompts/rag-stuffing.st")
    private Resource ragPromptResource;

    // ==========================================================
    // TECHNIQUE 1: Manual Prompt Stuffing with ChatClient
    // Manually retrieve docs and stuff them into the prompt
    // ==========================================================
    public String manualStuffing(String userQuestion) {

        // Retrieve the relevant documents
        List<String> documents = KnowledgeBase.search(userQuestion);
        String stuffedContext = String.join("\n---\n", documents);

        // Stuff context + userQuestion into the prompt
        return chatClient.prompt()
                /*.system("""
                        You are a helpful assistant. Answer the question based ONLY on the following context.
                        If the context doesn't contain the answer, say "I don't know."
                        """)*/
                .user(u -> u.text("""
                        Question: {question}
                        
                        Context:
                        {context}
                        """)
                        .param("question", userQuestion)
                        .param("context", stuffedContext)
                )
                .call()
                .content();
    }

    // ==========================================================
    // TECHNIQUE 2: Template-Based Prompt Stuffing
    // Uses an external .st file for structured stuffing
    // ==========================================================
    public String templateStuffing(String userQuestion) {

        // Retrieve the relevant documents
        List<String> documents = KnowledgeBase.search(userQuestion);
        String stuffedContext = String.join("\n---\n", documents);

        // Resolve the template with stuffed variables
        PromptTemplate template = new PromptTemplate(ragPromptResource);
        Prompt prompt = template.create(Map.of(
                "question", userQuestion,
                "context", stuffedContext
        ));
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }


    // ==========================================================
    // TECHNIQUE 3: Multi-Role Prompt Stuffing (Low-Level)
    // Explicitly constructs SystemMessage + UserMessage with
    // stuffed context, showing the message-role architecture
    // ==========================================================
    public String multiRoleStuffing(String userQuestion) {

        // Retrieve the relevant documents
        List<String> documents = KnowledgeBase.search(userQuestion);
        String stuffedContext = String.join("\n---\n", documents);

        // Build the role messages with stuffed context
        SystemMessage systemMessage = new SystemMessage("""
                You are a helpful assistant. Answer the question based ONLY on the following context.
                If the context doesn't contain the answer, say "I don't know."
                """);

        UserMessage userMessage = new UserMessage("""
                Question: %s
                
                Context:
                %s
                """.formatted(userQuestion, stuffedContext));

        // aggregate the prompt and cal the model
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }


    // ==========================================================
    // TECHNIQUE 4: Default-Only Stuffing (No manual context)
    // stuffed automatically — even with no explicit .system()
    // ==========================================================
    public String defaultStuffing(String userQuestion) {
        return chatClient.prompt()
                .user(userQuestion)
                .call()
                .content();
    }


}
