package com.boghdady.springaidemo.controller;

import com.boghdady.springaidemo.model.CodeReview;
import com.boghdady.springaidemo.model.SentimentAnalysis;
import com.boghdady.springaidemo.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("ai/api")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    // Simple String Response
    @GetMapping("/simple")
    public Map<String, String> simple(@RequestParam String q) {
        String answer = aiService.askSimple(q);
        return Map.of("question", q,
                "answer", answer);
    }

    // Creative And Precise (Options Override)
    @GetMapping("/creative")
    public Map<String, String> creative(@RequestParam String topic) {
        return Map.of("topic", topic,
                "story", aiService.askCreative(topic));
    }

    @GetMapping("/precise")
    public Map<String, String> precise(@RequestParam String q) {
        return Map.of("question", q,
                "answer", aiService.askPrecise(q));
    }

    // Full ChatResponse With metadata
    @GetMapping("/metadata")
    public Map<String, Object> metadata(@RequestParam String q) {
        ChatResponse response = aiService.askWithMetadata(q);

        var usage = response.getMetadata().getUsage();
        String content = response.getResult().getOutput().getText();
        String finishReason = response.getResult().getMetadata().getFinishReason();

        return Map.of(
                "answer", content,
                "inputTokens", usage.getPromptTokens(),
                "outputTokens", usage.getCompletionTokens(),
                "totalTokens", usage.getTotalTokens(),
                "finishReason", finishReason
        );
    }

    // Structured POJO Responses
    @GetMapping("/sentiment")
    public SentimentAnalysis sentiment(@RequestParam String text) {
        return aiService.analysis(text);
    }

    @GetMapping("/code-review")
    public CodeReview review(@RequestParam String code) {
        return aiService.review(code);
    }

    // Stream
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String q) {
        return aiService.streamAnswer(q);
    }


}
