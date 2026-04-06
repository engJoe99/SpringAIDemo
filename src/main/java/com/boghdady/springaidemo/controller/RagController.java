package com.boghdady.springaidemo.controller;

import com.boghdady.springaidemo.dto.AskRequest;
import com.boghdady.springaidemo.dto.AskResponse;
import com.boghdady.springaidemo.service.RagOrchestrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagOrchestrationService ragService;

    @PostMapping("/ask")
    public ResponseEntity<AskResponse> ask(@RequestBody AskRequest request) {
        String answer = ragService.simpleRag(request.question());
        return ResponseEntity.ok(new AskResponse(answer));
    }

    @PostMapping("/ask/advance")
    public ResponseEntity<RagOrchestrationService.RagResponse> askAdvance(@RequestBody AskRequest request) {
        return ResponseEntity.ok(ragService.advancedRag(request.question()));
    }


}
