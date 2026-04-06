package com.boghdady.springaidemo.service;

import com.boghdady.springaidemo.rag.PostRetrievalService;
import com.boghdady.springaidemo.rag.PreRetrievalService;
import com.boghdady.springaidemo.rag.RetrievalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RagOrchestrationService {

    private final RetrievalService retrievalService;
    private final PreRetrievalService preRetrievalService;
    private final PostRetrievalService postRetrievalService;
    private final ChatClient ragChatClient;
    private final ChatClient chatClient;

    public RagOrchestrationService(RetrievalService retrievalService, PreRetrievalService preRetrievalService,
                                   PostRetrievalService postRetrievalService,
                                   @Qualifier("ragChatClient") ChatClient ragChatClient,
                                   @Qualifier("chatClient") ChatClient chatClient) {

        this.retrievalService = retrievalService;
        this.preRetrievalService = preRetrievalService;
        this.postRetrievalService = postRetrievalService;
        this.ragChatClient = ragChatClient;
        this.chatClient = chatClient;
    }

    // Simple RAG orchestration without any advanced techniques
    public String simpleRag(String question) {
        return ragChatClient.prompt()
                .user(question)
                .call()
                .content();
    }

    // Advanced RAG orchestration using multiple techniques:
    public RagResponse advancedRag(String question) {

        // Step 1: Pre-retrieval - optimize query
        String optimizedQuery = preRetrievalService.askWithQueryRewriting(question);

        // Step 2: Retrieval - get candidate documents
        List<Document> rawDocs = retrievalService.retrieve(optimizedQuery, 6, 0.60);

        // Step 3: Post-retrieval - apply one or more techniques
        // Example: Context Compression
        List<Document> filteredDocs = postRetrievalService.compressDocuments(question, rawDocs);

        // Step 4: Build context and generate answer
        String context = filteredDocs.stream()
                .map(document -> "[Source: %s]".formatted(document.getText()))
                .collect(Collectors.joining("\n---\n"));

        String answer = ragChatClient.prompt()
                .user("""
                  You are a knowledgeable company assistant.
                  Answer using ONLY the context below. Cite the source for each claim.
                  If not in context, say "I don't have that information."

                  CONTEXT:
                  %s

                  QUESTION: %s
                  """.formatted(context, question))
                .call()
                .content();

        return new RagResponse(question, optimizedQuery, answer, filteredDocs);
    }


    public record RagResponse(
            String originalQuestion,
            String optimizedQuery,
            String answer,
            List<Document> sources
    ){}

}
