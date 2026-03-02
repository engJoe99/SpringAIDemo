package com.boghdady.springaidemo.controller;

import com.boghdady.springaidemo.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/chat")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService chatService;

    // 1. Uses default system message from application.yml
    @GetMapping
    public String chat(@RequestParam String msg) {
        return chatService.chatWithDefaults(msg);
    }

    // 2. Overrides system message at runtime
    @GetMapping("/interview")
    public String chatInterview(@RequestParam String msg) {
        return chatService.chatWithOverride(msg);
    }

    // 3. Explicit message roles (low-level)
    @GetMapping("/roles")
    public String chatRoles(@RequestParam String msg) {
        return chatService.chatWithExplicitRoles(msg);
    }

    // 4. Prompt template with variables
    @GetMapping("/template")
    public String chatTemplate(@RequestParam String topic,
                               @RequestParam(defaultValue = "Intermediate") String level) {
        return chatService.chatWithTemplate(topic, level);
    }



}
