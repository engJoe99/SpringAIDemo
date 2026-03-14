package com.boghdady.springaidemo.model;

import java.util.List;

public record SentimentAnalysis (
        String sentiment,
        double confidence,
        List<String> keyPhrases,
        String explanation
) {}
