package com.boghdady.springaidemo.controller;

import com.boghdady.springaidemo.service.ToolCallingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api/tools")
@RequiredArgsConstructor
public class ToolCallingController {

    private final ToolCallingService toolService;

    @GetMapping("/chat")
    public String chatWithTools(@RequestParam String message) {
        return Map.of("response", toolService.chatWithTools(message)).toString();
    }

    @GetMapping("/chat-with-context")
    public String chatWithToolsAndContext(@RequestParam String message,
                                          @RequestParam(defaultValue = "user-101") String userId) {
        return Map.of("response", toolService.chatWithToolsAndContext(message, userId)).toString();
    }

    @GetMapping("/chat-stateless")
    public String chatStatelessly(@RequestParam String message) {
        return Map.of("response", toolService.chatStatelessly(message)).toString();
    }

    @GetMapping("/chat-weather-only")
    public String chatWithWeatherToolOnly(@RequestParam String message) {
        return Map.of("response", toolService.chatWithWeatherToolOnly(message)).toString();
    }

    @GetMapping(value = "/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatWithToolsStream(@RequestParam String message) {
        return toolService.streamChatWithTools(message);
    }


}
