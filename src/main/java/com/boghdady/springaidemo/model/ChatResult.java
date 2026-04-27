package com.boghdady.springaidemo.model;

public record ChatResult(
        String answer,
        float confidence,
        boolean passed,
        int attempts,
        String feedback
) {}
