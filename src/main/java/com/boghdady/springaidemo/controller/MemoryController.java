package com.boghdady.springaidemo.controller;

import com.boghdady.springaidemo.dto.ChatRequest;
import com.boghdady.springaidemo.dto.ChatResponse;
import com.boghdady.springaidemo.service.MemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class MemoryController {

    private final MemoryService memoryService;

    @PostMapping("/stateless")
    public ChatResponse stateless(@RequestBody ChatRequest request) {
        return new ChatResponse("none", memoryService.chatStateless(request.message()));
    }

    @PostMapping("/messageMemory")
    public ChatResponse messageMemory(@RequestBody ChatRequest request) {
        String reply = memoryService.chatWithMessageMemory(request.conversationId(), request.message());
        return new ChatResponse(request.conversationId(), reply);
    }

    @PostMapping("/promptMemory")
    public ChatResponse promptMemory(@RequestBody ChatRequest request) {
        String reply = memoryService.chatWithPromptMemory(request.conversationId(), request.message());
        return new ChatResponse(request.conversationId(), reply);
    }



}
