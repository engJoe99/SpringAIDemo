package com.boghdady.springaidemo.controller;

import com.boghdady.springaidemo.config.ModelType;
import com.boghdady.springaidemo.service.AiAssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {

    private final AiAssistantService aiService;

    @GetMapping("/explain")
    public ResponseEntity<String> explain(@RequestParam ModelType modelType,
                                          @RequestParam String topic) {
        return ResponseEntity.ok(aiService.explain(modelType, topic));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam ModelType modelType,
                               @RequestParam String topic) {
        return aiService.stream(modelType, topic);
    }




}
