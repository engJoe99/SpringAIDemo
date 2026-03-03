package com.boghdady.springaidemo.config;

import java.util.List;
import java.util.Map;

/**
 * Simulates a knowledge base / vector store
 */
public class KnowledgeBase {

    private static final Map<String, List<String>> DOCUMENTS = Map.of(
            "spring ai", List.of(
                    "Spring AI is a framework that provides consistent abstractions over multiple AI providers like OpenAI, Ollama, and AWS Bedrock.",
                    "Spring AI supports ChatModel, ChatClient, Advisors, Prompt Templates, and Vector Store integrations.",
                    "ChatClient is the recommended high-level API. ChatModel is the low-level provider-specific engine."
            ),
            "prompt stuffing", List.of(
                    "Prompt stuffing is the deliberate injection of all required context into a single prompt.",
                    "LLMs are stateless, so all context (rules, documents, history) must be re-injected every request.",
                    "Spring AI automates prompt stuffing via Advisors like QuestionAnswerAdvisor and MessageChatMemoryAdvisor."
            ),
            "chatclient", List.of(
                    "ChatClient is a high-level fluent facade over ChatModel, similar to WebClient over HTTP.",
                    "ChatClient supports .system(), .user(), .tools(), defaults, and advisor chains.",
                    "ChatClient delegates execution to ChatModel which translates to provider-specific API calls."
            )
    );

    /**
     * Simulates a vector similarity search.
     * Returns documents matching any keyword in the query.
     */
    public static List<String> search(String query) {
        String lowerQuery = query.toLowerCase();
        return DOCUMENTS.entrySet().stream()
                .filter(entry -> lowerQuery.contains(entry.getKey()))
                .flatMap(entry -> entry.getValue().stream())
                .toList();
    }

}
