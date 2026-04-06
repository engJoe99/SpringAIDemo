package com.boghdady.springaidemo.dto;

import java.util.Map;

public record DocumentEntry(
        String content,
        Map<String, Object> metadata
) { }
