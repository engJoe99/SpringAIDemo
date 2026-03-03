package com.boghdady.springaidemo.controller;

import com.boghdady.springaidemo.service.PromptStuffingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/stuff")
@RequiredArgsConstructor
public class PromptStuffingController {

    private final PromptStuffingService stuffingService;

    @GetMapping("/manual")
    public String manual(@RequestParam String question) {
        return stuffingService.manualStuffing(question);
    }

    @GetMapping("/template")
    public String template(@RequestParam String question) {
        return stuffingService.templateStuffing(question);
    }

    @GetMapping("/roles")
    public String roles(@RequestParam String question) {
        return stuffingService.multiRoleStuffing(question);
    }

    @GetMapping("/default")
    public String defaults(@RequestParam String question) {
        return stuffingService.defaultStuffing(question);
    }

}
