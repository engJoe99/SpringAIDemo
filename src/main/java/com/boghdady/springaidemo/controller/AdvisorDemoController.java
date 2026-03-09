package com.boghdady.springaidemo.controller;

import com.boghdady.springaidemo.service.AdvisorDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/advisors")
@RequiredArgsConstructor
public class AdvisorDemoController {

    private final AdvisorDemoService advisorDemoService;

    // Full advisor chain: Guardrail → Context → Timing → Logger
    @GetMapping("/full")
    public String fullChain(@RequestParam String message) {
        return advisorDemoService.chatWithAllAdvisors(message);
    }

    // No advisors: direct LLM access
    @GetMapping("/none")
    public String noAdvisors(@RequestParam String message) {
        return advisorDemoService.chatWithNoAdvisors(message);
    }

    // Per-request advisor: adds timing at runtime only
    @GetMapping("/per-request")
    public String perRequest(@RequestParam String message) {
        return advisorDemoService.chatPerRequestAdvisor(message);
    }

    // Guardrail test: try sending blocked content
    @GetMapping("/guardrail")
    public String guardrail(@RequestParam String message) {
        return advisorDemoService.chatBlockedRequest(message);
    }

    // Context injection test: ask about advisors/chatclient/tools
    @GetMapping("/context")
    public String context(@RequestParam String message) {
        return advisorDemoService.chatWithContext(message);
    }


}
