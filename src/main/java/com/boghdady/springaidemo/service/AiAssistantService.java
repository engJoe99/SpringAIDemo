package com.boghdady.springaidemo.service;

import com.boghdady.springaidemo.config.ModelType;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class AiAssistantService {

    private final AiModelRouter router;

    public String explain(ModelType modelType, String topic) {

        ChatClient chatClient = router.resolve(modelType);

        return chatClient.prompt()
                .user("Explain " + topic + " clearly")
                .call()
                .content();
    }

    public Flux<String> stream(ModelType modelType, String topic) {

        ChatClient chatClient = router.resolve(modelType);

        return chatClient.prompt()
                .user("Explain " + topic + " in detail")
                .stream()
                .content();
    }



}
