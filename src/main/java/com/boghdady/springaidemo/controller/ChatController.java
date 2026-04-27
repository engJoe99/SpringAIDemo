package com.boghdady.springaidemo.controller;

import com.boghdady.springaidemo.service.EvaluatedRagService;
import com.boghdady.springaidemo.service.GuardrailChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")

public class ChatController {

    private final EvaluatedRagService ragService;
    private final GuardrailChatService chatService;

    public ChatController(EvaluatedRagService ragService,
                          GuardrailChatService chatService) {
        this.ragService = ragService;
        this.chatService = chatService;
    }

    // ── Guardrailed Chat ──
    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, String> req) {
        var result = chatService.chat(req.get("message"));
        return ResponseEntity.ok(Map.of(
                "answer", result.answer(),
                "confidence", result.confidence(),
                "passed", result.passed(),
                "attempts", result.attempts()));
    }

    // ── RAG with Grounding Evaluation ──
    @PostMapping("/rag")
    public ResponseEntity<?> rag(@RequestBody Map<String, String> req) {
        var result = ragService.ask(req.get("question"));
        return ResponseEntity.ok(Map.of(
                "answer", result.answer(),
                "confidence", result.confidence(),
                "grounded", result.grounded(),
                "docsUsed", result.docsUsed()));
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP",
                "evaluator", "active");
    }

}
