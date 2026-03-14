package com.boghdady.springaidemo.service;

import com.boghdady.springaidemo.model.CodeReview;
import com.boghdady.springaidemo.model.SentimentAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;

    public String askSimple(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    public String askCreative(String topic) {
        return chatClient.prompt()
                .system("You are a wildly creative storyteller")
                .user("Tell me a short story about: " + topic)
                .options(ChatOptions.builder()
                        .temperature(0.95)
                        .maxTokens(900)
                        .build())
                .call()
                .content();
    }

    public String askPrecise(String question) {
        return chatClient.prompt()
                .system("You are a factual assistant, Be concise and accurate")
                .user(question)
                .options(ChatOptions.builder()
                        .temperature(0.1)
                        .maxTokens(200)
                        .build())
                .call()
                .content();
    }

    public ChatResponse askWithMetadata(String text) {
        return chatClient.prompt()
                .system("""
                You are a sentiment analysis engine.
                Analyze the user's text and respond with ONLY valid JSON
                matching this structure:
                {
                  "sentiment": "POSITIVE" | "NEGATIVE" | "NEUTRAL",
                  "confidence": 0.0 to 1.0,
                  "keyPhrases": ["phrase1", "phrase2"],
                  "explanation": "brief explanation"
                }
                """)
                .user(text)
                .options(ChatOptions.builder()
                        .temperature(0.1)
                        .build())
                .call()
                .chatResponse();
    }

    public SentimentAnalysis analysis(String text) {
        return chatClient.prompt()
                .system("""
                You are a sentiment analysis engine.
                Analyze the user's text and respond with ONLY valid JSON
                matching this structure:
                {
                  "sentiment": "POSITIVE" | "NEGATIVE" | "NEUTRAL",
                  "confidence": 0.0 to 1.0,
                  "keyPhrases": ["phrase1", "phrase2"],
                  "explanation": "brief explanation"
                }
                """)
                .user(text)
                .options(ChatOptions.builder()
                        .temperature(0.1) // low temp for structured output
                        .build())
                .call()
                .entity(SentimentAnalysis.class);
    }

    public CodeReview review(String code) {
        return chatClient.prompt()
                .system("""
                You are a senior code reviewer.
                Review the code and respond with ONLY valid JSON:
                {
                  "score": 1-10,
                  "issues": ["issue1", ...],
                  "suggestions": ["suggestion1", ...],
                  "overallVerdict": "PASS" | "NEEDS_WORK" | "FAIL"
                }
                """)
                .user(code)
                .options(ChatOptions.builder()
                        .temperature(0.2)
                        .maxTokens(600)
                        .build())
                .call()
                .entity(CodeReview.class);
    }

    public Flux<String> streamAnswer(String question) {
        return chatClient.prompt()
                .user(question)
                .options(ChatOptions.builder()
                        .temperature(0.7)
                        .build())
                .stream()
                .content();
    }

    public Flux<ChatResponse> streamWithMetadata(String question) {
        return chatClient.prompt()
                .user(question)
                .stream()
                .chatResponse();
    }



}
