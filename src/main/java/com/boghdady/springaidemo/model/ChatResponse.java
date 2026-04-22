package com.boghdady.springaidemo.model;


public record ChatResponse(
        String sessionId,
        String response,
        long processingTimeMs
) { }
