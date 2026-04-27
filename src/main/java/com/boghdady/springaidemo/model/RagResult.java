package com.boghdady.springaidemo.model;

public record RagResult(
        String answer,
        float confidence,
        boolean grounded,
        int docsUsed,
        String feedback
) {}
