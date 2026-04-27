package com.boghdady.springaidemo.service;

import com.boghdady.springaidemo.model.ChatResult;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

// Every response is evaluated before returning to the response
@Service
public class GuardrailChatService {

    private static final Logger log = LoggerFactory.getLogger(GuardrailChatService.class);

    private final ChatClient chatClient;
    private final RelevancyEvaluator evaluator;
    private final Counter evalPassedCounter;
    private final Counter evalFailedCounter;
    private final Counter fallbackCounter;
    private final Timer responseTimer;

    private static final float THRESHOLD = 0.75f;
    private final static int MAX_ATTEMPTS = 2;

    public GuardrailChatService(@Qualifier("evaluatorChatClient") ChatClient chatClient,
                                RelevancyEvaluator evaluator,
                                MeterRegistry registry) {

        this.chatClient = chatClient;
        this.evaluator = evaluator;
        this.evalPassedCounter = Counter.builder("ai.eval.passed")
                .register(registry);
        this.evalFailedCounter = Counter.builder("ai.eval.failed")
                .register(registry);
        this.fallbackCounter = Counter.builder("ai.fallback.served")
                .register(registry);
        this.responseTimer = Timer.builder("ai.response.time")
                .register(registry);
    }

    public ChatResult chat(String message) {
        return responseTimer.record(() -> {

            for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
                    String response = chatClient.prompt(message).call().content();

                EvaluationResponse eval = evaluator.evaluate(new EvaluationRequest(message, response));
                log.info("[EVAL] attempt={} score={} pass={} q='{}'", attempt, eval.getScore(), eval.isPass(), message);

                if (eval.isPass() && eval.getScore() >= THRESHOLD) {
                    evalPassedCounter.increment();
                    return new ChatResult(response, eval.getScore(), true, attempt, eval.getFeedback());
                }

                evalFailedCounter.increment();
            }
            fallbackCounter.increment();
            return new ChatResult(
                    "I'm unable to provide a confident answer. " +
                            "Please rephrase or contact support.",
                    0f, false, MAX_ATTEMPTS, "All attempts failed");
        });
    }

}
