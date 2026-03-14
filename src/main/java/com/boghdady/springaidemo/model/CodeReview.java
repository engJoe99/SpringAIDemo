package com.boghdady.springaidemo.model;

import java.util.List;

public record CodeReview(
        int score,
        List<String> issues,
        List<String> suggestions,
        String overallVerdict
) {}
