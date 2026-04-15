package com.boghdady.springaidemo.model;

import java.time.Instant;

public record ToolResult(
        boolean success,
        String data,
        String error,
        Instant timestamp
) {

    public static ToolResult ok(String data) {
        return new ToolResult(true, data, null, Instant.now());
    }

    public static ToolResult error(String error) {
        return new ToolResult(false, null, error, Instant.now());
    }

    @Override
    public String toString() {
        return success ? data : "Error: " + error;
    }


}
